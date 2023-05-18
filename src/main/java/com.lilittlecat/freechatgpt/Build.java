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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Build {

    public static void main(String[] args) throws TemplateException, IOException {
        Build build = new Build();
        build.init();
//        build.update();
    }

    public void update() {
        String basePath = System.getProperty("user.dir");

        File normalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "normal-websites.json");
        String normalWebsitesJSONString = FileUtil.readString(normalWebsitesJSON, StandardCharsets.UTF_8);
        List<Website> normalWebsitesJSONArray = JSON.parseArray(normalWebsitesJSONString, Website.class);

        File abnormalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "abnormal-websites.json");
        String abnormalWebsitesJSONString = FileUtil.readString(abnormalWebsitesJSON, StandardCharsets.UTF_8);
        List<Website> abnormalWebsitesJSONArray = JSON.parseArray(abnormalWebsitesJSONString, Website.class);





    }

    public void init() throws IOException, TemplateException {
        String basePath = System.getProperty("user.dir");
        String readmeFilePath = basePath + File.separator + "README.md";
        File file = new File(readmeFilePath);
        String readContent = FileUtil.readString(file, StandardCharsets.UTF_8);
        String normalSitesContent = StrUtil.subBetween(readContent, "<!-- normal-begin -->", "<!-- normal-end -->");
        String abnormalSitesContent = StrUtil.subBetween(readContent, "<!-- abnormal-begin -->", "<!-- abnormal-end -->");

        String[] normalSites = normalSitesContent.split("\n");
        List<Website> normalWebsites = new ArrayList<>();
        int normalId = 1;
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
                website.setId(normalId++);
                website.setUrl(link);
                website.setAddedDate(time);
                if (strings.length > 2) {
                    website.setCustomDescription(strings[2]);
                    System.out.println(website.getId() + "." + link + " " + time + " " + strings[2]);
                } else {
                    System.out.println(website.getId() + "." + link + " " + time);
                }
                normalWebsites.add(website);
            }
        }

        String normalWebsitesJSONString = JSON.toJSONString(normalWebsites, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.SortField);
        File normalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "normal-websites.json");
        FileUtil.writeString(normalWebsitesJSONString, normalWebsitesJSON, StandardCharsets.UTF_8);

        String[] abnormalSites = abnormalSitesContent.split("\n");
        List<Website> abnormalWebsites = new ArrayList<>();
        int abnormalId = 1;
        for (String abnormalSite : abnormalSites) {
            // Extract the link
            String link = extractLink(abnormalSite);
            // Extract the time
            String time = extractTime(abnormalSite);
            if (StrUtil.isNotBlank(link) && StrUtil.isNotBlank(time)) {
                Website website = new Website();
                website.setId(abnormalId++);
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

        String abnormalWebsitesJSONString = JSON.toJSONString(abnormalWebsites, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.SortField);
        File abnormalWebsitesJSON = new File(basePath + File.separator + "data" + File.separator + "abnormal-websites.json");
        FileUtil.writeString(abnormalWebsitesJSONString, abnormalWebsitesJSON, StandardCharsets.UTF_8);



//        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
//        FileTemplateLoader templateLoader = new FileTemplateLoader(new File(basePath + File.separator + "src" + File.separator + "main" + File.separator + "resources"));
//        cfg.setTemplateLoader(templateLoader);
//
////        cfg.setClassForTemplateLoading(Website.class, basePath + File.separator + "src" + File.separator + "main" + File.separator + "resources");
//        cfg.setDefaultEncoding("UTF-8");
//        Template template = cfg.getTemplate("normal-websites-table.ftl");
//
//        Map<String, Object> model = new HashMap<>();
//        model.put("websites", normalWebsites);
//
//        StringWriter out = new StringWriter();
//        template.process(model, out);
//
//        String renderedHtml = out.toString();

//        System.out.println(renderedHtml);


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
