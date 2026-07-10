@echo off
echo Fixing Vietnamese encoding issues...

REM Backup original file
copy "src\main\webapp\Views\Homepage.jsp" "src\main\webapp\Views\Homepage.jsp.backup"

REM Fix encoding issues
powershell -Command "(Get-Content 'src\main\webapp\Views\Homepage.jsp' -Encoding UTF8) -replace 'H N\?i', 'Hà Nội' | Set-Content 'src\main\webapp\Views\Homepage.jsp' -Encoding UTF8"

echo Fixed encoding issues!
echo Please check the file and verify Vietnamese characters display correctly.
pause
