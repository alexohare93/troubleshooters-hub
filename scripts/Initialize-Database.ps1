# get the directory where the script is located (rather than where the user is running it from)
$ScriptDir = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition

Remove-Item -Path "$ScriptDir\..\database.db" -ErrorAction SilentlyContinue
Get-Content "$ScriptDir\..\database.sql" | sqlite3 "$ScriptDir\..\database.db"