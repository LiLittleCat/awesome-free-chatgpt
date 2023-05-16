package com.lilittlecat.freechatgpt;

import lombok.Getter;

/**
 * @author <a href="https://github.com/LiLittleCat">LiLittleCat</a>
 * @since 2023/5/7
 */
@Getter
public enum Feature {
    FREE("free", "🎁", 2f),
    FREE_QUOTA("free_quota", "🔓", 1f),
    GPT4_SUPPORTED("gpt4_supported", "💪", 1f),
    MORE_THAN_CHAT("more_than_chat", "🧰", 0.5f),
    LOGIN_REQUIRED("login_required", "🔒", -1f),
    VPN_REQUIRED("vpn_required", "🌎", -1f),
    API_KEY_REQUIRED("api_key_required", "🔑", -1f),
    FOLLOW_ON_WECHAT_REQUIRED("follow_on_wechat_required", "👀", -1f),
    CHARGE_REQUIRED("charge_required", "💰", -2f),
    COMMUNITY_RECOMMENDATION("community_recommendation", "🌟", 0.5f);
    private final String value;
    private final String label;
    private final Float score;

    Feature(String value, String label, Float score) {
        this.value = value;
        this.label = label;
        this.score = score;
    }

}
