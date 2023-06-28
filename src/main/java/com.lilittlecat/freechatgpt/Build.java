package com.lilittlecat.freechatgpt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lilittlecat.freechatgpt.Feature.*;

public class Build {

    public static void main(String[] args) throws TemplateException, IOException {
        Build build = new Build();

//        build.initNormal();
//        build.initAbnormal();
//        build.updateReadme();

//        System.out.println(extractLabels(Feature.allLabelString()));

        build.newAdd();

    }

    public void newAdd() throws TemplateException, IOException {
        String basePath = System.getProperty("user.dir");
        File normalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "normal-websites.json");
        String normalWebsitesJSONString = FileUtil.readString(normalWebsitesJSON, StandardCharsets.UTF_8);
        List<Website> normalWebsites = JSON.parseArray(normalWebsitesJSONString, Website.class);
        File originalMdFile = new File(basePath + File.separator + "data" + File.separator + "original.md");
        String originalMdFileContent = FileUtil.readString(originalMdFile, StandardCharsets.UTF_8);
        String newAddContent = StrUtil.subBetween(originalMdFileContent, "<!-- new-add-begin -->", "<!-- new-add-end -->");
        String oldNormalWebsitesContent = StrUtil.subBetween(originalMdFileContent, "<!-- normal-begin -->", "<!-- normal-end -->");
        StringBuilder newNormalSitesContent = new StringBuilder(oldNormalWebsitesContent);
        String[] newAddLines = newAddContent.split("\n");
        for (String newAddLine : newAddLines) {
            String[] strings = newAddLine.split(" - ");
            if (strings.length < 2) {
                continue;
            }
            // new add to the end of normal
            newNormalSitesContent.append(newAddLine).append("\n");
            // Extract the link
            String link = extractLink(strings[0]);
            // Extract the time
            String time = extractTime(strings[1]);
            if (StrUtil.isNotBlank(link) && StrUtil.isNotBlank(time)) {
                Website website = new Website();
                website.setUrl(link);
                website.setAddedDate(time);
                if (strings.length > 2) {
                    website.setCustomDescription(wrapSentence(strings[2]));
                    System.out.println(website.getId() + "." + link + " " + time + " " + strings[2]);
                } else {
                    System.out.println(website.getId() + "." + link + " " + time);
                }
                // Extract the labels
                List<String> labels = extractLabels(newAddLine);
                if (CollUtil.isNotEmpty(labels)) {
                    List<Feature> features = new ArrayList<>();
                    for (String label : labels) {
                        Feature feature = fromLabel(label);
                        if (feature != null) {
                            features.add(feature);
                        }
                    }
                    website.setScore(Feature.score(features));
                    website.setFeatures(features);
                }
                normalWebsites.add(website);
            }
        }
        // normalWebsites 先根据得分排序，得分相同的再根据时间排序
        normalWebsites.sort((o1, o2) -> {
            int scoreCompare = o2.getScore().compareTo(o1.getScore());
            if (scoreCompare == 0) {
                return o2.getAddedDate().compareTo(o1.getAddedDate());
            }
            return scoreCompare;
        });
        // set id
        int normalId = 1;
        for (Website normalWebsite : normalWebsites) {
            normalWebsite.setId(normalId++);
        }
        FileUtil.writeString(JSON.toJSONString(normalWebsites, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.SortField),
                normalWebsitesJSON, StandardCharsets.UTF_8);

        // move new add to the end of normal
        String newOriginalMdContent = originalMdFileContent.replace(newAddContent, "\n\n\n\n")
                .replace(oldNormalWebsitesContent, newNormalSitesContent);

        FileUtil.writeString(newOriginalMdContent, originalMdFile, StandardCharsets.UTF_8);

        updateReadme();

    }

    public void updateReadme() throws IOException, TemplateException {
        String basePath = System.getProperty("user.dir");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        FileTemplateLoader templateLoader = new FileTemplateLoader(new File(basePath + File.separator + "src" + File.separator + "main" + File.separator + "resources"));
        cfg.setTemplateLoader(templateLoader);
//        cfg.setClassForTemplateLoading(Website.class, basePath + File.separator + "src" + File.separator + "main" + File.separator + "resources");
        cfg.setDefaultEncoding("UTF-8");

        File normalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "normal-websites.json");
        String normalWebsitesJSONString = FileUtil.readString(normalWebsitesJSON, StandardCharsets.UTF_8);
        List<Website> normalWebsites = JSON.parseArray(normalWebsitesJSONString, Website.class);

        Template normalTemplate = cfg.getTemplate("normal-websites-table.ftl");
        Map<String, Object> normalModel = new HashMap<>();
        normalModel.put("websites", normalWebsites);
        StringWriter normalOut = new StringWriter();
        normalTemplate.process(normalModel, normalOut);
        String normalRenderedHtml = "\n" + normalOut;
        System.out.println(normalRenderedHtml);


        File abnormalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "abnormal-websites.json");
        String abnormalWebsitesJSONString = FileUtil.readString(abnormalWebsitesJSON, StandardCharsets.UTF_8);
        List<Website> abnormalWebsites = JSON.parseArray(abnormalWebsitesJSONString, Website.class);

        Template abnormalTemplate = cfg.getTemplate("abnormal-websites-table.ftl");
        Map<String, Object> abnormalModel = new HashMap<>();
        abnormalModel.put("websites", abnormalWebsites);
        StringWriter abnormalOut = new StringWriter();
        abnormalTemplate.process(abnormalModel, abnormalOut);
        String abnormalRenderedHtml = "\n" + abnormalOut;
        System.out.println(abnormalRenderedHtml);


        String readmeFilePath = basePath + File.separator + "README.md";
        File readmeFile = new File(readmeFilePath);
        // replace  renderedHtml to README.md from <!-- normal-begin --> to <!-- normal-end -->
        String readContent = FileUtil.readString(readmeFile, StandardCharsets.UTF_8);
        String normalSitesContent = StrUtil.subBetween(readContent, "<!-- normal-begin -->", "<!-- normal-end -->");
        String abnormalSitesContent = StrUtil.subBetween(readContent, "<!-- abnormal-begin -->", "<!-- abnormal-end -->");
        String newReadmeContent = readContent.replace(normalSitesContent, normalRenderedHtml).replace(abnormalSitesContent, abnormalRenderedHtml);
        // replace the count, like https://img.shields.io/badge/websites-107-blue?style=flat to https://img.shields.io/badge/websites-{number}-blue?style=flat
        String websitesCount = StrUtil.subBetween(newReadmeContent, "https://img.shields.io/badge/websites-", "-blue?style=flat");
        String newReadmeContent2 = newReadmeContent.replace("https://img.shields.io/badge/websites-" + websitesCount + "-blue?style=flat", "https://img.shields.io/badge/websites-" + normalWebsites.size() + "-blue?style=flat");
        FileUtil.writeString(newReadmeContent2, readmeFile, StandardCharsets.UTF_8);

        // update README_en.md
        String readmeFilePathEnglish = basePath + File.separator + "README_en.md";
        File readmeFileEnglish = new File(readmeFilePathEnglish);
        List<Website> normalWebsitesEnglish = new ArrayList<>();
        for (Website normalWebsite : normalWebsites) {
            List<Feature> features = normalWebsite.getFeatures();
            if (features.contains(Feature.LOGIN_REQUIRED)
                    || features.contains(FREE_QUOTA)
                    || features.contains(Feature.FOLLOW_ON_WECHAT_REQUIRED)
                    || features.contains(Feature.CHARGE_REQUIRED)) {
                continue;
            }
            if (normalWebsite.getUrl().contains("tchat.c1ns.cn")) {
                continue;
            }
            features.remove(VPN_REQUIRED);
            normalWebsitesEnglish.add(normalWebsite);
        }
        // sort
        for (Website websitesEnglish : normalWebsitesEnglish) {
            websitesEnglish.setScore(Feature.score(websitesEnglish.getFeatures()));
        }
        normalWebsitesEnglish.sort((o1, o2) -> {
            int scoreCompare = o2.getScore().compareTo(o1.getScore());
            if (scoreCompare == 0) {
                return o2.getAddedDate().compareTo(o1.getAddedDate());
            }
            return scoreCompare;
        });
        Template normalTemplateEnglish = cfg.getTemplate("normal-websites-table-en.ftl");
        Map<String, Object> normalModelEnglish = new HashMap<>();
        normalModelEnglish.put("websites", normalWebsitesEnglish);
        StringWriter normalOutEnglish = new StringWriter();
        normalTemplateEnglish.process(normalModelEnglish, normalOutEnglish);
        String normalRenderedHtmlEnglish = "\n" + normalOutEnglish;
        System.out.println(normalRenderedHtmlEnglish);

        String readContentEnglish = FileUtil.readString(readmeFileEnglish, StandardCharsets.UTF_8);
        String normalSitesContentEnglish = StrUtil.subBetween(readContentEnglish, "<!-- normal-begin -->", "<!-- normal-end -->");
        String newReadmeContentEnglish = readContentEnglish.replace(normalSitesContentEnglish, normalRenderedHtmlEnglish);
        // replace the count, like https://img.shields.io/badge/websites-107-blue?style=flat to https://img.shields.io/badge/websites-{number}-blue?style=flat
        String websitesCountEnglish = StrUtil.subBetween(newReadmeContentEnglish, "https://img.shields.io/badge/websites-", "-blue?style=flat");
        String newReadmeContentEnglish2 = newReadmeContentEnglish.replace("https://img.shields.io/badge/websites-" + websitesCountEnglish + "-blue?style=flat",
                "https://img.shields.io/badge/websites-" + normalWebsitesEnglish.size() + "-blue?style=flat");
        FileUtil.writeString(newReadmeContentEnglish2, readmeFileEnglish, StandardCharsets.UTF_8);

        // update urls.json
        String urlsJsonFilePath = basePath + File.separator + "urls.json";
        File urlsJsonFile = new File(urlsJsonFilePath);
        List<String> urls = new ArrayList<>();
        urls.add("https://poe.com");
        urls.add("https://huggingface.co/chat");
        urls.add("https://chat.lmsys.org/");
        urls.add("https://bard.google.com/");
        urls.add("https://heypi.com/talk");
        urls.add("https://open-assistant.io/");
        urls.add("https://talk.truthgpt.one/");
        urls.add("https://yiyan.baidu.com/");
        urls.add("https://tongyi.aliyun.com/");
        urls.add("https://xinghuo.xfyun.cn/");

        for (Website normalWebsite : normalWebsitesEnglish) {
            urls.add(normalWebsite.getUrl());
        }
        FileUtil.writeString(JSON.toJSONString(urls), urlsJsonFile, StandardCharsets.UTF_8);

    }

    public void initNormal() {
        String basePath = System.getProperty("user.dir");
        String readmeFilePath = basePath + File.separator + "data" + File.separator + "original.md";
        File file = new File(readmeFilePath);
        String readContent = FileUtil.readString(file, StandardCharsets.UTF_8);
        String normalSitesContent = StrUtil.subBetween(readContent, "<!-- normal-begin -->", "<!-- normal-end -->");

        String[] normalSites = normalSitesContent.split("\n");
        List<Website> normalWebsites = new ArrayList<>();
        for (String normalSite : normalSites) {
            String[] strings = normalSite.split(" - ");
            if (strings.length < 2) {
                continue;
            }
            // Extract the link
            String link = extractLink(strings[0]);
            // Extract the time
            String time = extractTime(strings[1]);
            if (StrUtil.isNotBlank(link) && StrUtil.isNotBlank(time)) {
                Website website = new Website();
                website.setUrl(link);
                website.setAddedDate(time);
                if (strings.length > 2) {
                    website.setCustomDescription(wrapSentence(strings[2]));
                    if (strings.length > 3) {
                        website.setCustomDescriptionEnglish(wrapSentenceEnglish(strings[3]));
                    }
                    System.out.println(website.getId() + "." + link + " " + time + " " + strings[2]);
                } else {
                    System.out.println(website.getId() + "." + link + " " + time);
                }
                // Extract the labels
                List<String> labels = extractLabels(normalSite);
                if (CollUtil.isNotEmpty(labels)) {
                    List<Feature> features = new ArrayList<>();
                    for (String label : labels) {
                        Feature feature = fromLabel(label);
                        if (feature != null) {
                            features.add(feature);
                        }
                    }
                    website.setScore(Feature.score(features));
                    website.setFeatures(features);
                }
                normalWebsites.add(website);
            }
        }
        // normalWebsites 先根据得分排序，得分相同的再根据时间排序
        normalWebsites.sort((o1, o2) -> {
            int scoreCompare = o2.getScore().compareTo(o1.getScore());
            if (scoreCompare == 0) {
                return o2.getAddedDate().compareTo(o1.getAddedDate());
            }
            return scoreCompare;
        });
        // set id
        int normalId = 1;
        for (Website normalWebsite : normalWebsites) {
            normalWebsite.setId(normalId++);
        }

        String normalWebsitesJSONString = JSON.toJSONString(normalWebsites, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.SortField);
        File normalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "normal-websites.json");
        FileUtil.writeString(normalWebsitesJSONString, normalWebsitesJSON, StandardCharsets.UTF_8);
    }


    public void initAbnormal() throws IOException, TemplateException {
        String basePath = System.getProperty("user.dir");
        String readmeFilePath = basePath + File.separator + "data" + File.separator + "original.md";
        File file = new File(readmeFilePath);
        String readContent = FileUtil.readString(file, StandardCharsets.UTF_8);
        String abnormalSitesContent = StrUtil.subBetween(readContent, "<!-- abnormal-begin -->", "<!-- abnormal-end -->");

        String[] abnormalSites = abnormalSitesContent.split("\n");
        List<Website> abnormalWebsites = new ArrayList<>();
        for (String abnormalSite : abnormalSites) {
            // Extract the link
            String link = extractLink(abnormalSite);
            // Extract the time
            String time = extractTime(abnormalSite);
            if (StrUtil.isNotBlank(link) && StrUtil.isNotBlank(time)) {
                Website website = new Website();
                website.setUrl(link);
                website.setReportedInvalidDate(time);
                abnormalWebsites.add(website);
                System.out.println(link + " " + time);
            }
        }
        // sorted by reportedInvalidDate desc
        abnormalWebsites.sort((o1, o2) -> {
            LocalDate date1 = LocalDate.parse(o1.getReportedInvalidDate());
            LocalDate date2 = LocalDate.parse(o2.getReportedInvalidDate());
            return date2.compareTo(date1);
        });
        // set id
        int abnormalId = 1;
        for (Website abnormalWebsite : abnormalWebsites) {
            abnormalWebsite.setId(abnormalId++);
        }

        String abnormalWebsitesJSONString = JSON.toJSONString(abnormalWebsites, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.SortField);
        File abnormalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "abnormal-websites.json");
        FileUtil.writeString(abnormalWebsitesJSONString, abnormalWebsitesJSON, StandardCharsets.UTF_8);


    }

    public static String wrapSentence(String text) {
        String template = "<details>\n" +
                "<summary>内容过长，点击展开</summary>\n" +
                "{text}\n" +
                "</details>";
        if (StrUtil.isBlank(text)) {
            return null;
        }
        if (text.length() > 30) {
            return template.replace("{text}", text);
        } else {
            return text;
        }
    }
    public static String wrapSentenceEnglish(String text) {
        String template = "<details>\n" +
                "<summary>Content is too long, click to expand.</summary>\n" +
                "{text}\n" +
                "</details>";
        if (StrUtil.isBlank(text)) {
            return null;
        }
        if (text.length() > 30) {
            return template.replace("{text}", text);
        } else {
            return text;
        }
    }

    public static List<String> extractLabels(String str) {
        List<String> emojis = new ArrayList<>();
        int i = 0;
        while (i < str.length()) {
            int codepoint = str.codePointAt(i);
            int charCount = Character.charCount(codepoint);
            if (Character.isSurrogate((char) codepoint)) {
                i += charCount;
                continue;
            }
            String emoji = new String(Character.toChars(codepoint));
            if (Character.getType(codepoint) == Character.OTHER_SYMBOL && emoji.matches("[\\p{So}\\p{Sc}]")) {
                emojis.add(emoji);
            }
            i += charCount;
        }
        System.out.println(emojis);
        return emojis;

    }

    public static String extractLink(String content) {
        String regex = "\\[https?://\\S+]";
        Pattern linkPattern = Pattern.compile(regex);
        Matcher linkMatcher = linkPattern.matcher(content);
        if (linkMatcher.find()) {
            return linkMatcher.group(0).replace("[", "").replace("]", "");
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
