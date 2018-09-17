@echo off
set currDir=%~dp0
set gdrive=%currDir%gdrive.exe
cd..

for /r %%i in (%1) do (
    %gdrive% upload -p 1lkp-A75pxTsEhLUgDHkiGnhgoBhiSNrm --delete %%i
)