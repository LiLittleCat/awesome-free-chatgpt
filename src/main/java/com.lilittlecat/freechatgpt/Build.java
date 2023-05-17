package com.lilittlecat.freechatgpt;

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
            // Extract the link
            String link = extractLink(normalSite);
            // Extract the time
            String time = extractTime(normalSite);
            if (StrUtil.isNotBlank(link) && StrUtil.isNotBlank(time)) {
                Website website = new Website();
                website.setId(normalId++);
                website.setUrl(link);
                website.setAddedDate(time);
                normalWebsites.add(website);
            }
            System.out.println(link + " " + time);
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
            }
            System.out.println(link + " " + time);
        }

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
