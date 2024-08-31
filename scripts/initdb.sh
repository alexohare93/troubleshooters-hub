#!/bin/bash

# get the directory where the script is located (rather than where the user is running it from)
SCRIPT_DIR="$(dirname "$(realpath "$0")")"

rm -f "$SCRIPT_DIR/../database.db"
sqlite3 "$SCRIPT_DIR/../database.db" < "$SCRIPT_DIR/../database.sql"