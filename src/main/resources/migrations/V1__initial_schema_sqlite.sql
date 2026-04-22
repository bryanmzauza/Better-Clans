CREATE TABLE IF NOT EXISTS bc_schema_version (
    version INTEGER PRIMARY KEY,
    applied_at INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS bc_clan (
    id           TEXT PRIMARY KEY,
    tag          TEXT NOT NULL UNIQUE,
    name         TEXT NOT NULL UNIQUE,
    leader_uuid  TEXT NOT NULL,
    founded_at   INTEGER NOT NULL,
    level        INTEGER NOT NULL DEFAULT 1,
    xp           INTEGER NOT NULL DEFAULT 0,
    tag_color    TEXT NOT NULL DEFAULT 'white',
    motd         TEXT,
    kills        INTEGER NOT NULL DEFAULT 0,
    deaths       INTEGER NOT NULL DEFAULT 0,
    wins         INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bc_member (
    player_uuid  TEXT PRIMARY KEY,
    clan_id      TEXT NOT NULL,
    role         TEXT NOT NULL,
    joined_at    INTEGER NOT NULL,
    kills        INTEGER NOT NULL DEFAULT 0,
    deaths       INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (clan_id) REFERENCES bc_clan(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_member_clan ON bc_member(clan_id);

CREATE TABLE IF NOT EXISTS bc_relation (
    clan_a_id       TEXT NOT NULL,
    clan_b_id       TEXT NOT NULL,
    type            TEXT NOT NULL,
    established_at  INTEGER NOT NULL,
    PRIMARY KEY (clan_a_id, clan_b_id),
    FOREIGN KEY (clan_a_id) REFERENCES bc_clan(id) ON DELETE CASCADE,
    FOREIGN KEY (clan_b_id) REFERENCES bc_clan(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bc_invite (
    player_uuid   TEXT NOT NULL,
    clan_id       TEXT NOT NULL,
    invited_by    TEXT NOT NULL,
    invited_at    INTEGER NOT NULL,
    PRIMARY KEY (player_uuid, clan_id)
);

CREATE TABLE IF NOT EXISTS bc_duel_log (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    winner_uuid   TEXT NOT NULL,
    loser_uuid    TEXT NOT NULL,
    fought_at     INTEGER NOT NULL,
    duel_type     TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS bc_gladiator_history (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    week_start       INTEGER NOT NULL,
    winner_clan_id   TEXT,
    runner_up_id     TEXT,
    third_place_id   TEXT,
    participants     INTEGER NOT NULL
);
