@echo off
cd /d "%~dp0config-server"
echo ========================================================
echo   Demarrage de Spring Cloud Config Server (Port 9101)
echo ========================================================
java -jar target\config.server-0.0.1-SNAPSHOT.jar
pause
