package com.lilittlecat.freechatgpt.website;

import com.lilittlecat.freechatgpt.badge.Badge;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Website metadata class for storing Open Graph and basic meta information
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class WebsiteMetadata {
    // Basic meta information
    private String title;
    private String description;
    private String url;
    
    // Open Graph meta information
    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    private String ogSiteName;
    private String ogType;
    
    // Twitter Card meta information
    private String twitterCard;
    private String twitterTitle;
    private String twitterDescription;
    private String twitterImage;
    private String twitterSite;
    
    // Additional meta information
    private String favicon;
    private Badge status;

    public String getTitle() {
        return title != null ? title :
               ogTitle != null ? ogTitle :
               twitterTitle != null ? twitterTitle : "";
    }

    public String getDescription() {
        return description != null ? description : 
               ogDescription != null ? ogDescription : 
               twitterDescription != null ? twitterDescription : "";
    }
} 