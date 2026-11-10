@echo off
cd /d "%~dp0microservice-produits"
echo ========================================================
echo   Demarrage de Microservice Produits (Port 9001)
echo ========================================================
java -jar target\microservice-produits-0.0.1-SNAPSHOT.jar
pause
