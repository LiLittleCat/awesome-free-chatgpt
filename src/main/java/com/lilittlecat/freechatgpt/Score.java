package com.lilittlecat.freechatgpt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yi Liu
 * @since 2025/4/1
 */
@Getter
public enum Score {

    LOGIN_NOT_REQUIRED(100.0),
    LOGIN_OPTIONAL(0.0),
    LOGIN_REQUIRED(-50.0),


    PRO_MODEL(10.0),
    NORMAL_MODEL(5.0),

    MODEL_FREE(10.0),
    MODEL_LIMITED(5.0),
    MODEL_PAID(2.0),

    SPONSORS(1000.0),
    WEB_ACCESS(10.0),
    DAYS_SURVIVED(0.1),
    DRAWING(10.0),


    ;
    private final Double score;

    Score(Double score) {
        this.score = score;
    }
}
