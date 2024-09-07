CREATE TABLE Users (
   Id INTEGER PRIMARY KEY AUTOINCREMENT,
   Username TEXT NOT NULL UNIQUE,
   HashedPassword TEXT NOT NULL,
   Created DATETIME DEFAULT CURRENT_TIMESTAMP,
   LastLogin DATETIME
);

CREATE TABLE Communities (
   Id INTEGER PRIMARY KEY AUTOINCREMENT,
   Name TEXT NOT NULL UNIQUE,
   Created DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE CommunityUsers (
   Id INTEGER PRIMARY KEY AUTOINCREMENT,
   CommunityId INTEGER NOT NULL,
   UserId INTEGER NOT NULL,
   Created DATETIME DEFAULT CURRENT_TIMESTAMP,
   Permission INTEGER DEFAULT 0,
   FOREIGN KEY (CommunityId) REFERENCES Communities(Id),
   FOREIGN KEY (UserId) REFERENCES Users(Id),
   UNIQUE (CommunityId, UserId)
);


-- test data

-- username is 'admin', password is '123'
INSERT INTO Users (Username, HashedPassword) VALUES ('admin', '$2a$12$2Sll4xwHPA4j0LG4NpJZEeCtidG72KqFpyBzwqwJrkjzIv7MgqGli');
-- username is 'user', password is '123'
INSERT INTO Users (Username, HashedPassword) VALUES ('user', '$2a$12$2Sll4xwHPA4j0LG4NpJZEeCtidG72KqFpyBzwqwJrkjzIv7MgqGli');

