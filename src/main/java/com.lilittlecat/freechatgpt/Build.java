package com.lilittlecat.freechatgpt;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Build {

    public static void main(String[] args) {
        String basePath = System.getProperty("user.dir");
        String readmeFilePath = basePath + File.separator + "README.md";
        File file = new File(readmeFilePath);
        String readContent = FileUtil.readString(file, StandardCharsets.UTF_8);
        String normalSitesContent = StrUtil.subBetween(readContent, "<!-- normal-begin -->", "<!-- normal-end -->");
        String abnormalSitesContent = StrUtil.subBetween(readContent, "<!-- abnormal-begin -->", "<!-- abnormal-end -->");

        String[] normalSites = normalSitesContent.split("\n");
        for (String normalSite : normalSites) {
            // Extract the link
            String link = extractLink(normalSite);
            // Extract the time
            String time = extractTime(normalSite);
            if (StrUtil.isNotBlank(link) && StrUtil.isNotBlank(time)) {
                Website website = new Website();
                website.setUrl(link);
                website.setAddedDate(LocalDate.parse(time));
            }
            System.out.println(link + " " + time);
        }


    }

    public static String extractLink(String content) {
        Pattern linkPattern = Pattern.compile("\\((.+?)\\)");
        Matcher linkMatcher = linkPattern.matcher(content);
        if (linkMatcher.find()) {
            return linkMatcher.group(1);
        }
        return null;
    }
    public static String extractTime(String content) {
        Pattern timePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher timeMatcher = timePattern.matcher(content);
        if (timeMatcher.find()) {
            return timeMatcher.group();
        }
        return null;
    }

}
