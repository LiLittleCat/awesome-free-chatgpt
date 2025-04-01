package com.lilittlecat.freechatgpt;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lilittlecat.freechatgpt.badge.Badge;
import com.lilittlecat.freechatgpt.feature.Feature;
import com.lilittlecat.freechatgpt.feature.Login;
import com.lilittlecat.freechatgpt.feature.Model;
import com.lilittlecat.freechatgpt.feature.OtherFeature;
import com.lilittlecat.freechatgpt.website.Website;
import com.lilittlecat.freechatgpt.website.WebsiteMetadata;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Build {

    public static void main(String[] args) {
        String[] websites = {
                // "https://www.google.com",
                // "https://heck.ai",
                // "https://this-is-invalid-domain.com",
                // "https://github.com/vuejs/vue-next",
                // "https://tongyi.aliyun.com/"
//                "https://leetcode.cn/problems/count-the-number-of-beautiful-subarrays/description/?envType=daily-question&envId=2025-03-06"

                "https://huggingface.co/spaces/lmarena-ai/chatbot-arena-leaderboard"
        };

//        for (String website : websites) {
//            try {
//                WebsiteMetadata metadata = getWebsiteMetadata(website);
//                System.out.println("Website: " + website);
//                System.out.println("Title: " + metadata.getTitle());
//                System.out.println("Description: " + metadata.getDescription());
//                System.out.println("OG Title: " + metadata.getOgTitle());
//                System.out.println("OG Description: " + metadata.getOgDescription());
//                System.out.println("OG Image: " + metadata.getOgImage());
//                System.out.println("Status Badge URL: " + metadata.getStatus().generateUrl());
//                System.out.println("Favicon: " + metadata.getFavicon());
//                System.out.println("-------------------");
//            } catch (Exception e) {
//                System.out.println("Error processing website: " + website);
//                System.out.println("Error: " + e.getMessage());
//                System.out.println("-------------------");
//            }
//        }
        // update
        new Build().update();
    }

    public void update() {
        String basePath = System.getProperty("user.dir");
        File originalJSON = new File(String.join(File.separator, basePath, "data", "v2", "original.json"));
        JSONObject originalJSONObject = JSON.parseObject(FileUtil.readString(originalJSON, StandardCharsets.UTF_8));
        List<Website> originalWebsites = originalJSONObject.getJSONArray("websites").toJavaList(Website.class);
        List<Website> result = new ArrayList<>();
        originalWebsites.parallelStream().forEach(
                website -> {
                    String url = website.getUrl();
                    if (StrUtil.isEmpty(url)) {
                        return;
                    }
                    // get info
                    try {
                        WebsiteMetadata metadata = getWebsiteMetadata(url);
                        website.setTitle(metadata.getTitle());
                        website.setDescription(metadata.getDescription());
                        website.setFavicon(metadata.getFavicon());
                        website.setStatus(metadata.getStatus());

                    } catch (Exception e) {
                        System.out.println("Error processing website: " + url);
                        System.out.println("Error: " + e.getMessage());
                        System.out.println("-------------------");
                    }
                    List<String> featureStrings = website.getFeatureStrings();
                    List<Feature> features = website.getFeatures() == null ? new ArrayList<>() : website.getFeatures();
                    for (String featureString : featureStrings) {
                        if (StrUtil.isNotEmpty(featureString)) {
                            OtherFeature otherFeature = OtherFeature.getByName(featureString);
                            if (otherFeature != null) {
                                features.add(otherFeature);
                            }
                            Login login = Login.getByName(featureString);
                            if (login != null) {
                                features.add(login);
                            }
                        }
                    }
                    // calculate score
                    double score = 0.0;
                    for (Feature feature : features) {
                        score += feature.getScore();
                        website.getFeatureBadges().add(feature.getBadge());
                    }
                    // model
                    if (website.getModels() != null) {
                        for (Model model : website.getModels()) {
                            if (model.getPro()) {
                                model.setScore(Score.PRO_MODEL.getScore());
                            } else {
                                model.setScore(Score.NORMAL_MODEL.getScore());
                            }
                            score += model.getScore();
                            // badge
                            website.getModelBadges().add(model.getBadge());
                        }
                    }
                    // uptime
                    if (website.getAddedDate() != null) {
                        // 计算从添加日期到现在的天数
                        try {
                            java.time.LocalDate addedDate = java.time.LocalDate.parse(website.getAddedDate());
                            java.time.LocalDate now = java.time.LocalDate.now();
                            long daysSurvived = java.time.temporal.ChronoUnit.DAYS.between(addedDate, now);
                            // 每天存活时间 +0.1
                            score += daysSurvived * Score.DAYS_SURVIVED.getScore();
                            // badge
                            OtherFeature.UPTIME.setMessage(OtherFeature.UPTIME.getMessage().replace("{daysSurvived}", String.valueOf(daysSurvived)));
                            OtherFeature.UPTIME.setMessageCN(OtherFeature.UPTIME.getMessageCN().replace("{daysSurvived}", String.valueOf(daysSurvived)));
                            website.getFeatureBadges().add(OtherFeature.UPTIME.getBadge());
                        } catch (Exception e) {
                            // 日期解析错误，忽略这部分分数
                            System.out.println("Error parsing date: " + website.getAddedDate() + " for website: " + website.getUrl());
                        }
                    }
                    website.setScore(score);
                    result.add(website);
                }
        );
        // 根据分数倒序
        result.sort((o1, o2) -> {
            if (o1.getScore() == null && o2.getScore() == null) {
                return 0;
            } else if (o1.getScore() == null) {
                return 1;
            } else if (o2.getScore() == null) {
                return -1;
            } else {
                return Double.compare(o2.getScore(), o1.getScore());
            }
        });
        // set id
        int id = 1;
        for (Website website : result) {
            website.setId(id++);
        }
        System.out.println(result);
        String normalWebsitesJSONString = JSON.toJSONString(result, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.SortField);
        File normalWebsitesJSON = new File(String.join(File.separator, basePath, "data", "v2", "websites.json"));
        FileUtil.writeString(normalWebsitesJSONString, normalWebsitesJSON, StandardCharsets.UTF_8);
    }

    public void updateReadme() throws IOException, TemplateException {
        String basePath = System.getProperty("user.dir");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        FileTemplateLoader templateLoader = new FileTemplateLoader(new File(basePath + File.separator + "src" + File.separator + "main" + File.separator + "resources"));
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");



    }

    /**
     * Wrap sentence
     *
     * @param text
     * @return
     */
    public static String wrapSentence(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        String template;
        template = "<details>\n" +
                "<summary>{summary}</summary>\n" +
                "{text}\n" +
                "</details>";
        if (text.length() > 30) {
            return template.replace("{summary}", text.substring(0, 30) + "...").replace("{text}", text);
        } else {
            return text;
        }
    }

    /**
     * Get website metadata including title, description, Open Graph tags, and
     * status
     *
     * @param url Website URL to analyze
     * @return WebsiteMetadata object containing all extracted information
     */
    public static WebsiteMetadata getWebsiteMetadata(String url) {
        WebsiteMetadata metadata = new WebsiteMetadata();
        metadata.setUrl(url);

        try {
            // Send HTTP GET request with desktop User-Agent
            HttpResponse<String> response = Unirest.get(url)
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .asString();

            // Set status badge
            metadata.setStatus(checkWebsiteStatus(url));

            // Parse HTML with Jsoup
            Document doc = Jsoup.parse(response.getBody());

            // Extract basic meta information
            metadata.setTitle(getMetaContent(doc, "title", "content"));
            if (metadata.getTitle() == null || metadata.getTitle().isEmpty()) {
                metadata.setTitle(doc.title());
            }
            metadata.setDescription(getMetaContent(doc, "description", "content"));

            // Extract Open Graph meta information
            metadata.setOgTitle(getMetaContent(doc, "og:title", "content"));
            metadata.setOgDescription(getMetaContent(doc, "og:description", "content"));
            metadata.setOgImage(getMetaContent(doc, "og:image", "content"));
            metadata.setOgSiteName(getMetaContent(doc, "og:site_name", "content"));
            metadata.setOgType(getMetaContent(doc, "og:type", "content"));

            // Extract Twitter Card meta information
            metadata.setTwitterCard(getMetaContent(doc, "twitter:card", "content"));
            metadata.setTwitterTitle(getMetaContent(doc, "twitter:title", "content"));
            metadata.setTwitterDescription(getMetaContent(doc, "twitter:description", "content"));
            metadata.setTwitterImage(getMetaContent(doc, "twitter:image", "content"));
            metadata.setTwitterSite(getMetaContent(doc, "twitter:site", "content"));

            // Extract favicon
            Element faviconLink = doc.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                String faviconUrl = faviconLink.attr("abs:href");
                metadata.setFavicon(faviconUrl);
            }

            return metadata;
        } catch (Exception e) {
            metadata.setStatus(Badge.STATUS_ERROR);
            throw new RuntimeException("Failed to fetch website metadata: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to extract meta tag content
     */
    private static String getMetaContent(Document doc, String property, String attr) {
        Element meta = doc.select(String.format("meta[property=%s], meta[name=%s]", property, property)).first();
        return meta != null ? meta.attr(attr) : null;
    }

    /**
     * Check website status and return corresponding status badge
     *
     * @param url Website URL to check
     * @return Status badge object
     * <p>
     * <ul>
     * <li>
     * <b>STATUS_OK</b>: Status code 200-299, indicates website is
     * normal</li>
     * <li>
     * <b>STATUS_WARNING</b>: Status code 300-499, indicates redirect or
     * client error</li>
     * <li>
     * <b>STATUS_ERROR</b>: Status code 500+ or request exception, indicates
     * server error</li>
     *
     * </ul>
     */
    public static Badge checkWebsiteStatus(String url) {
        try {
            // Send HTTP GET request
            HttpResponse<String> response = Unirest.get(url)
                    // Set User-Agent to avoid being blocked
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .asString();

            // Get response status code
            int statusCode = response.getStatus();

            // Return corresponding badge based on status code
            if (statusCode >= 200 && statusCode < 300) {
                return Badge.STATUS_OK;
            } else if (statusCode >= 300 && statusCode < 500) {
                return Badge.STATUS_WARNING;
            } else {
                return Badge.STATUS_ERROR;
            }
        } catch (Exception e) {
            // Return error badge when request fails
            return Badge.STATUS_ERROR;
        }
    }

}
