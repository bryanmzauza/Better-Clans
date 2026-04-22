CREATE TABLE IF NOT EXISTS bc_schema_version (
    version INT PRIMARY KEY,
    applied_at BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bc_clan (
    id           CHAR(36) PRIMARY KEY,
    tag          CHAR(3) NOT NULL UNIQUE,
    name         VARCHAR(32) NOT NULL UNIQUE,
    leader_uuid  CHAR(36) NOT NULL,
    founded_at   BIGINT NOT NULL,
    level        INT NOT NULL DEFAULT 1,
    xp           BIGINT NOT NULL DEFAULT 0,
    tag_color    VARCHAR(16) NOT NULL DEFAULT 'white',
    motd         VARCHAR(255),
    kills        INT NOT NULL DEFAULT 0,
    deaths       INT NOT NULL DEFAULT 0,
    wins         INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bc_member (
    player_uuid  CHAR(36) PRIMARY KEY,
    clan_id      CHAR(36) NOT NULL,
    role         VARCHAR(16) NOT NULL,
    joined_at    BIGINT NOT NULL,
    kills        INT NOT NULL DEFAULT 0,
    deaths       INT NOT NULL DEFAULT 0,
    FOREIGN KEY (clan_id) REFERENCES bc_clan(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_member_clan ON bc_member(clan_id);

CREATE TABLE IF NOT EXISTS bc_relation (
    clan_a_id       CHAR(36) NOT NULL,
    clan_b_id       CHAR(36) NOT NULL,
    type            VARCHAR(8) NOT NULL,
    established_at  BIGINT NOT NULL,
    PRIMARY KEY (clan_a_id, clan_b_id),
    FOREIGN KEY (clan_a_id) REFERENCES bc_clan(id) ON DELETE CASCADE,
    FOREIGN KEY (clan_b_id) REFERENCES bc_clan(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bc_invite (
    player_uuid   CHAR(36) NOT NULL,
    clan_id       CHAR(36) NOT NULL,
    invited_by    CHAR(36) NOT NULL,
    invited_at    BIGINT NOT NULL,
    PRIMARY KEY (player_uuid, clan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bc_duel_log (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    winner_uuid   CHAR(36) NOT NULL,
    loser_uuid    CHAR(36) NOT NULL,
    fought_at     BIGINT NOT NULL,
    duel_type     VARCHAR(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bc_gladiator_history (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    week_start       BIGINT NOT NULL,
    winner_clan_id   CHAR(36),
    runner_up_id     CHAR(36),
    third_place_id   CHAR(36),
    participants     INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
