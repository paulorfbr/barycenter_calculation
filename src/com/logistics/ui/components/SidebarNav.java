package com.logistics.ui.components;

import com.logistics.ui.theme.DesignTokens;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Application sidebar navigation panel.
 *
 * Design spec:
 *   - Expanded width: 240px, collapsed width: 64px
 *   - Background: color-primary-900 (#0D2137)
 *   - Active item: left 3px accent bar (color-primary-400), darker background
 *   - Collapse toggle at bottom
 *   - Smooth 200ms width transition on collapse/expand
 *   - Tooltips shown for icon-only collapsed items
 *
 * Usage:
 *   SidebarNav nav = new SidebarNav();
 *   nav.addSection("MANAGEMENT");
 *   nav.addItem(new NavItem("Companies", "domain", Screen.COMPANIES));
 *   nav.addItem(new NavItem("Locations", "map-marker", Screen.LOCATIONS));
 *   nav.setOnNavigate(screen -> contentArea.show(screen));
 *   nav.setActiveScreen(Screen.DASHBOARD);
 */
public class SidebarNav extends VBox {

    // -------------------------------------------------------------------------
    // Navigation model
    // -------------------------------------------------------------------------

    /**
     * Enum representing every top-level screen in the application.
     * Passed to navigation listeners when the user clicks a nav item.
     */
    public enum Screen {
        DASHBOARD,
        COMPANIES,
        LOCATIONS,
        SHIPMENTS,
        ROUTES,
        WAREHOUSES,
        REPORTS,
        SETTINGS
    }

    /**
     * Data object describing a single navigation item.
     */
    public record NavItem(String label, String iconName, Screen screen) {}

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private final BooleanProperty collapsed = new SimpleBooleanProperty(false);
    private final ObjectProperty<Screen> activeScreen = new SimpleObjectProperty<>(Screen.DASHBOARD);
    private Consumer<Screen> onNavigate;

    private final List<NavItemButton> navButtons = new ArrayList<>();
    private final VBox logoArea;
    private final VBox itemContainer;
    private final Region spacer;
    private final Button collapseToggle;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public SidebarNav() {
        getStyleClass().add("sidebar");
        setMinWidth(DesignTokens.SIDEBAR_WIDTH_EXPANDED);
        setPrefWidth(DesignTokens.SIDEBAR_WIDTH_EXPANDED);
        setMaxWidth(DesignTokens.SIDEBAR_WIDTH_EXPANDED);

        // Logo area
        logoArea = buildLogoArea();

        // Item container (scrollable if many items)
        itemContainer = new VBox();
        itemContainer.setSpacing(0);

        // Spacer pushes collapse toggle to bottom
        spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Collapse toggle button
        collapseToggle = buildCollapseToggle();

        getChildren().addAll(logoArea, itemContainer, spacer, collapseToggle);

        // React to collapse state changes
        collapsed.addListener((obs, wasCollapsed, isNowCollapsed) -> {
            animateWidthChange(isNowCollapsed);
            navButtons.forEach(btn -> btn.setCollapsed(isNowCollapsed));
        });

        // React to active screen changes
        activeScreen.addListener((obs, old, current) ->
                navButtons.forEach(btn -> btn.setActive(btn.screen() == current))
        );
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Adds a visual section heading (label only, not clickable).
     *
     * @param sectionLabel uppercase section name, e.g. "MANAGEMENT"
     */
    public void addSection(String sectionLabel) {
        Label label = new Label(sectionLabel);
        label.getStyleClass().add("sidebar-section-label");
        label.setMaxWidth(Double.MAX_VALUE);
        itemContainer.getChildren().add(label);
    }

    /**
     * Adds a clickable navigation item to the sidebar.
     *
     * @param item the NavItem descriptor
     */
    public void addItem(NavItem item) {
        NavItemButton btn = new NavItemButton(item, this::handleNavClick);
        btn.setActive(item.screen() == activeScreen.get());
        navButtons.add(btn);
        itemContainer.getChildren().add(btn);
    }

    /**
     * Registers the callback invoked when the user clicks a navigation item.
     *
     * @param handler receives the Screen enum value of the clicked item
     */
    public void setOnNavigate(Consumer<Screen> handler) {
        this.onNavigate = handler;
    }

    /**
     * Programmatically sets the active screen highlight.
     *
     * @param screen the screen to mark as active
     */
    public void setActiveScreen(Screen screen) {
        activeScreen.set(screen);
    }

    /**
     * Collapses or expands the sidebar programmatically.
     *
     * @param collapse true to collapse to icon-only mode
     */
    public void setCollapsed(boolean collapse) {
        collapsed.set(collapse);
    }

    public boolean isCollapsed() {
        return collapsed.get();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private VBox buildLogoArea() {
        VBox area = new VBox();
        area.getStyleClass().add("sidebar-logo-area");
        area.setAlignment(Pos.CENTER_LEFT);
        area.setMinHeight(DesignTokens.TOPBAR_HEIGHT);
        area.setPrefHeight(DesignTokens.TOPBAR_HEIGHT);

        Label logo = new Label("Define Logistics");
        logo.getStyleClass().add("sidebar-logo-text");

        area.getChildren().add(logo);
        return area;
    }

    private Button buildCollapseToggle() {
        Button btn = new Button("<");
        btn.getStyleClass().addAll("btn-icon", "nav-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMinHeight(48);
        btn.setPrefHeight(48);
        btn.setOnAction(e -> setCollapsed(!isCollapsed()));
        return btn;
    }

    private void animateWidthChange(boolean toCollapsed) {
        double targetWidth = toCollapsed
                ? DesignTokens.SIDEBAR_WIDTH_COLLAPSED
                : DesignTokens.SIDEBAR_WIDTH_EXPANDED;

        // JavaFX does not support CSS width transitions; drive via Timeline.
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        Duration.millis(DesignTokens.ANIM_SIDEBAR_TOGGLE),
                        new javafx.animation.KeyValue(prefWidthProperty(), targetWidth,
                                javafx.animation.Interpolator.EASE_BOTH),
                        new javafx.animation.KeyValue(minWidthProperty(), targetWidth,
                                javafx.animation.Interpolator.EASE_BOTH),
                        new javafx.animation.KeyValue(maxWidthProperty(), targetWidth,
                                javafx.animation.Interpolator.EASE_BOTH)
                )
        );
        timeline.play();

        collapseToggle.setText(toCollapsed ? ">" : "<");
    }

    private void handleNavClick(Screen screen) {
        setActiveScreen(screen);
        if (onNavigate != null) {
            onNavigate.accept(screen);
        }
    }

    // -------------------------------------------------------------------------
    // Inner class: NavItemButton
    // -------------------------------------------------------------------------

    /**
     * A single navigation item button within the sidebar.
     * Handles active state, collapsed (icon-only) mode, and tooltips.
     */
    private static class NavItemButton extends Button {

        private final NavItem navItem;
        private boolean isActive = false;

        NavItemButton(NavItem item, Consumer<Screen> onClick) {
            this.navItem = item;
            setText(item.label());
            getStyleClass().add("nav-item");
            setMaxWidth(Double.MAX_VALUE);
            setAlignment(Pos.CENTER_LEFT);
            setMinHeight(48);
            setPrefHeight(48);

            Tooltip.install(this, new Tooltip(item.label()));

            setOnAction(e -> onClick.accept(item.screen()));
        }

        void setActive(boolean active) {
            this.isActive = active;
            getStyleClass().removeAll("nav-item-active");
            if (active) {
                getStyleClass().add("nav-item-active");
            }
        }

        void setCollapsed(boolean collapsed) {
            if (collapsed) {
                // Show only first letter as icon placeholder
                // (replace with actual icon glyph from Ikonli in real integration)
                setText(navItem.label().substring(0, 1).toUpperCase());
                setAlignment(Pos.CENTER);
            } else {
                setText(navItem.label());
                setAlignment(Pos.CENTER_LEFT);
            }
        }

        Screen screen() {
            return navItem.screen();
        }
    }
}
