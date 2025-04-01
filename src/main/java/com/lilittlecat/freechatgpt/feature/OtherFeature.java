package com.lilittlecat.freechatgpt.feature;

import com.lilittlecat.freechatgpt.Score;
import com.lilittlecat.freechatgpt.badge.Badge;
import com.lilittlecat.freechatgpt.badge.BadgeLogoBase64;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * OtherFeature
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OtherFeature implements Feature {

    public static OtherFeature SPONSORS = new OtherFeature()
            .setLabel("Sponsors")
            .setLabelCN("赞助商")
            .setMessage(null)
            .setMessageCN(null)
            .setLogo(Badge.LOGO_GITHUB_SPONSORS)
            .setLogoColor(Badge.COLOR_WHITE)
            .setColor(Badge.COLOR_PINK)
            .setScore(Score.SPONSORS.getScore());
    public static OtherFeature WEB_ACCESS = new OtherFeature()
            .setLabel("Web Access")
            .setLabelCN("联网")
            .setMessage("Supported")
            .setMessageCN("支持")
            .setLogo(BadgeLogoBase64.WEB_ACCESS)
            .setLogoColor(Badge.COLOR_WHITE)
            .setLabelColor(Badge.COLOR_BLACK)
            .setScore(Score.WEB_ACCESS.getScore());

    public static OtherFeature UPTIME = new OtherFeature()
            .setLabel("Uptime")
            .setLabelCN("添加后存活时间")
            .setMessage("{daysSurvived} days since added")
            .setMessageCN("{daysSurvived} 天")
            .setLogo(BadgeLogoBase64.UPTIME)
            .setLogoColor(Badge.COLOR_WHITE)
            .setLabelColor(Badge.COLOR_BLACK)
            .setScore(null);

    public static OtherFeature DRAWING = new OtherFeature()
            .setLabel("Drawing")
            .setLabelCN("绘图")
            .setMessage("Supported")
            .setMessageCN("支持")
            .setLogo(BadgeLogoBase64.DRAWING)
            .setLogoColor(Badge.COLOR_WHITE)
            .setLabelColor(Badge.COLOR_BLACK)
            .setScore(Score.DRAWING.getScore());

    public static Map<String, OtherFeature> MAP;

    static {
        java.util.HashMap<String, OtherFeature> map = new java.util.HashMap<>();
        map.put("SPONSORS", SPONSORS);
        map.put("WEB_ACCESS", WEB_ACCESS);
        map.put("UPTIME", UPTIME);
        map.put("DRAWING", DRAWING);
        MAP = java.util.Collections.unmodifiableMap(map);
    }

    public static OtherFeature getByName(String name) {
        return MAP.get(name);
    }

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
    public Double getScore() {
        return score;
    }
}
