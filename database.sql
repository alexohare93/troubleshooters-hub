CREATE TABLE Images (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    FileName TEXT NOT NULL
);

CREATE TABLE Users (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    Username TEXT NOT NULL UNIQUE,
    HashedPassword TEXT NOT NULL,
    Created DATETIME DEFAULT CURRENT_TIMESTAMP,
    LastLogin DATETIME
);

CREATE TABLE UserProfiles (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    UserId INTEGER NOT NULL,
    DisplayName TEXT NOT NULL,
    Bio TEXT NOT NULL,
    ProfileImageId INTEGER,
    FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE CASCADE
    FOREIGN KEY (ProfileImageId) REFERENCES Images(Id) ON DELETE SET NULL
);

CREATE TABLE Communities (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    Name TEXT NOT NULL,
    Genre TEXT NOT NULL,
    Description TEXT NOT NULL,
    Created DATETIME DEFAULT CURRENT_TIMESTAMP,
    BannerImageId INTEGER,
    IsPrivate INTEGER NOT NULL DEFAULT 0,  -- New isPrivate column to store boolean (0 = false/public, 1 = true/private)
    FOREIGN KEY (BannerImageId) REFERENCES Images(Id) ON DELETE SET NULL
);

CREATE TABLE CommunityMembers (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    CommunityId INTEGER NOT NULL,
    UserId INTEGER NOT NULL,
    Created DATETIME DEFAULT CURRENT_TIMESTAMP,
    Permission INTEGER DEFAULT 0,
    FOREIGN KEY (CommunityId) REFERENCES Communities(Id) ON DELETE CASCADE,
    FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE CASCADE,
    UNIQUE (CommunityId, UserId)
);

CREATE TABLE CommunityPosts (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    CommunityId INTEGER NOT NULL,
    UserId INTEGER NOT NULL,
    Title TEXT NOT NULL,
    Content TEXT NOT NULL,
    Created DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE CASCADE,
    FOREIGN KEY (CommunityId) REFERENCES Communities(Id) ON DELETE CASCADE
);

CREATE TABLE Events (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    CommunityId INTEGER NOT NULL,
    Name TEXT NOT NULL,
    Description TEXT NOT NULL,
    Venue TEXT NOT NULL,
    Capacity INTEGER NOT NULL,
    Scheduled DATETIME NOT NULL,
    Created DATETIME DEFAULT CURRENT_TIMESTAMP,
    BannerImageId INTEGER,
    FOREIGN KEY (CommunityId) REFERENCES Communities(Id) ON DELETE CASCADE,
    FOREIGN KEY (BannerImageId) REFERENCES Images(Id) ON DELETE SET NULL
);

CREATE TABLE Bookings (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    EventId INTEGER NOT NULL,
    UserId INTEGER NOT NULL,
    Created DATETIME DEFAULT CURRENT_TIMESTAMP,
    Permission INTEGER DEFAULT 0,
    FOREIGN KEY (EventId) REFERENCES Events(Id) ON DELETE CASCADE,
    FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE CASCADE,
    UNIQUE (EventId, UserId)
);

CREATE TABLE EventComments (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    EventId INTEGER NOT NULL,
    UserId INTEGER NOT NULL,
    Content TEXT NOT NULL,
    Created DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES Users(Id) ON DELETE CASCADE,
    FOREIGN KEY (EventId) REFERENCES Events(Id) ON DELETE CASCADE
);

-- test data

-- username is 'admin', password is '123'
INSERT INTO Users (Username, HashedPassword) VALUES ('admin', '$2a$12$2Sll4xwHPA4j0LG4NpJZEeCtidG72KqFpyBzwqwJrkjzIv7MgqGli');

-- user profile
INSERT INTO UserProfiles (UserId, DisplayName, Bio) VALUES (1, 'Admin', 'This is an admin account');

-- username is 'user', password is '123'
-- INSERT INTO Users (Username, HashedPassword) VALUES ('user', '$2a$12$2Sll4xwHPA4j0LG4NpJZEeCtidG72KqFpyBzwqwJrkjzIv7MgqGli');

INSERT INTO Communities (Name, Genre, Description) VALUES ('Test Community', 'Test Rock', 'This is a test Community');

-- 'admin' is a member of 'Test Community' and is superadmin
INSERT INTO CommunityMembers (CommunityId, UserId, Permission) VALUES (1, 1, 1);
-- 'user' is a member of 'Test Community' and can read and write events
INSERT INTO CommunityMembers (CommunityId, UserId, Permission) VALUES (1, 2, 6);

-- made by 'admin' in 'Test Community'
INSERT INTO CommunityPosts (CommunityId, UserId, Title, Content) VALUES (1, 1, 'Test Post', 'This is a test Community Post');

INSERT INTO Events (CommunityId, Name, Description, Venue, Capacity, Scheduled) VALUES (1, 'Test Event', 'This is a test Event', 'Not a real place', 100, '2025-01-01 00:00:00');

-- 'admin' is attending 'Test Event' and is superadmin
INSERT INTO Bookings (EventId, UserId, Permission) VALUES (1, 1, 1);
-- 'user' is attending 'Test Event' and can read and write event details and comments
INSERT INTO Bookings (EventId, UserId, Permission) VALUES (1, 2, 6);

-- made by 'admin' in 'Test Event'
INSERT INTO EventComments (UserId, EventId, Content) VALUES (1, 1, 'This is a test comment');
