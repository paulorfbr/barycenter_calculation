package com.logistics.ui.theme;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Central design token registry for the Logistics Management System.
 *
 * All UI components must reference these tokens rather than hardcoding
 * values. This ensures a single source of truth for the visual language
 * and makes theme changes (e.g., dark mode) a controlled, systematic
 * operation rather than a search-and-replace exercise.
 *
 * Token naming convention:
 *   COLOR_[ROLE]_[SHADE]   where shade is 050-900 (lighter to darker)
 *   FONT_[ROLE]
 *   SPACE_[N]              multiples of 4px
 *   RADIUS_[N]
 */
public final class DesignTokens {

    private DesignTokens() {
        // Static constants only — do not instantiate.
    }

    // -------------------------------------------------------------------------
    // Color — Primary (brand blue)
    // -------------------------------------------------------------------------

    /** Sidebar background, darkest chrome. */
    public static final Color COLOR_PRIMARY_900 = Color.web("#0D2137");

    /** Navigation active state, section headers. */
    public static final Color COLOR_PRIMARY_800 = Color.web("#1A3A5C");

    /** Primary button fill, prominent actions. */
    public static final Color COLOR_PRIMARY_700 = Color.web("#1F4E79");

    /** Button hover, link color. */
    public static final Color COLOR_PRIMARY_600 = Color.web("#2E6DA4");

    /** Accent highlights, selected row indicators. */
    public static final Color COLOR_PRIMARY_400 = Color.web("#5B9BD5");

    /** Active sidebar item background tint. */
    public static final Color COLOR_PRIMARY_100 = Color.web("#DDEEFF");

    /** Table alternate row, card background tint. */
    public static final Color COLOR_PRIMARY_050 = Color.web("#F0F7FF");

    // -------------------------------------------------------------------------
    // Color — Neutral (grays)
    // -------------------------------------------------------------------------

    /** Body text, headings. */
    public static final Color COLOR_NEUTRAL_900 = Color.web("#111827");

    /** Secondary text, labels, table headers. */
    public static final Color COLOR_NEUTRAL_700 = Color.web("#374151");

    /** Placeholder text, disabled text. */
    public static final Color COLOR_NEUTRAL_500 = Color.web("#6B7280");

    /** Borders, dividers, separator lines. */
    public static final Color COLOR_NEUTRAL_300 = Color.web("#D1D5DB");

    /** Page/window background. */
    public static final Color COLOR_NEUTRAL_100 = Color.web("#F3F4F6");

    /** Card surfaces, input backgrounds, modal backgrounds. */
    public static final Color COLOR_NEUTRAL_000 = Color.web("#FFFFFF");

    // -------------------------------------------------------------------------
    // Color — Semantic: Success
    // -------------------------------------------------------------------------

    /** Delivered status text, positive metric values. */
    public static final Color COLOR_SUCCESS_600 = Color.web("#16A34A");

    /** Success badge / chip background. */
    public static final Color COLOR_SUCCESS_100 = Color.web("#DCFCE7");

    // -------------------------------------------------------------------------
    // Color — Semantic: Warning
    // -------------------------------------------------------------------------

    /** In-transit status text, caution messages. */
    public static final Color COLOR_WARNING_600 = Color.web("#D97706");

    /** Warning badge background. */
    public static final Color COLOR_WARNING_100 = Color.web("#FEF3C7");

    // -------------------------------------------------------------------------
    // Color — Semantic: Danger
    // -------------------------------------------------------------------------

    /** Errors, overdue shipment text, destructive actions. */
    public static final Color COLOR_DANGER_600 = Color.web("#DC2626");

    /** Danger badge background. */
    public static final Color COLOR_DANGER_100 = Color.web("#FEE2E2");

    // -------------------------------------------------------------------------
    // Color — Semantic: Info
    // -------------------------------------------------------------------------

    /** Informational text, pending status. */
    public static final Color COLOR_INFO_600 = Color.web("#0891B2");

    /** Info badge background. */
    public static final Color COLOR_INFO_100 = Color.web("#CFFAFE");

    // -------------------------------------------------------------------------
    // CSS Hex Strings (for use in JavaFX -fx-background-color and similar)
    // -------------------------------------------------------------------------

    public static final String CSS_PRIMARY_900 = "#0D2137";
    public static final String CSS_PRIMARY_800 = "#1A3A5C";
    public static final String CSS_PRIMARY_700 = "#1F4E79";
    public static final String CSS_PRIMARY_600 = "#2E6DA4";
    public static final String CSS_PRIMARY_400 = "#5B9BD5";
    public static final String CSS_PRIMARY_100 = "#DDEEFF";
    public static final String CSS_PRIMARY_050 = "#F0F7FF";

    public static final String CSS_NEUTRAL_900 = "#111827";
    public static final String CSS_NEUTRAL_700 = "#374151";
    public static final String CSS_NEUTRAL_500 = "#6B7280";
    public static final String CSS_NEUTRAL_300 = "#D1D5DB";
    public static final String CSS_NEUTRAL_100 = "#F3F4F6";
    public static final String CSS_NEUTRAL_000 = "#FFFFFF";

    public static final String CSS_SUCCESS_600 = "#16A34A";
    public static final String CSS_SUCCESS_100 = "#DCFCE7";
    public static final String CSS_WARNING_600 = "#D97706";
    public static final String CSS_WARNING_100 = "#FEF3C7";
    public static final String CSS_DANGER_600  = "#DC2626";
    public static final String CSS_DANGER_100  = "#FEE2E2";
    public static final String CSS_INFO_600    = "#0891B2";
    public static final String CSS_INFO_100    = "#CFFAFE";

    // -------------------------------------------------------------------------
    // Spacing (px values — apply to JavaFX Insets and gaps)
    // -------------------------------------------------------------------------

    public static final double SPACE_1  = 4;
    public static final double SPACE_2  = 8;
    public static final double SPACE_3  = 12;
    public static final double SPACE_4  = 16;
    public static final double SPACE_5  = 20;
    public static final double SPACE_6  = 24;
    public static final double SPACE_8  = 32;
    public static final double SPACE_10 = 40;
    public static final double SPACE_12 = 48;

    // -------------------------------------------------------------------------
    // Border radius
    // -------------------------------------------------------------------------

    public static final double RADIUS_1    = 4;   // Inputs, table cells
    public static final double RADIUS_2    = 6;   // Buttons, badges
    public static final double RADIUS_3    = 8;   // Cards, panels
    public static final double RADIUS_4    = 12;  // Modals, large containers
    public static final double RADIUS_FULL = 9999; // Avatars, pill badges

    // -------------------------------------------------------------------------
    // Typography
    // -------------------------------------------------------------------------

    public static final double FONT_SIZE_DISPLAY = 28;
    public static final double FONT_SIZE_H1      = 22;
    public static final double FONT_SIZE_H2      = 18;
    public static final double FONT_SIZE_H3      = 15;
    public static final double FONT_SIZE_BODY    = 14;
    public static final double FONT_SIZE_SMALL   = 12;
    public static final double FONT_SIZE_MONO    = 13;
    public static final double FONT_SIZE_LABEL   = 11;

    // -------------------------------------------------------------------------
    // Layout constants
    // -------------------------------------------------------------------------

    /** Expanded sidebar width. */
    public static final double SIDEBAR_WIDTH_EXPANDED  = 240;

    /** Collapsed (icon-only) sidebar width. */
    public static final double SIDEBAR_WIDTH_COLLAPSED = 64;

    /** Application top bar height. */
    public static final double TOPBAR_HEIGHT = 56;

    /** Minimum application window width. */
    public static final double WINDOW_MIN_WIDTH = 1024;

    /** Minimum application window height. */
    public static final double WINDOW_MIN_HEIGHT = 768;

    /** Preferred application window width at launch. */
    public static final double WINDOW_DEFAULT_WIDTH = 1440;

    /** Preferred application window height at launch. */
    public static final double WINDOW_DEFAULT_HEIGHT = 900;

    // -------------------------------------------------------------------------
    // Animation durations (milliseconds)
    // -------------------------------------------------------------------------

    public static final double ANIM_BUTTON_HOVER    = 100;
    public static final double ANIM_SIDEBAR_TOGGLE  = 200;
    public static final double ANIM_MODAL_OPEN      = 180;
    public static final double ANIM_MODAL_CLOSE     = 140;
    public static final double ANIM_PAGE_TRANSITION = 150;
    public static final double ANIM_TOAST_SLIDE     = 220;
    public static final double ANIM_CHART_LOAD      = 400;

    // -------------------------------------------------------------------------
    // Helper: convert Color to rgba CSS string
    // -------------------------------------------------------------------------

    /**
     * Converts a JavaFX Color to a CSS rgba() string suitable for use in
     * -fx-background-color and similar properties.
     *
     * @param color   the JavaFX Color
     * @param opacity override opacity (0.0 – 1.0); pass negative to use color's own opacity
     * @return        CSS rgba string, e.g. "rgba(30, 78, 121, 0.5)"
     */
    public static String toRgba(Color color, double opacity) {
        int r = (int) Math.round(color.getRed()   * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue()  * 255);
        double a = opacity < 0 ? color.getOpacity() : Math.min(1.0, Math.max(0.0, opacity));
        return String.format("rgba(%d, %d, %d, %.2f)", r, g, b, a);
    }
}
