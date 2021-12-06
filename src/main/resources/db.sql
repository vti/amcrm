DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
    id TEXT PRIMARY KEY,
    version BIGINT NOT NULL,

    name TEXT NOT NULL,
    surname TEXT NOT NULL,
    photo_location TEXT,

    created_by TEXT NOT NULL,
    updated_by TEXT,
    deleted_by TEXT
);

DROP TABLE IF EXISTS event;
CREATE TABLE event (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    origin_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    payload TEXT
);

