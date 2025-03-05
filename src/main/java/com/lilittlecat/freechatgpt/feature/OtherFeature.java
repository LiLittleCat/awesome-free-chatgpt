package com.lilittlecat.freechatgpt.feature;

import com.lilittlecat.freechatgpt.Badge;
import com.lilittlecat.freechatgpt.BadgeLogoBase64;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * OtherFeature
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OtherFeature implements Feature {

    public static final OtherFeature SPONSORS = new OtherFeature()
            .setLabel("Sponsors")
            .setLabelCN("赞助商")
            .setMessage(null)
            .setMessageCN(null)
            .setLogo(Badge.LOGO_GITHUB_SPONSORS)
            .setLogoColor(Badge.COLOR_WHITE)
            .setColor(Badge.COLOR_PINK)
            .setScore(10.0);
    public static final OtherFeature WEB_ACCESS = new OtherFeature()
            .setLabel("Web Access")
            .setLabelCN("联网")
            .setMessage("Supported")
            .setMessageCN("支持")
            .setLogo(BadgeLogoBase64.WEB_ACCESS)
            .setLogoColor(Badge.COLOR_WHITE)
            .setLabelColor(Badge.COLOR_BLACK)
            .setScore(10.0);

    public static final OtherFeature UPTIME = new OtherFeature()
            .setLabel("Uptime")
            .setLabelCN("存活时间")
            .setMessage("x days")
            .setMessageCN("x 天")
            .setLogo(BadgeLogoBase64.UPTIME)
            .setLogoColor(Badge.COLOR_WHITE)
            .setLabelColor(Badge.COLOR_BLACK)
            .setScore(0.0);

    private String label;
    private String labelCN;
    private String message;
    private String messageCN;
    private String color;
    private String logo;
    private String logoColor;
    private String labelColor;
    private Double score;

    @Override
    public Badge getBadge() {
        return new Badge()
                .setLabel(label)
                .setMessage(message)
                .setColor(color)
                .setLogo(logo)
                .setLogoColor(logoColor)
                .setLabelColor(labelColor);
    }

    @Override
    public Badge getBadgeCN() {
        return new Badge()
                .setLabel(labelCN)
                .setMessage(messageCN)
                .setColor(color)
                .setLogo(logo)
                .setLogoColor(logoColor)
                .setLabelColor(labelColor);
    }

    @Override
    public Double getTotalScore() {
        return score;
    }
}
