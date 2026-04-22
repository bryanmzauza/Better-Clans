package com.bryanmz.betterclans.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

/**
 * Constroi o DataSource Hikari para MySQL ou SQLite a partir do config.yml.
 */
public final class HikariProvider {

    private HikariProvider() {}

    public static HikariDataSource create(ConfigurationSection db, File pluginDataFolder) {
        String type = db.getString("type", "sqlite").toLowerCase();
        HikariConfig cfg = new HikariConfig();
        cfg.setPoolName("BetterClans-Hikari");

        if (type.equals("mysql")) {
            ConfigurationSection my = db.getConfigurationSection("mysql");
            String host = my.getString("host", "localhost");
            int port = my.getInt("port", 3306);
            String database = my.getString("database", "betterclans");
            boolean ssl = my.getBoolean("ssl", false);

            cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
            cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=" + ssl + "&useUnicode=true&characterEncoding=utf8");
            cfg.setUsername(my.getString("username", "betterclans"));
            cfg.setPassword(my.getString("password", ""));
            cfg.setMaximumPoolSize(my.getInt("pool-size", 10));
        } else {
            ConfigurationSection sq = db.getConfigurationSection("sqlite");
            String file = sq != null ? sq.getString("file", "betterclans.db") : "betterclans.db";
            File dbFile = new File(pluginDataFolder, file);
            pluginDataFolder.mkdirs();

            cfg.setDriverClassName("org.sqlite.JDBC");
            cfg.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            // SQLite nao suporta pool real; mantemos 1.
            cfg.setMaximumPoolSize(1);
        }

        cfg.setConnectionTimeout(10_000);
        return new HikariDataSource(cfg);
    }
}
