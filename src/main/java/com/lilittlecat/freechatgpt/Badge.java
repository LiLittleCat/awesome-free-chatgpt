package com.lilittlecat.freechatgpt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.awt.Color;

/**
 * Badge class representing a Shields.io badge
 *
 * @author Yi Liu
 * @since 2025/2/17
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Badge {

    public static final Badge STATUS_WARNING = new Badge()
            .setLabel("Status")
            .setMessage("Warning")
            .setColor("F1C40F")
            .setLogo(BadgeLogoBase64.STATUS_WARNING);

    public static final Badge STATUS_OK = new Badge()
            .setLabel("Status")
            .setMessage("OK")
            .setColor("2ECC71")
            .setLogo(BadgeLogoBase64.STATUS_OK);

    public static final Badge STATUS_ERROR = new Badge()
            .setLabel("Status")
            .setMessage("Error")
            .setColor("E74C3C")
            .setLogo(BadgeLogoBase64.STATUS_ERROR);

    /**
     * The colors of the badge
     * format:
     * https://img.shields.io/badge/{label}-{message}-{color}?logo={logo}&labelColor={labelColor}
     * example:
     * <p>
     * ![Sponsors](https://img.shields.io/badge/Sponsors-F9848C?logo=GitHub-Sponsors&logoColor=white&style=flat-square)
     * </p>
     */
    public static final Color COLOR_GREEN1 = new Color(0x438440);
    public static final Color COLOR_BLUE1 = new Color(0x2479be);
    public static final Color COLOR_YELLOW1= new Color(0xE8BA36);

    public static final String COLOR_GREEN = "438440";
    public static final String COLOR_BLUE = "2479be";
    public static final String COLOR_YELLOW = "E8BA36";
    public static final String COLOR_PINK = "F9848C";
    public static final String COLOR_BLACK = "0D0D0D";
    public static final String COLOR_WHITE = "FFFFFF";

    /**
     * The styles of the badge
     */
    public static final String STYLE_FLAT = "flat";
    public static final String STYLE_FLAT_SQUARE = "flat-square";
    public static final String STYLE_PLASTIC = "plastic";
    public static final String STYLE_FOR_THE_BADGE = "for-the-badge";
    public static final String STYLE_SOCIAL = "social";

    /**
     * The logo names
     */
    public static final String LOGO_OPENAI = "openai";
    public static final String LOGO_CLAUDE = "claude";
    public static final String LOGO_GITHUB_SPONSORS = "GitHub-Sponsors";
    public static final String LOGO_SIMPLE_LOGIN = "simplelogin";

    /**
     * The label text shown on the left side of the badge
     */
    private String label;

    /**
     * The message text shown on the right side of the badge
     */
    private String message;

    /**
     * The color of the badge (right side)
     */
    private String color;

    /**
     * The style of the badge (plastic, flat, flat-square, for-the-badge, social)
     */
    private String style = STYLE_FLAT_SQUARE;

    /**
     * The logo to be displayed in the badge
     */
    private String logo;

    /**
     * The color of the logo
     */
    private String logoColor = COLOR_WHITE;

    /**
     * The label color (left side)
     */
    private String labelColor;

    /**
     * Generate the base URL for the badge
     *
     * @return Shields.io URL
     */
    public String generateUrl() {
        StringBuilder url = new StringBuilder("https://img.shields.io/badge/");

        // URL encode the label and message
        String encodedLabel = null;
        String encodedMessage = null;
        
        if (label != null && !label.isEmpty()) {
            encodedLabel = label.replace(" ", "%20").replace("-", "--");
            url.append(encodedLabel);
        }
        
        if (message != null && !message.isEmpty()) {
            encodedMessage = message.replace(" ", "%20").replace("-", "--");
            url.append("-").append(encodedMessage);
        }
        
        if (color != null && !color.isEmpty()) {
            url.append("-").append(color);
        }

        // Add optional parameters
        if (style != null && !style.equals(STYLE_FLAT)) {
            url.append("?style=").append(style);
        }

        if (logo != null) {
            url.append(url.indexOf("?") == -1 ? "?" : "&")
                    .append("logo=").append(logo);
        }

        if (logoColor != null) {
            url.append("&logoColor=").append(logoColor);
        }

        if (labelColor != null) {
            url.append("&labelColor=").append(labelColor);
        }

        return url.toString();
    }

    /**
     * Generate a Markdown formatted badge
     *
     * @return Markdown formatted badge
     */
    public String toMarkdown() {
        return String.format("![%s](%s)", label, generateUrl());
    }

    /**
     * Generate a Markdown formatted badge with a link
     *
     * @param link The URL to link to when the badge is clicked
     * @return Markdown formatted badge with a link
     */
    public String toMarkdown(String link) {
        return String.format("[![%s](%s)](%s)", label, generateUrl(), link);
    }

    /**
     * Generate an HTML formatted badge
     *
     * @return HTML formatted badge
     */
    public String toHtml() {
        return String.format("<img alt=\"%s\" src=\"%s\">", label, generateUrl());
    }

    /**
     * Generate an HTML formatted badge with a link
     *
     * @param link The URL to link to when the badge is clicked
     * @return HTML formatted badge with a link
     */
    public String toHtml(String link) {
        return String.format("<a href=\"%s\"><img alt=\"%s\" src=\"%s\"></a>",
                link, label, generateUrl());
    }

    /**
     * Generate a BBCode formatted badge
     *
     * @return BBCode formatted badge
     */
    public String toBBCode() {
        return String.format("[img alt=%s]%s[/img]", label, generateUrl());
    }

    /**
     * Generate a BBCode formatted badge with a link
     *
     * @param link The URL to link to when the badge is clicked
     * @return BBCode formatted badge with a link
     */
    public String toBBCode(String link) {
        return String.format("[url=%s][img alt=%s]%s[/img][/url]",
                link, label, generateUrl());
    }

    /**
     * Generate a reStructuredText formatted badge
     *
     * @return reStructuredText formatted badge
     */
    public String toRst() {
        return String.format(".. image:: %s\n   :alt: %s", generateUrl(), label);
    }

    /**
     * Generate a reStructuredText formatted badge with a link
     *
     * @param link The URL to link to when the badge is clicked
     * @return reStructuredText formatted badge with a link
     */
    public String toRst(String link) {
        return String.format(".. image:: %s\n   :alt: %s\n   :target: %s",
                generateUrl(), label, link);
    }

    /**
     * Generate an AsciiDoc formatted badge
     *
     * @return AsciiDoc formatted badge
     */
    public String toAsciiDoc() {
        return String.format("image:%s[\"%s\"]", generateUrl(), label);
    }

    /**
     * Generate an AsciiDoc formatted badge with a link
     *
     * @param link The URL to link to when the badge is clicked
     * @return AsciiDoc formatted badge with a link
     */
    public String toAsciiDoc(String link) {
        return String.format("image:%s[\"%s\",link=\"%s\"]",
                generateUrl(), label, link);
    }
}
