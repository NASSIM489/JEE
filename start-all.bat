@echo off
title Lancement Global TP Spring Cloud Config
echo ========================================================
echo   Lancement du Config Server et du Microservice Produits
echo ========================================================

echo [1/2] Demarrage du Config Server sur le port 9101...
start "Spring Cloud Config Server (9101)" cmd /k "cd /d ""%~dp0config-server"" && java -jar target\config.server-0.0.1-SNAPSHOT.jar"

echo En attente de l'initialisation du Config Server (10s)...
timeout /t 10 /nobreak > nul

echo [2/2] Demarrage du Microservice Produits sur le port 9001...
start "Microservice Produits (9001)" cmd /k "cd /d ""%~dp0microservice-produits"" && java -jar target\microservice-produits-0.0.1-SNAPSHOT.jar"

echo.
echo ========================================================
echo Les deux services tournent dans deux fenetres separees !
echo.
echo Verifiez les endpoints dans votre navigateur :
echo 1) Config Server : http://localhost:9101/microservice-produits/default
echo 2) Microservice  : http://localhost:9001/Produits
echo 3) Actuator      : http://localhost:9001/actuator/health
echo ========================================================
pause
