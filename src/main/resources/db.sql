DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
    id TEXT PRIMARY KEY,
    version BIGINT NOT NULL,

    name TEXT NOT NULL,
    surname TEXT NOT NULL,
    photo_location TEXT,

    created_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_at TIMESTAMP,
    updated_by TEXT,
    deleted_at TIMESTAMP,
    deleted_by TEXT
);

DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id TEXT PRIMARY KEY,
    version BIGINT NOT NULL,

    name TEXT NOT NULL,
    is_admin BOOLEAN NOT NULL,

    created_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_at TIMESTAMP,
    updated_by TEXT,
    deleted_at TIMESTAMP,
    deleted_by TEXT
);

DROP TABLE IF EXISTS session;
CREATE TABLE session (
    id TEXT PRIMARY KEY,

    user_id TEXT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS event;
CREATE TABLE event (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created_at TIMESTAMP,
    name TEXT NOT NULL,
    origin_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    payload TEXT
);