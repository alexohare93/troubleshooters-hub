# get the directory where the script is located (rather than where the user is running it from)
$ScriptDir = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition

Get-Content "$ScriptDir\mock_data\communities.sql" | sqlite3 "$ScriptDir\..\database.db"
Get-Content "$ScriptDir\mock_data\events.sql" | sqlite3 "$ScriptDir\..\database.db"
Get-Content "$ScriptDir\mock_data\users.sql" | sqlite3 "$ScriptDir\..\database.db"
Get-Content "$ScriptDir\mock_data\community_members.sql" | sqlite3 "$ScriptDir\..\database.db"
Get-Content "$ScriptDir\mock_data\bookings.sql" | sqlite3 "$ScriptDir\..\database.db"
Get-Content "$ScriptDir\mock_data\community_posts.sql" | sqlite3 "$ScriptDir\..\database.db"
Get-Content "$ScriptDir\mock_data\event_comments.sql" | sqlite3 "$ScriptDir\..\database.db"
