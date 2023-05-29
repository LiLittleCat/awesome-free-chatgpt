package com.lilittlecat.freechatgpt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lilittlecat.freechatgpt.Feature.*;

public class Build {

    public static void main(String[] args) throws TemplateException, IOException {
        Build build = new Build();
        build.initNormal();
        build.initAbnormal();
        build.buildTable();
    }

    public void buildTable() throws IOException, TemplateException {
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
        FileUtil.writeString(newReadmeContent, readmeFile, StandardCharsets.UTF_8);

    }

    public void update() {
        String basePath = System.getProperty("user.dir");

        File normalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "normal-websites.json");
        String normalWebsitesJSONString = FileUtil.readString(normalWebsitesJSON, StandardCharsets.UTF_8);
        List<Website> normalWebsitesJSONArray = JSON.parseArray(normalWebsitesJSONString, Website.class);

        File abnormalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "abnormal-websites.json");
        String abnormalWebsitesJSONString = FileUtil.readString(abnormalWebsitesJSON, StandardCharsets.UTF_8);
        List<Website> abnormalWebsitesJSONArray = JSON.parseArray(abnormalWebsitesJSONString, Website.class);

        Website website = normalWebsitesJSONArray.get(0);
        List<Feature> features = new ArrayList<>();
        features.add(FREE);
        features.add(GPT4_SUPPORTED);
        website.setFeatures(features);
        website.setScore(Feature.score(features));

        System.out.println(JSON.toJSONString(website));


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

    public static List<String> extractLabels(String str) {
        List<String> emojis = new ArrayList<>();
        int i = 0;
        while (i < str.length()) {
            int codepoint = str.codePointAt(i);
            if (Character.isSupplementaryCodePoint(codepoint)) {
                i += 2;
            } else {
                i++;
            }
            if (Character.isSurrogate((char) codepoint)) {
                continue;
            }
            if (Character.getType(codepoint) == Character.OTHER_SYMBOL) {
                emojis.add(new String(Character.toChars(codepoint)));
            }
        }
        System.out.println(emojis);
        return emojis;

    }

    public static String extractLink(String content) {
        String regex = "\\[https?://\\S+]";
        Pattern linkPattern = Pattern.compile(regex);
        Matcher linkMatcher = linkPattern.matcher(content);
        if (linkMatcher.find()) {
            String link = linkMatcher.group(0).replace("[", "").replace("]", "");
            if (!link.endsWith("/")) {
                return link + "/";
            }
            return link;
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
