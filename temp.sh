#!/bin/bash

# 1. checkout develop branch
git checkout develop

# 2. create target directory if it does not exist
if [ ! -d "../awesome-free-chatgpt-temp" ]; then
  mkdir ../awesome-free-chatgpt-temp
fi
# 3. copy files to target directory
cp README.md README_en.md urls.json ../awesome-free-chatgpt-temp/

# 4. create a new branch from main branch
if [ -n "$1" ]; then
  branch_name=$1
else
  branch_name=$(date +%Y%m%d)
fi

git checkout main
git pull
git checkout -b $branch_name

# 5. copy files from target directory to current directory
cp ../awesome-free-chatgpt-temp/* .
