@echo off
set currDir=%~dp0
set gradlew=%currDir%internal\gradleTask.bat
cd..

call %gradlew% jfxNative
call %gradlew% archive