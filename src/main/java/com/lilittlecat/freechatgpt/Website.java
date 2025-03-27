package com.lilittlecat.freechatgpt;

import com.lilittlecat.freechatgpt.feature.Feature;
import com.lilittlecat.freechatgpt.feature.Model;

import lombok.Data;

import java.util.List;

/**
 * @author <a href="https://github.com/LiLittleCat">LiLittleCat</a>
 * @since 2023/5/16
 */
@Data
public class Website {
    /**
     * Unique identifier for the website
     */
    private Integer id;
    /**
     * URL of the website
     */
    private String url;
    /**
     * Title of the website
     */
    private String title;
    /**
     * Description of the website
     */
    private String description;
    /**
     * Language of the website
     */
    private String lang;
    /**
     * List of features available on the website
     */
    private List<Feature> features;
    /**
     * List of models associated with the website
     */
    private List<Model> models;
    /**
     * List of badges awarded to the website
     */
    private List<Badge> badges;
    /**
     * Score of the website
     */
    private Double score;
    /**
     * Date when the website was added
     */
    private String addedDate;
    /**
     * Date when the website was last updated
     */
    private String updatedDate;
    /**
     * Date when the website was reported as invalid
     */
    private String reportedInvalidDate;
    /**
     * Reason why the website was reported as invalid
     */
    private String reportedInvalidReason;
    /**
     * Labels associated with the website
     */
    private String labels;
    /**
     * Custom description of the website
     */
    private String customDescription;
    /**
     * Custom description of the website in English
     */
    private String customDescriptionEnglish;
    /**
     * Source information of the website
     */
    private String sourceInfo;
    /**
     * URL for previewing the website
     */
    private String previewUrl;
    /**
     * 0: normal 1: abnormal
     */
    private Badge status;
    /**
     * Additional information about the website
     */
    private Object extraInfo;
}
