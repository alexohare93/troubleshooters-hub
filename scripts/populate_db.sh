#!/bin/bash

# get the directory where the script is located (rather than where the user is running it from)
SCRIPT_DIR="$(dirname "$(realpath "$0")")"

sqlite3 "$SCRIPT_DIR/../database.db" < "$SCRIPT_DIR/mock_data/users.sql"
sqlite3 "$SCRIPT_DIR/../database.db" < "$SCRIPT_DIR/mock_data/communities.sql"
sqlite3 "$SCRIPT_DIR/../database.db" < "$SCRIPT_DIR/mock_data/community_members.sql"
sqlite3 "$SCRIPT_DIR/../database.db" < "$SCRIPT_DIR/mock_data/community_posts.sql"
sqlite3 "$SCRIPT_DIR/../database.db" < "$SCRIPT_DIR/mock_data/events.sql"
sqlite3 "$SCRIPT_DIR/../database.db" < "$SCRIPT_DIR/mock_data/bookingst .sql"
sqlite3 "$SCRIPT_DIR/../database.db" < "$SCRIPT_DIR/mock_data/event_comments.sql"
