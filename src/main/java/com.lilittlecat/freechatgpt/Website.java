package com.lilittlecat.freechatgpt;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author <a href="https://github.com/LiLittleCat">LiLittleCat</a>
 * @since 2023/5/16
 */
@Data
public class Website {
    private String url;
    private String title;
    private String description;
    private String lang;
    private List<Feature> features;
    private Double score;
    private LocalDate addedDate;
    private LocalDate updatedDate;
    private LocalDate reportedInvalidDate;
    private String labels;
    private String customDescription;
    private String sourceInfo;
    /**
     * 0: normal 1: abnormal
     */
    private Integer status;
}
