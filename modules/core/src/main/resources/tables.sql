CREATE TABLE brands (
    uuid UUID PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE categories (
    uuid UUID PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);