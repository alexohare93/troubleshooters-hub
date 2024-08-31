CREATE TABLE Users (
   Id INTEGER PRIMARY KEY AUTOINCREMENT,
   Username TEXT NOT NULL UNIQUE,
   HashedPassword TEXT NOT NULL,
   Created DATETIME DEFAULT CURRENT_TIMESTAMP,
   LastLoggedIn DATETIME,
   Permission INTEGER DEFAULT 0
);



-- test data

-- username is 'admin', password is '123' and is superadmin
INSERT INTO Users (Username, HashedPassword, Permission) VALUES ('admin', '$2a$12$2Sll4xwHPA4j0LG4NpJZEeCtidG72KqFpyBzwqwJrkjzIv7MgqGli', 1);

-- username is 'user', password is '123' and is able to read and write events
INSERT INTO Users (Username, HashedPassword, Permission) VALUES ('user', '$2a$12$2Sll4xwHPA4j0LG4NpJZEeCtidG72KqFpyBzwqwJrkjzIv7MgqGli', 6);