package com.lilittlecat.freechatgpt.feature;

import com.lilittlecat.freechatgpt.Score;
import com.lilittlecat.freechatgpt.badge.Badge;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Login
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Login implements Feature {

    public static Login LOGIN_NOT_REQUIRED = new Login()
            .setLabel("Login")
            .setLabelCN("登录")
            .setRequirement(Requirement.NOT_REQUIRED);

    public static Login LOGIN_REQUIRED = new Login()
            .setLabel("Login")
            .setLabelCN("登录")
            .setRequirement(Requirement.REQUIRED);

    public static Login LOGIN_OPTIONAL = new Login()
            .setLabel("Login")
            .setLabelCN("登录")
            .setRequirement(Requirement.OPTIONAL);

    public static Map<String, Login> MAP;

    static {
        HashMap<String, Login> map = new HashMap<>();
        map.put("LOGIN_NOT_REQUIRED", LOGIN_NOT_REQUIRED);
        map.put("LOGIN_REQUIRED", LOGIN_REQUIRED);
        map.put("LOGIN_OPTIONAL", LOGIN_OPTIONAL);
        MAP = Collections.unmodifiableMap(map);
    }

    public static Login getByName(String name) {
        return MAP.get(name);
    }

    private String label;
    private String labelCN;

    private String logo = Badge.LOGO_SIMPLE_LOGIN;
    private String logoColor = Badge.COLOR_WHITE;
    private String labelColor = Badge.COLOR_BLACK;

    private Requirement requirement;

    @Getter
    public enum Requirement {
        NOT_REQUIRED("Not Required", "不需要", Score.LOGIN_NOT_REQUIRED.getScore(), Badge.COLOR_GREEN),
        OPTIONAL("Optional", "可选", Score.LOGIN_OPTIONAL.getScore(), Badge.COLOR_BLUE),
        REQUIRED("Required", "需要", Score.LOGIN_REQUIRED.getScore(), Badge.COLOR_YELLOW),

        ;
        private final String message;
        private final String messageCN;
        private final Double score;
        private final String color;


        Requirement(String message, String messageCN, double score, String color) {
            this.message = message;
            this.messageCN = messageCN;
            this.score = score;
            this.color = color;
        }
    }

    @Override
    public Badge getBadge() {
        return new Badge()
                .setLabel(label)
                .setMessage(requirement.getMessage())
                .setColor(requirement.getColor())
                .setLogo(logo)
                .setLogoColor(logoColor)
                .setLabelColor(labelColor);
    }

    @Override
    public Badge getBadgeCN() {
        return new Badge()
                .setLabel(labelCN)
                .setMessage(requirement.getMessageCN())
                .setColor(requirement.getColor())
                .setLogo(logo)
                .setLogoColor(logoColor)
                .setLabelColor(labelColor);
    }

    @Override
    public Double getScore() {
        return requirement.getScore();
    }
}
