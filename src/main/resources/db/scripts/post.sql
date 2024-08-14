CREATE TABLE post (
    id SERIAL PRIMARY KEY,
    link TEXT UNIQUE NOT NULL,
    created TIMESTAMP NOT NULL
);

CREATE TABLE post
(
    id      SERIAL PRIMARY KEY,
    title   TEXT,
    link    TEXT UNIQUE NOT NULL,
    created TIMESTAMP   NOT NULL,
    description TEXT
);