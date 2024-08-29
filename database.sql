CREATE TABLE Users (
   Id INTEGER PRIMARY KEY AUTOINCREMENT,
   Username TEXT NOT NULL UNIQUE,
   HashedPassword TEXT NOT NULL,
   Created DATETIME DEFAULT CURRENT_TIMESTAMP,
   LastLoggedIn DATETIME
);



-- test data

-- username is 'admin', password is '123'
INSERT INTO Users (Username, HashedPassword) VALUES ('admin', '$2a$12$2Sll4xwHPA4j0LG4NpJZEeCtidG72KqFpyBzwqwJrkjzIv7MgqGli');