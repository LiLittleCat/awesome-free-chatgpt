package com.lilittlecat.freechatgpt.feature;

import com.lilittlecat.freechatgpt.badge.Badge;

/**
 * Feature
 */
public interface Feature {

    /**
     * Get the badge
     *
     * @return the badge
     */
    Badge getBadge();

    /**
     * Get the badge in Chinese
     *
     * @return the badge in Chinese
     */
    Badge getBadgeCN();

    /**
     * Get the score
     *
     * @return the score
     */
    Double getScore();

}
