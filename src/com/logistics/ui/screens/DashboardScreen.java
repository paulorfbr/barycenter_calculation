package com.logistics.ui.screens;

import com.logistics.ui.components.KpiCard;
import com.logistics.ui.components.NotificationToast;
import com.logistics.ui.components.StatusBadge;
import com.logistics.ui.theme.DesignTokens;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/**
 * SCR-001: Dashboard Screen
 *
 * The primary landing screen after login. Displays the top-level operational
 * picture at a glance: KPI cards, a shipment status chart area, a live
 * activity feed, and an overdue shipments table.
 *
 * Layout structure (matches wireframe SCR-001):
 *
 *   [Page title row]
 *   [KPI card row — 4 cards in an HBox]
 *   [Middle row — chart panel (2/3) + activity feed (1/3)]
 *   [Overdue shipments table (full width)]
 *
 * Data integration:
 *   Call refresh(DashboardData) to populate all widgets. This method is
 *   designed to be called from a background service thread via
 *   Platform.runLater() after data loads.
 */
public class DashboardScreen extends VBox {

    // -------------------------------------------------------------------------
    // KPI cards (kept as fields to allow live updates)
    // -------------------------------------------------------------------------

    private final KpiCard kpiCompanies    = KpiCard.forCompanies(0, "");
    private final KpiCard kpiShipments    = KpiCard.forActiveShipments(0, "");
    private final KpiCard kpiLocations    = KpiCard.forLocations(0, "");
    private final KpiCard kpiOnTime       = KpiCard.forOnTimeRate(0, "", true);

    // -------------------------------------------------------------------------
    // Activity feed
    // -------------------------------------------------------------------------

    private final VBox activityFeed       = new VBox(4);

    // -------------------------------------------------------------------------
    // Overdue table
    // -------------------------------------------------------------------------

    private final TableView<OverdueRow> overdueTable = new TableView<>();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DashboardScreen() {
        getStyleClass().add("content-area");
        setSpacing(DesignTokens.SPACE_6);

        getChildren().addAll(
                buildTitleRow(),
                buildKpiRow(),
                buildMiddleRow(),
                buildOverdueSection()
        );

        // Populate with sample data (replace with real data service calls)
        loadSampleData();
    }

    // -------------------------------------------------------------------------
    // Section builders
    // -------------------------------------------------------------------------

    private HBox buildTitleRow() {
        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export");
        exportBtn.getStyleClass().add("btn-secondary");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.getStyleClass().add("btn-primary");
        refreshBtn.setOnAction(e -> loadSampleData());

        HBox row = new HBox(DesignTokens.SPACE_3, title, spacer, exportBtn, refreshBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildKpiRow() {
        // Each card gets equal width via HBox grow
        for (KpiCard card : new KpiCard[]{kpiCompanies, kpiShipments, kpiLocations, kpiOnTime}) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }

        HBox row = new HBox(DesignTokens.SPACE_4,
                kpiCompanies, kpiShipments, kpiLocations, kpiOnTime);
        row.setFillHeight(true);
        return row;
    }

    private HBox buildMiddleRow() {
        // Chart panel (left, 2/3 width)
        VBox chartPanel = buildChartPanel();
        HBox.setHgrow(chartPanel, Priority.ALWAYS);

        // Activity feed (right, 1/3 width)
        VBox feedPanel = buildActivityFeedPanel();
        feedPanel.setMinWidth(280);
        feedPanel.setPrefWidth(280);
        feedPanel.setMaxWidth(340);

        HBox row = new HBox(DesignTokens.SPACE_4, chartPanel, feedPanel);
        row.setFillHeight(true);
        return row;
    }

    private VBox buildChartPanel() {
        VBox panel = new VBox(DesignTokens.SPACE_4);
        panel.getStyleClass().add("card");

        Label title = new Label("Shipments This Week");
        title.getStyleClass().add("section-title");

        // Chart placeholder — integrate javafx.scene.chart.BarChart here
        // with two series: "Delivered" and "In Transit", one bar per day.
        Region chartPlaceholder = new Region();
        chartPlaceholder.setMinHeight(180);
        chartPlaceholder.setStyle(
                "-fx-background-color: " + DesignTokens.CSS_PRIMARY_050 + ";"
                + "-fx-background-radius: 6px;"
        );

        Label placeholderText = new Label("Bar Chart: Mon–Sun shipment volume\n(Integrate javafx.scene.chart.BarChart)");
        placeholderText.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + "; -fx-font-size: 13px;");
        placeholderText.setWrapText(true);

        StackPane chartArea = new StackPane(chartPlaceholder, placeholderText);
        StackPane.setAlignment(placeholderText, Pos.CENTER);
        VBox.setVgrow(chartArea, Priority.ALWAYS);

        panel.getChildren().addAll(title, chartArea);
        return panel;
    }

    private VBox buildActivityFeedPanel() {
        VBox panel = new VBox(DesignTokens.SPACE_3);
        panel.getStyleClass().add("card");

        Label title = new Label("Recent Activity");
        title.getStyleClass().add("section-title");

        activityFeed.setSpacing(0);
        ScrollPane scroll = new ScrollPane(activityFeed);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panel.getChildren().addAll(title, scroll);
        return panel;
    }

    private VBox buildOverdueSection() {
        VBox section = new VBox(DesignTokens.SPACE_4);
        section.getStyleClass().add("card");

        // Section header
        Label title = new Label("Overdue Shipments");
        title.getStyleClass().add("section-title");

        Button viewAll = new Button("View all overdue");
        viewAll.getStyleClass().add("btn-secondary");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(DesignTokens.SPACE_3, title, spacer, viewAll);
        header.setAlignment(Pos.CENTER_LEFT);

        // Table
        buildOverdueTable();
        section.getChildren().addAll(header, overdueTable);
        return section;
    }

    @SuppressWarnings("unchecked")
    private void buildOverdueTable() {
        overdueTable.getStyleClass().add("data-table");
        overdueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        overdueTable.setFixedCellSize(48);
        overdueTable.setPrefHeight(200);

        TableColumn<OverdueRow, String> colId = col("Shipment", "shipmentId", 110);
        TableColumn<OverdueRow, String> colCompany = col("Company", "company", 160);
        TableColumn<OverdueRow, String> colFrom = col("Origin", "origin", 80);
        TableColumn<OverdueRow, String> colTo = col("Destination", "destination", 80);
        TableColumn<OverdueRow, String> colDays = col("Days Overdue", "daysOverdue", 100);

        overdueTable.getColumns().addAll(colId, colCompany, colFrom, colTo, colDays);
    }

    // -------------------------------------------------------------------------
    // Data model for the overdue table
    // -------------------------------------------------------------------------

    /** Simple view-model row for the overdue shipments table. */
    public static class OverdueRow {
        private final String shipmentId, company, origin, destination, daysOverdue;

        public OverdueRow(String shipmentId, String company,
                          String origin, String destination, String daysOverdue) {
            this.shipmentId  = shipmentId;
            this.company     = company;
            this.origin      = origin;
            this.destination = destination;
            this.daysOverdue = daysOverdue;
        }

        public String getShipmentId()  { return shipmentId; }
        public String getCompany()     { return company; }
        public String getOrigin()      { return origin; }
        public String getDestination() { return destination; }
        public String getDaysOverdue() { return daysOverdue; }
    }

    // -------------------------------------------------------------------------
    // Data loading
    // -------------------------------------------------------------------------

    /**
     * Populates the dashboard with representative sample data.
     * In production, replace this with calls to your data service layer,
     * then invoke Platform.runLater(() -> refresh(data)) from the async thread.
     */
    private void loadSampleData() {
        // KPI cards
        kpiCompanies.withValue("142").withTrend("+2 today", KpiCard.Trend.POSITIVE);
        kpiShipments.withValue("1,847").withTrend("23 active", KpiCard.Trend.NEUTRAL);
        kpiLocations.withValue("38").withTrend("3 new this week", KpiCard.Trend.POSITIVE);
        kpiOnTime.withValue("94.2%").withTrend("+1.3% vs last week", KpiCard.Trend.POSITIVE);

        // Activity feed
        activityFeed.getChildren().clear();
        String[][] activities = {
                {"09:41", "Shipment #SHP-1823 delivered to JFK"},
                {"09:30", "New company added: Acme Corp"},
                {"09:15", "Route #R-44 updated by admin"},
                {"08:55", "Alert: Shipment #SHP-1799 is overdue"},
                {"08:40", "Location #L-12 GPS sync completed"}
        };
        for (String[] act : activities) {
            activityFeed.getChildren().add(buildActivityItem(act[0], act[1]));
        }

        // Overdue table
        overdueTable.getItems().clear();
        overdueTable.getItems().addAll(
                new OverdueRow("#SHP-1799", "Acme Corp",     "LAX", "JFK", "3 days"),
                new OverdueRow("#SHP-1755", "Global Freight","ORD", "MIA", "2 days"),
                new OverdueRow("#SHP-1701", "FastLog Inc",   "SEA", "BOS", "1 day")
        );
    }

    // -------------------------------------------------------------------------
    // Component helpers
    // -------------------------------------------------------------------------

    private HBox buildActivityItem(String time, String message) {
        Label timeLabel = new Label(time);
        timeLabel.setStyle(
                "-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + ";"
                + "-fx-font-size: 12px; -fx-min-width: 40px;"
        );

        Label msgLabel = new Label(message);
        msgLabel.setStyle(
                "-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_700 + "; -fx-font-size: 13px;"
        );
        msgLabel.setWrapText(true);
        HBox.setHgrow(msgLabel, Priority.ALWAYS);

        HBox item = new HBox(DesignTokens.SPACE_3, timeLabel, msgLabel);
        item.setAlignment(Pos.TOP_LEFT);
        item.setPadding(new Insets(8, 0, 8, 0));
        item.setStyle("-fx-border-color: transparent transparent "
                + DesignTokens.CSS_NEUTRAL_100 + " transparent; -fx-border-width: 0 0 1px 0;");
        return item;
    }

    /** Convenience column factory. */
    private static <T> TableColumn<OverdueRow, T> col(String header, String property, double minWidth) {
        TableColumn<OverdueRow, T> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setMinWidth(minWidth);
        return col;
    }
}
