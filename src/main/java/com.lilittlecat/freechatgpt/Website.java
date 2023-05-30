package com.lilittlecat.freechatgpt;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="https://github.com/LiLittleCat">LiLittleCat</a>
 * @since 2023/5/16
 */
@Data
public class Website {
    private Integer id;
    private String url;
    private String title;
    private String description;
    private String lang;
    private List<Feature> features;
    private Double score;
    private String addedDate;
    private String updatedDate;
    private String reportedInvalidDate;
    private String reportedInvalidReason;
    private String labels;
    private String customDescription;
    private String customDescriptionEnglish;
    private String sourceInfo;
    private String previewUrl;
    /**
     * 0: normal 1: abnormal
     */
    private Integer status;
    private Object extraInfo;
}
