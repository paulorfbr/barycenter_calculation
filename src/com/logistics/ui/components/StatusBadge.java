package com.logistics.ui.components;

import com.logistics.ui.theme.DesignTokens;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 * A pill-shaped status badge for shipment and entity status display.
 *
 * Design spec:
 *   - Padding: 4px 10px
 *   - Border-radius: 9999px (pill shape)
 *   - Font: 12px, weight 600
 *   - Colors: semantic background + matching foreground per status
 *
 * Usage:
 *   StatusBadge badge = new StatusBadge(ShipmentStatus.DELIVERED);
 *   tableCell.setGraphic(badge);
 */
public class StatusBadge extends Label {

    public enum ShipmentStatus {
        DELIVERED,
        IN_TRANSIT,
        PENDING,
        OVERDUE,
        CANCELLED
    }

    public enum EntityStatus {
        ACTIVE,
        INACTIVE,
        PENDING,
        MAINTENANCE
    }

    public StatusBadge(ShipmentStatus status) {
        super(labelFor(status));
        getStyleClass().addAll("badge", cssBadgeClass(status));
        setAlignment(Pos.CENTER);
        setMinWidth(Label.USE_PREF_SIZE);
    }

    public StatusBadge(EntityStatus status) {
        super(labelFor(status));
        getStyleClass().addAll("badge", cssBadgeClass(status));
        setAlignment(Pos.CENTER);
        setMinWidth(Label.USE_PREF_SIZE);
    }

    // -------------------------------------------------------------------------
    // Label text
    // -------------------------------------------------------------------------

    private static String labelFor(ShipmentStatus status) {
        return switch (status) {
            case DELIVERED  -> "Delivered";
            case IN_TRANSIT -> "In Transit";
            case PENDING    -> "Pending";
            case OVERDUE    -> "Overdue";
            case CANCELLED  -> "Cancelled";
        };
    }

    private static String labelFor(EntityStatus status) {
        return switch (status) {
            case ACTIVE      -> "Active";
            case INACTIVE    -> "Inactive";
            case PENDING     -> "Pending";
            case MAINTENANCE -> "Maintenance";
        };
    }

    // -------------------------------------------------------------------------
    // CSS class mapping
    // -------------------------------------------------------------------------

    private static String cssBadgeClass(ShipmentStatus status) {
        return switch (status) {
            case DELIVERED  -> "badge-delivered";
            case IN_TRANSIT -> "badge-in-transit";
            case PENDING    -> "badge-pending";
            case OVERDUE    -> "badge-overdue";
            case CANCELLED  -> "badge-cancelled";
        };
    }

    private static String cssBadgeClass(EntityStatus status) {
        return switch (status) {
            case ACTIVE      -> "badge-active";
            case INACTIVE    -> "badge-inactive";
            case PENDING     -> "badge-pending";
            case MAINTENANCE -> "badge-warning"; // reuses warning color
        };
    }
}
