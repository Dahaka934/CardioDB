@echo off
set currDir=%~dp0
set gradlew=%currDir%internal\gradleTask.bat
set gdrive=%currDir%internal\gdriveUpload.bat
cd..

call %gdrive% CardioDB\build\archives\*