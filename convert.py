# convert the urls in README.md to json format, and save it to urls.json, colapse the same urls, remove the last `/` in the url

import json
import re


# Read the content of README.md
with open("README.md", "r", encoding="utf-8") as file:
    content = file.read()
    # Stop reading when reaching a line that contains '### 🚫 已失效'
    content = content.split('### 🚫 已失效')[0]

# Find all URLs in the content []
urls = re.findall(r'http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+(?=\])', content)
# urls = re.findall(r'(?<!~~)(http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+)(?!~~)', content)


# Remove the last '/' in the URL and collapse the same URLs
unique_urls = []
for url in urls:
    url = url[:-1] if url.endswith('/') else url
    if url not in unique_urls:
        unique_urls.append(url)


# Save the URLs to urls.json
with open("urls.json", "w") as file:
    json.dump(unique_urls, file)
