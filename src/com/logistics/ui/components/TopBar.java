package com.logistics.ui.components;

import com.logistics.ui.theme.DesignTokens;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

/**
 * Application-level top bar (toolbar / header strip).
 *
 * Design spec:
 *   - Height: 56px
 *   - Background: neutral-000 (white)
 *   - Bottom border: 1px neutral-300
 *   - Elevation: Level 1 (subtle shadow)
 *   - Layout (left to right):
 *       [Breadcrumb trail]   [flexible spacer]   [Global search]   [Notification bell]   [User avatar]
 *
 * The breadcrumb is a simple text sequence; for a full clickable breadcrumb
 * implementation the caller should set breadcrumb segments individually.
 *
 * Usage:
 *   TopBar topBar = new TopBar();
 *   topBar.setBreadcrumb("Companies", "Acme Corp");
 *   topBar.setOnSearch(query -> performSearch(query));
 */
public class TopBar extends HBox {

    private final Label breadcrumbLabel  = new Label();
    private final TextField searchField  = new TextField();
    private final Button notificationBtn = new Button("!");
    private final Label userAvatarLabel  = new Label("U");

    private Consumer<String> onSearch;

    public TopBar() {
        getStyleClass().add("topbar");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(DesignTokens.SPACE_4);
        setPadding(new Insets(0, DesignTokens.SPACE_6, 0, DesignTokens.SPACE_6));
        setMinHeight(DesignTokens.TOPBAR_HEIGHT);
        setPrefHeight(DesignTokens.TOPBAR_HEIGHT);

        // Breadcrumb (left)
        breadcrumbLabel.getStyleClass().add("topbar-breadcrumb");
        breadcrumbLabel.setText("Home");

        // Flexible spacer — pushes search and actions to the right
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        // Global search field (center-right)
        searchField.getStyleClass().add("global-search");
        searchField.setPromptText("Search companies, shipments, locations...");
        searchField.setOnAction(e -> {
            if (onSearch != null) onSearch.accept(searchField.getText().trim());
        });

        // Notification button
        notificationBtn.getStyleClass().add("btn-icon");
        notificationBtn.setTooltip(new javafx.scene.control.Tooltip("Notifications"));

        // User avatar (initials badge — replace with profile image in full build)
        userAvatarLabel.getStyleClass().add("badge");
        userAvatarLabel.setStyle(
                "-fx-background-color: " + DesignTokens.CSS_PRIMARY_600 + ";"
                + "-fx-text-fill: white;"
                + "-fx-min-width: 32px; -fx-min-height: 32px;"
                + "-fx-pref-width: 32px; -fx-pref-height: 32px;"
                + "-fx-background-radius: 9999px;"
                + "-fx-alignment: center;"
        );
        userAvatarLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(breadcrumbLabel, leftSpacer, searchField, notificationBtn, userAvatarLabel);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Updates the breadcrumb trail.
     *
     * @param segments ordered breadcrumb segments, e.g. "Home", "Companies", "Acme Corp"
     */
    public void setBreadcrumb(String... segments) {
        if (segments == null || segments.length == 0) {
            breadcrumbLabel.setText("Home");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            sb.append(segments[i]);
            if (i < segments.length - 1) sb.append("  >  ");
        }
        breadcrumbLabel.setText(sb.toString());
    }

    /**
     * Sets the notification badge count on the notification button.
     * Pass 0 to clear the badge.
     *
     * @param count number of unread notifications
     */
    public void setNotificationCount(int count) {
        notificationBtn.setText(count > 0 ? String.valueOf(Math.min(count, 99)) : "");
    }

    /**
     * Sets the initials or name to display in the user avatar.
     *
     * @param initials e.g. "JD" for John Doe
     */
    public void setUserInitials(String initials) {
        userAvatarLabel.setText(initials);
    }

    /**
     * Registers the callback invoked when the user submits a search query.
     *
     * @param handler receives the trimmed, non-empty search string
     */
    public void setOnSearch(Consumer<String> handler) {
        this.onSearch = handler;
    }

    /**
     * Clears the search field programmatically.
     */
    public void clearSearch() {
        searchField.clear();
    }
}
