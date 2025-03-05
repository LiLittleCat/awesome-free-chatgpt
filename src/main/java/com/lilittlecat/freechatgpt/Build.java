package com.lilittlecat.freechatgpt;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Build {

    public static void main(String[] args) {
        String[] websites = {
                "https://www.google.com",
                "https://heck.ai",
                "https://this-is-invalid-domain.com",
                "https://github.com/vuejs/vue-next",
                "https://tongyi.aliyun.com/"
        };

        for (String website : websites) {
            try {
                WebsiteMetadata metadata = getWebsiteMetadata(website);
                System.out.println("Website: " + website);
                System.out.println("Title: " + metadata.getTitle());
                System.out.println("Description: " + metadata.getDescription());
                System.out.println("OG Title: " + metadata.getOgTitle());
                System.out.println("OG Description: " + metadata.getOgDescription());
                System.out.println("OG Image: " + metadata.getOgImage());
                System.out.println("Status Badge URL: " + metadata.getStatus().generateUrl());
                System.out.println("Favicon: " + metadata.getFavicon());
                System.out.println("-------------------");
            } catch (Exception e) {
                System.out.println("Error processing website: " + website);
                System.out.println("Error: " + e.getMessage());
                System.out.println("-------------------");
            }
        }
    }

    /**
     * Get website metadata including title, description, Open Graph tags, and status
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
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
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
     *<p>
     *    <ul>
     *        <li>
            <b>STATUS_OK</b>: Status code 200-299, indicates website is normal</li>
     *        <li>
            <b>STATUS_WARNING</b>: Status code 300-499, indicates redirect or client error</li>
     *        <li>
            <b>STATUS_ERROR</b>: Status code 500+ or request exception, indicates server error</li>
     * 
    </ul>
     */
    public static Badge checkWebsiteStatus(String url) {
        try {
            // Send HTTP GET request
            HttpResponse<String> response = Unirest.get(url)
                    // Set User-Agent to avoid being blocked
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
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
