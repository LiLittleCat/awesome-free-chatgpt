# 1. checkout develop branch
git checkout develop

# 2. create target directory if it does not exist
if (!(Test-Path -Path "..\\awesome-free-chatgpt-temp")) {
  New-Item -ItemType Directory -Path "..\\awesome-free-chatgpt-temp"
}

# 3. copy files to target directory
Copy-Item README.md, README_en.md, urls.json -Destination "..\\awesome-free-chatgpt-temp\"

# 4. create a new branch from main branch
if ($args[0]) {
  $branch_name = $args[0]
} else {
  $branch_name = Get-Date -Format "yyyyMMdd"
}
git checkout main
git pull
git checkout -b $branch_name

# 5. copy files from target directory to current directory
Copy-Item "..\\awesome-free-chatgpt-temp\*" -Destination .