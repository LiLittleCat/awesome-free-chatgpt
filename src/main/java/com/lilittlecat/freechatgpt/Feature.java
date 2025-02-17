package com.lilittlecat.freechatgpt;

import lombok.Getter;

import java.util.List;

/**
 * Tag class representing a feature tag
 * @author Yi Liu
 * @since 2025/2/17
 */
@Getter
public enum Feature {
//    FREE("free", "🎁", 2f),
    RECOMMENDED("recommended", "👍", 5f),
    FREE("free", "🆓", 2f),
    FREE_QUOTA("free_quota", "🔓", -1f),
    GPT4_SUPPORTED("gpt4_supported", "💪", 1f),
    MORE_THAN_CHAT("more_than_chat", "🧰", 0.5f),
    LOGIN_REQUIRED("login_required", "🔒", -5f),
    VPN_REQUIRED("vpn_required", "🌎", -1f),
    API_KEY_REQUIRED("api_key_required", "🔑", -3f),
    FOLLOW_ON_WECHAT_REQUIRED("follow_on_wechat_required", "👀", -5f),
    CHARGE_REQUIRED("charge_required", "💰", -5f),
    COMMUNITY_RECOMMENDATION("community_recommendation", "🌟", 0.5f),
    SPONSORS("sponsors", "🏆", 10f, new Badge(Badge.COLOR_GREEN, Badge.STYLE_FLAT, Badge.LOGO_GITHUB_SPONSORS)),;

    LOGIN_NOT_REQUIRED("login_not_required", "🔓", 5f),

    private final String value;
    private final String label;
    private final Float score;
    private final Badge badge;
    private final Badge badgeEnglish;

    Feature(String value, String label, Float score, Badge badge) {
        this.value = value;
        this.label = label;
        this.score = score;
        this.badge = badge;
    }

    public static double score(List<Feature> featureList) {
        return featureList.stream().mapToDouble(Feature::getScore).sum();
    }

    public static Feature fromLabel(String label) {
        for (Feature feature : Feature.values()) {
            if (feature.getLabel().equals(label)) {
                return feature;
            }
        }
        return null;
    }

    public static String allLabelString() {
        StringBuilder sb = new StringBuilder();
        for (Feature feature : Feature.values()) {
            sb.append(feature.getLabel());
        }
        return sb.toString();
    }

}
