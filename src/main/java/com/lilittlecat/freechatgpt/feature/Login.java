package com.lilittlecat.freechatgpt.feature;

import com.lilittlecat.freechatgpt.Badge;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Login
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Login implements Feature {

    public static final Login NOT_REQUIRED = new Login()
            .setLabel("Login")
            .setLabelCN("登录")
            .setRequirement(Requirement.NotRequired);

    public static final Login REQUIRED = new Login()
            .setLabel("Login")
            .setLabelCN("登录")
            .setRequirement(Requirement.Required);

    public static final Login OPTIONAL = new Login()
            .setLabel("Login")
            .setLabelCN("登录")
            .setRequirement(Requirement.Optional);

    private String label;
    private String labelCN;

    private String logo = Badge.LOGO_SIMPLE_LOGIN;
    private String logoColor = Badge.COLOR_WHITE;
    private String labelColor = Badge.COLOR_BLACK;

    private Requirement requirement;

    @Getter
    public enum Requirement {
        NotRequired("Not Required", "不需要", 10, Badge.COLOR_GREEN),
        Optional("Optional", "可选", 0, Badge.COLOR_BLUE),
        Required("Required", "需要", -10, Badge.COLOR_YELLOW),

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
    public Double getTotalScore() {
        return requirement.getScore();
    }
}
