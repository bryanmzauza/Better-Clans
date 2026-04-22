package com.bryanmz.betterclans.database;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.database.dao.ClanDAO;
import com.bryanmz.betterclans.database.dao.MySQLClanDAO;
import com.bryanmz.betterclans.database.dao.SQLiteClanDAO;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * Orquestra HikariCP + migracoes + escolhe o DAO certo conforme config.
 */
public final class DatabaseManager {

    private final BetterClansPlugin plugin;
    private HikariDataSource dataSource;
    private ClanDAO clanDAO;
    private Type type;

    public enum Type { MYSQL, SQLITE }

    public DatabaseManager(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        FileConfiguration cfg = plugin.getConfig();
        String raw = cfg.getString("database.type", "sqlite").toLowerCase();
        this.type = raw.equals("mysql") ? Type.MYSQL : Type.SQLITE;

        this.dataSource = HikariProvider.create(
                cfg.getConfigurationSection("database"),
                plugin.getDataFolder());

        runMigrations();

        this.clanDAO = type == Type.MYSQL
                ? new MySQLClanDAO(dataSource)
                : new SQLiteClanDAO(dataSource);
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public DataSource dataSource() {
        return dataSource;
    }

    public ClanDAO clans() {
        return clanDAO;
    }

    public Type type() {
        return type;
    }

    private void runMigrations() {
        try (Connection conn = dataSource.getConnection()) {
            ensureVersionTable(conn);
            int current = currentVersion(conn);
            if (current < 1) {
                applyResource(conn, type == Type.MYSQL
                        ? "migrations/V1__initial_schema_mysql.sql"
                        : "migrations/V1__initial_schema_sqlite.sql");
                recordVersion(conn, 1);
                plugin.getLogger().info("Schema V1 aplicado.");
            }
        } catch (SQLException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao aplicar migrations", e);
            throw new IllegalStateException("Nao foi possivel inicializar o banco de dados", e);
        }
    }

    private void ensureVersionTable(Connection conn) throws SQLException {
        String sql = type == Type.MYSQL
                ? "CREATE TABLE IF NOT EXISTS bc_schema_version (version INT PRIMARY KEY, applied_at BIGINT NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
                : "CREATE TABLE IF NOT EXISTS bc_schema_version (version INTEGER PRIMARY KEY, applied_at INTEGER NOT NULL);";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    private int currentVersion(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             var rs = st.executeQuery("SELECT COALESCE(MAX(version), 0) FROM bc_schema_version")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void recordVersion(Connection conn, int version) throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT INTO bc_schema_version(version, applied_at) VALUES(?, ?)")) {
            ps.setInt(1, version);
            ps.setLong(2, System.currentTimeMillis());
            ps.executeUpdate();
        }
    }

    private void applyResource(Connection conn, String resource) throws IOException, SQLException {
        try (InputStream in = plugin.getResource(resource)) {
            if (in == null) throw new IOException("Recurso nao encontrado: " + resource);
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append('\n');
            }
            String script = sb.toString();
            try (Statement st = conn.createStatement()) {
                for (String stmt : script.split(";")) {
                    String trimmed = stmt.trim();
                    if (!trimmed.isEmpty()) st.execute(trimmed);
                }
            }
        }
    }
}
