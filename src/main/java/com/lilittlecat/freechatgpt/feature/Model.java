package com.lilittlecat.freechatgpt.feature;

import com.lilittlecat.freechatgpt.Badge;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Language model
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Model implements Feature {

    /**
     * Provider, example: openai, claude, etc.
     */
    private String provider;

    /**
     * Name, example: gpt-4, claude-3-5-sonnet, etc.
     */
    private String name;
    private String nameCN;

    /**
     * Score
     */
    private Double score;

    /**
     * Price
     */
    private Price price;

    /**
     * Badge logo, simple logo or a base64 logo
     */
    private String logo;

    /**
     * Badge logo color
     */
    private String logoColor = Badge.COLOR_WHITE;

    /**
     * Badge label color
     */
    private String labelColor = Badge.COLOR_BLACK;

    @Getter
    public enum Price {
        FREE("Free", "免费", 10.0, Badge.COLOR_GREEN),
        LIMITED("Limited", "额度", 5.0, Badge.COLOR_BLUE),
        PAID("Paid", "付费", 2.0, Badge.COLOR_YELLOW),
        ;

        private final String message;
        private final String messageCN;
        private final Double scoreRatio;
        private final String color;

        Price(String message, String messageCN, Double scoreRatio, String color) {
            this.message = message;
            this.messageCN = messageCN;
            this.scoreRatio = scoreRatio;   
            this.color = color;
        }
    }


    @Override
    public Badge getBadge() {
        // ![GPT-4o 额度](https://img.shields.io/badge/GPT--4o-额度-2479be?logo=openai&style=flat-square&labelColor=000000)
        return new Badge()
                .setLabel(nameCN.replaceAll("-", "--"))
                .setMessage(price.getMessage())
                .setColor(price.getColor())
                .setLogo(logo)
                .setLogoColor(logoColor)
                .setLabelColor(labelColor);
    }

    @Override
    public Badge getBadgeCN() {
        return new Badge()
                .setLabel(name.replaceAll("-", "--"))
                .setMessage(price.getMessageCN())
                .setColor(price.getColor())
                .setLogo(logo)
                .setLogoColor(logoColor)
                .setLabelColor(labelColor);
    }

    @Override
    public Double getTotalScore() {
        return score * price.getScoreRatio();
    }
}
