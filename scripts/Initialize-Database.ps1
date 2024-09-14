$ScriptDir = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition

Remove-Item -Path "$ScriptDir\..\database.db" -ErrorAction SilentlyContinue

Get-Content "$ScriptDir\..\database.sql" | & "C:\Users\jeffr\Downloads\sqlite-tools-win-x64-3460100\sqlite3.exe" "$ScriptDir\..\database.db"