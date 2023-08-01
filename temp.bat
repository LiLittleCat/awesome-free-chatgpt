@echo off

:: 1. checkout develop branch
git checkout develop

:: 2. create target directory if it does not exist
if not exist "..\\awesome-free-chatgpt-temp" (
  mkdir ..\\awesome-free-chatgpt-temp
)

:: 3. copy files to target directory
copy README.md README_en.md urls.json ..\\awesome-free-chatgpt-temp\\

:: 4. create a new branch from main branch
if "%1"=="" (
  set branch_name=%date:~0,4%%date:~5,2%%date:~8,2%
) else (
  set branch_name=%1
)

git checkout main
git pull
git checkout -b %branch_name%

:: 5. copy files from target directory to current directory
copy ..\\awesome-free-chatgpt-temp\\* .