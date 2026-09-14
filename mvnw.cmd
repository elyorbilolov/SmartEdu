@echo off
set "DIR=%~dp0"
if defined JAVA_HOME goto checkJava
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot" (
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
)
:checkJava
"%DIR%apache-maven-3.9.6\bin\mvn.cmd" %*
