@echo off
chcp 65001 > nul
title SmartEdu - Ta'lim Platformasi
cls
echo ========================================================
echo         «SmartEdu» Zamonaviy Ta'lim Platformasi
echo                Java 17 & Spring Boot 3
echo ========================================================
echo.
echo Loyiha ishga tushirilmoqda, iltimos kuting...
echo Server manzili: http://localhost:8080
echo.

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

call "%~dp0mvnw.cmd" spring-boot:run

pause
