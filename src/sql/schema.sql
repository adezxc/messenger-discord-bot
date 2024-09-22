CREATE TABLE IF NOT EXISTS dumpfiles (
    hash TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS participants (
    name TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS messages (
    id TEXT PRIMARY KEY,
    sender_name REFERENCES participants(name),
    content TEXT,
    timestamp_ms INTEGER,
    dumpfile_hash REFERENCES dumpfiles(hash),
    UNIQUE(sender_name, content, timestamp_ms)
);

CREATE TABLE IF NOT EXISTS media (
    id TEXT PRIMARY KEY,
    message_id REFERENCES messages(id),
    uri TEXT,
    timestamp_ms INTEGER
);

CREATE TABLE IF NOT EXISTS reactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    actor REFERENCES participants(name),
    message_id REFERENCES messages(id),
    reaction TEXT
);
