package com.logistics.ui.components;

import com.logistics.ui.theme.DesignTokens;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * A KPI (Key Performance Indicator) metric card for the dashboard.
 *
 * Design spec:
 *   - Size: flexible width, fixed 100px height
 *   - Layout: icon (left) | label / value / trend (right stack)
 *   - Border-radius: 8px
 *   - Elevation: Level 1 (subtle drop shadow)
 *
 * Usage:
 *   KpiCard card = new KpiCard("Total Companies", "142", "+2 today", KpiCard.Trend.POSITIVE);
 *   dashboardGrid.add(card, 0, 0);
 */
public class KpiCard extends VBox {

    public enum Trend {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }

    private final Label labelNode  = new Label();
    private final Label valueNode  = new Label();
    private final Label trendNode  = new Label();

    /**
     * Constructs a KPI card.
     *
     * @param label     Short descriptive label, e.g. "Total Companies"
     * @param value     Primary metric value, e.g. "142" or "94.2%"
     * @param trend     Supplementary trend text, e.g. "+2 today" or "-1.3%"
     * @param direction Determines text color for the trend line
     */
    public KpiCard(String label, String value, String trend, Trend direction) {
        getStyleClass().add("kpi-card");

        labelNode.setText(label);
        labelNode.getStyleClass().add("kpi-label");

        valueNode.setText(value);
        valueNode.getStyleClass().add("kpi-value");

        trendNode.setText(trend);
        trendNode.getStyleClass().add(
                direction == Trend.POSITIVE ? "kpi-trend-positive"
                : direction == Trend.NEGATIVE ? "kpi-trend-negative"
                : "kpi-label"
        );

        setSpacing(2);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(labelNode, valueNode, trendNode);
        setFillWidth(true);
    }

    // -------------------------------------------------------------------------
    // Fluent update methods (for live data binding)
    // -------------------------------------------------------------------------

    public KpiCard withValue(String value) {
        valueNode.setText(value);
        return this;
    }

    public KpiCard withTrend(String trend, Trend direction) {
        trendNode.setText(trend);
        trendNode.getStyleClass().removeAll("kpi-trend-positive", "kpi-trend-negative", "kpi-label");
        trendNode.getStyleClass().add(
                direction == Trend.POSITIVE ? "kpi-trend-positive"
                : direction == Trend.NEGATIVE ? "kpi-trend-negative"
                : "kpi-label"
        );
        return this;
    }

    // -------------------------------------------------------------------------
    // Factory helpers for common logistics KPIs
    // -------------------------------------------------------------------------

    public static KpiCard forCompanies(int count, String trendText) {
        return new KpiCard("Total Companies", String.valueOf(count), trendText, Trend.POSITIVE);
    }

    public static KpiCard forActiveShipments(int count, String trendText) {
        return new KpiCard("Active Shipments", String.valueOf(count), trendText, Trend.NEUTRAL);
    }

    public static KpiCard forLocations(int count, String trendText) {
        return new KpiCard("Locations", String.valueOf(count), trendText, Trend.POSITIVE);
    }

    public static KpiCard forOnTimeRate(double percent, String trendText, boolean improving) {
        return new KpiCard(
                "On-Time Rate",
                String.format("%.1f%%", percent),
                trendText,
                improving ? Trend.POSITIVE : Trend.NEGATIVE
        );
    }
}
