package com.logistics.ui.screens;

import com.logistics.application.port.in.CalculateBarycentreUseCase;
import com.logistics.application.port.in.ManageCompanyUseCase;
import com.logistics.application.service.BarycentreService;
import com.logistics.application.service.CompanyService;
import com.logistics.domain.model.Company;
import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.infrastructure.adapter.ServiceLocator;
import com.logistics.ui.components.KpiCard;
import com.logistics.ui.components.NotificationToast;
import com.logistics.ui.components.StatusBadge;
import com.logistics.ui.theme.DesignTokens;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * SCR-NEW: Barycenter Calculation Screen
 *
 * Allows users to:
 *   1. Select a company (whose persisted consumption sites are loaded).
 *   2. Choose algorithm (single-step barycenter vs Weiszfeld iterative).
 *   3. Trigger the calculation on a background thread.
 *   4. View the result: optimal coordinates, weighted tonnage summary, and
 *      an interactive Leaflet map showing input sites + the computed center.
 *
 * Layout:
 *   [Page title row]
 *   [Control bar: company selector, algorithm toggle, Calculate button]
 *   [KPI row: total sites, total tonnage, algorithm, convergence error]
 *   [Split view: result card (left 1/3) + Leaflet map (right 2/3)]
 *   [Input sites table: name, latitude, longitude, tons, distance to center]
 *
 * Integration:
 *   - Reads companies from CompanyService (ServiceLocator).
 *   - Triggers BarycentreService.calculateForCompany() on a JavaFX Task.
 *   - Subscribes to BarycentreCalculatedEvent via InProcessEventPublisher for
 *     live updates when the calculation is triggered from other screens.
 */
public class BarycentreScreen extends VBox {

    // -------------------------------------------------------------------------
    // Services (from ServiceLocator — no field injection framework needed here)
    // -------------------------------------------------------------------------

    private final CompanyService     companyService;
    private final BarycentreService  barycentreService;

    // -------------------------------------------------------------------------
    // Controls
    // -------------------------------------------------------------------------

    private final ComboBox<CompanyItem> companySelector = new ComboBox<>();
    private final ToggleGroup           algorithmToggle = new ToggleGroup();
    private final Button                calculateBtn    = new Button("Calculate");
    private final ProgressIndicator     spinner         = new ProgressIndicator();

    // -------------------------------------------------------------------------
    // KPI cards
    // -------------------------------------------------------------------------

    private final KpiCard kpiSites       = new KpiCard("Input Sites",         "—", "active sites", KpiCard.Trend.NEUTRAL);
    private final KpiCard kpiTons        = new KpiCard("Total Traffic",        "—", "metric tons",  KpiCard.Trend.NEUTRAL);
    private final KpiCard kpiIterations  = new KpiCard("Iterations",           "—", "algorithm",    KpiCard.Trend.NEUTRAL);
    private final KpiCard kpiError       = new KpiCard("Convergence Error",    "—", "km residual",  KpiCard.Trend.NEUTRAL);

    // -------------------------------------------------------------------------
    // Result display
    // -------------------------------------------------------------------------

    private final Label resultLatLabel  = new Label("—");
    private final Label resultLonLabel  = new Label("—");
    private final Label resultAddrLabel = new Label("Optimal position will appear here.");

    // -------------------------------------------------------------------------
    // Input sites table
    // -------------------------------------------------------------------------

    private final TableView<SiteRow> sitesTable = new TableView<>();

    // -------------------------------------------------------------------------
    // Map
    // -------------------------------------------------------------------------

    private WebView mapWebView;
    private WebEngine mapEngine;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BarycentreScreen() {
        ServiceLocator locator = ServiceLocator.getInstance();
        this.companyService   = locator.getCompanyService();
        this.barycentreService = locator.getBarycentreService();

        getStyleClass().add("content-area");
        setSpacing(DesignTokens.SPACE_6);

        buildSitesTable();

        getChildren().addAll(
                buildTitleRow(),
                buildControlBar(),
                buildKpiRow(),
                buildMainContentRow(),
                buildSitesSection()
        );

        loadCompanies();
    }

    // -------------------------------------------------------------------------
    // Section builders
    // -------------------------------------------------------------------------

    private HBox buildTitleRow() {
        Label title = new Label("Barycenter Calculation");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Find the optimal logistics center weighted by traffic volume");
        subtitle.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + "; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox titleStack = new VBox(2, title, subtitle);

        HBox row = new HBox(DesignTokens.SPACE_3, titleStack, spacer);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildControlBar() {
        // Company selector
        companySelector.setPromptText("Select a company...");
        companySelector.setPrefWidth(280);
        companySelector.getStyleClass().add("form-combo");
        companySelector.setOnAction(e -> onCompanySelected());

        // Algorithm toggle
        RadioButton simpleBtn = new RadioButton("Weighted Barycenter");
        simpleBtn.setToggleGroup(algorithmToggle);
        simpleBtn.setSelected(true);
        simpleBtn.setUserData(false);

        RadioButton weiszfeldBtn = new RadioButton("Weiszfeld (Iterative)");
        weiszfeldBtn.setToggleGroup(algorithmToggle);
        weiszfeldBtn.setUserData(true);

        HBox algorithmBox = new HBox(DesignTokens.SPACE_4, simpleBtn, weiszfeldBtn);
        algorithmBox.setAlignment(Pos.CENTER_LEFT);

        Label algoLabel = new Label("Algorithm:");
        algoLabel.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_700 + "; -fx-font-size: 13px;");

        // Calculate button
        calculateBtn.getStyleClass().add("btn-primary");
        calculateBtn.setOnAction(e -> triggerCalculation());

        // Spinner (hidden while idle)
        spinner.setPrefSize(20, 20);
        spinner.setVisible(false);

        HBox bar = new HBox(DesignTokens.SPACE_4,
                companySelector, algoLabel, algorithmBox,
                new Region(), calculateBtn, spinner);
        HBox.setHgrow(bar.getChildren().get(3), Priority.ALWAYS);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("card");
        bar.setPadding(new Insets(DesignTokens.SPACE_4));
        return bar;
    }

    private HBox buildKpiRow() {
        for (KpiCard card : new KpiCard[]{kpiSites, kpiTons, kpiIterations, kpiError}) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }
        return new HBox(DesignTokens.SPACE_4, kpiSites, kpiTons, kpiIterations, kpiError);
    }

    private HBox buildMainContentRow() {
        // Result card (left)
        VBox resultCard = buildResultCard();
        resultCard.setMinWidth(280);
        resultCard.setPrefWidth(300);
        resultCard.setMaxWidth(340);

        // Map (right)
        mapWebView = new WebView();
        mapEngine  = mapWebView.getEngine();
        mapEngine.loadContent(buildEmptyMapHtml());
        HBox.setHgrow(mapWebView, Priority.ALWAYS);

        HBox row = new HBox(DesignTokens.SPACE_4, resultCard, mapWebView);
        row.setFillHeight(true);
        row.setMinHeight(380);
        return row;
    }

    private VBox buildResultCard() {
        VBox card = new VBox(DesignTokens.SPACE_4);
        card.getStyleClass().add("card");

        Label title = new Label("Optimal Position");
        title.getStyleClass().add("section-title");

        Label latLabel = new Label("Latitude");
        latLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + ";");
        resultLatLabel.setStyle("-fx-font-size: 22px; font-weight: 700; -fx-text-fill: "
                + DesignTokens.CSS_NEUTRAL_900 + "; -fx-font-family: 'JetBrains Mono', monospace;");

        Label lonLabel = new Label("Longitude");
        lonLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + ";");
        resultLonLabel.setStyle("-fx-font-size: 22px; font-weight: 700; -fx-text-fill: "
                + DesignTokens.CSS_NEUTRAL_900 + "; -fx-font-family: 'JetBrains Mono', monospace;");

        resultAddrLabel.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + "; -fx-font-size: 12px;");
        resultAddrLabel.setWrapText(true);

        Separator sep = new Separator();

        Label infoLabel = new Label("""
                The optimal position minimises total
                weighted transport distance across
                all consumption sites.""");
        infoLabel.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + "; -fx-font-size: 12px;");
        infoLabel.setWrapText(true);

        card.getChildren().addAll(
                title, latLabel, resultLatLabel,
                lonLabel, resultLonLabel,
                resultAddrLabel, sep, infoLabel);
        return card;
    }

    private VBox buildSitesSection() {
        VBox section = new VBox(DesignTokens.SPACE_4);
        section.getStyleClass().add("card");

        Label title = new Label("Input Consumption Sites");
        title.getStyleClass().add("section-title");

        section.getChildren().addAll(title, sitesTable);
        return section;
    }

    @SuppressWarnings("unchecked")
    private void buildSitesTable() {
        TableColumn<SiteRow, String> colName  = strCol("Site Name",              "name",            180);
        TableColumn<SiteRow, String> colLat   = strCol("Latitude",               "latitude",         90);
        TableColumn<SiteRow, String> colLon   = strCol("Longitude",              "longitude",        90);
        TableColumn<SiteRow, String> colTons  = strCol("Traffic (tons)",          "weightTons",      120);
        TableColumn<SiteRow, String> colDist  = strCol("Distance to Center (km)", "distanceKm",      160);

        sitesTable.getColumns().addAll(colName, colLat, colLon, colTons, colDist);
        sitesTable.getStyleClass().add("data-table");
        sitesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        sitesTable.setFixedCellSize(48);
        sitesTable.setPrefHeight(220);
        sitesTable.setPlaceholder(new Label("Select a company and click Calculate to see input sites."));
    }

    // -------------------------------------------------------------------------
    // Business logic
    // -------------------------------------------------------------------------

    private void loadCompanies() {
        companyService.findByStatus(Company.Status.ACTIVE).forEach(c -> {
            long siteCount = ServiceLocator.getInstance()
                    .getConsumptionSiteService().findActiveByCompany(c.getId()).size();
            companySelector.getItems().add(
                    new CompanyItem(c.getId(), c.getName(), (int) siteCount));
        });
    }

    private void onCompanySelected() {
        CompanyItem selected = companySelector.getValue();
        if (selected == null) return;

        // Load input sites into the table (without running calculation)
        List<ConsumptionSite> sites = ServiceLocator.getInstance()
                .getConsumptionSiteService().findActiveByCompany(selected.companyId());

        sitesTable.getItems().clear();
        sites.forEach(s -> sitesTable.getItems().add(SiteRow.fromSite(s, "—")));

        kpiSites.withValue(String.valueOf(sites.size()))
                .withTrend(selected.name() + " active sites", KpiCard.Trend.NEUTRAL);
    }

    private void triggerCalculation() {
        CompanyItem selected = companySelector.getValue();
        if (selected == null) {
            NotificationToast.show("Select a Company",
                    "Please select a company before calculating.",
                    NotificationToast.Type.WARNING);
            return;
        }

        boolean useIterative = (boolean) algorithmToggle.getSelectedToggle().getUserData();

        calculateBtn.setDisable(true);
        spinner.setVisible(true);

        Task<LogisticsCenter> task = new Task<>() {
            @Override
            protected LogisticsCenter call() {
                return barycentreService.calculateForCompany(
                        CalculateBarycentreUseCase.StoredSitesCommand.defaultFor(
                                selected.companyId())
                );
            }
        };

        task.setOnSucceeded(e -> {
            LogisticsCenter center = task.getValue();
            displayResult(center, selected.name());
            calculateBtn.setDisable(false);
            spinner.setVisible(false);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            NotificationToast.show("Calculation Failed",
                    ex.getMessage(), NotificationToast.Type.ERROR);
            calculateBtn.setDisable(false);
            spinner.setVisible(false);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void displayResult(LogisticsCenter center, String companyName) {
        double lat = center.getOptimalPosition().latitude();
        double lon = center.getOptimalPosition().longitude();

        resultLatLabel.setText(String.format("%.4f°", lat));
        resultLonLabel.setText(String.format("%.4f°", lon));
        resultAddrLabel.setText(center.getOptimalPosition().toDisplayString());

        kpiTons.withValue(String.format("%.0f t", center.getTotalWeightedVolume().tons()))
               .withTrend("total weighted volume", KpiCard.Trend.POSITIVE);
        kpiIterations.withValue(String.valueOf(center.getIterationCount()))
                     .withTrend(center.getAlgorithmDescription(), KpiCard.Trend.NEUTRAL);
        kpiError.withValue(String.format("%.4f km", center.getConvergenceErrorKm()))
                .withTrend("residual at convergence",
                        center.getConvergenceErrorKm() < 0.1 ? KpiCard.Trend.POSITIVE : KpiCard.Trend.NEUTRAL);

        // Reload input sites table with distances
        List<ConsumptionSite> sites = ServiceLocator.getInstance()
                .getConsumptionSiteService().findActiveByCompany(center.getCompanyId());

        sitesTable.getItems().clear();
        sites.forEach(s -> {
            double distKm = center.getOptimalPosition().distanceKmTo(s.getCoordinate());
            sitesTable.getItems().add(SiteRow.fromSite(s, String.format("%.1f", distKm)));
        });

        // Update map
        mapEngine.loadContent(buildResultMapHtml(center, sites, companyName));

        NotificationToast.show(
                "Calculation Complete",
                String.format("Optimal center for %s: %.4f, %.4f",
                        companyName, lat, lon),
                NotificationToast.Type.SUCCESS);
    }

    // -------------------------------------------------------------------------
    // Map HTML generation
    // -------------------------------------------------------------------------

    private String buildEmptyMapHtml() {
        return buildLeafletBase(
                "37.8", "-96",   // center of USA
                "4",             // zoom
                "// No data yet."
        );
    }

    private String buildResultMapHtml(LogisticsCenter center,
                                       List<ConsumptionSite> sites,
                                       String companyName) {
        double lat = center.getOptimalPosition().latitude();
        double lon = center.getOptimalPosition().longitude();

        StringBuilder markers = new StringBuilder();

        // Input site markers (blue)
        for (ConsumptionSite s : sites) {
            double dist = center.getOptimalPosition().distanceKmTo(s.getCoordinate());
            markers.append(String.format(
                "L.circleMarker([%f,%f], {radius:8, color:'#2E6DA4', fillColor:'#5B9BD5', fillOpacity:0.8})"
                + ".addTo(map).bindPopup('<b>%s</b><br>%.0f t<br>%.1f km to center');%n",
                s.getCoordinate().latitude(), s.getCoordinate().longitude(),
                escapeJs(s.getName()), s.getTrafficVolume().tons(), dist));
        }

        // Optimal center marker (red star)
        markers.append(String.format(
            "var centerIcon = L.divIcon({html:'<div style=\"background:#DC2626;width:16px;height:16px;"
            + "border-radius:50%%;border:3px solid white;box-shadow:0 2px 6px rgba(0,0,0,.4);\"></div>',"
            + "iconAnchor:[8,8]});"
            + "L.marker([%f,%f], {icon:centerIcon}).addTo(map)"
            + ".bindPopup('<b>Optimal Center</b><br>%s<br>%.0f t total').openPopup();%n",
            lat, lon, escapeJs(companyName),
            center.getTotalWeightedVolume().tons()));

        // Draw weighted lines from each site to the center
        for (ConsumptionSite s : sites) {
            markers.append(String.format(
                "L.polyline([[%f,%f],[%f,%f]], {color:'#5B9BD5',weight:1,opacity:0.4}).addTo(map);%n",
                s.getCoordinate().latitude(), s.getCoordinate().longitude(), lat, lon));
        }

        return buildLeafletBase(String.valueOf(lat), String.valueOf(lon), "5", markers.toString());
    }

    private String buildLeafletBase(String lat, String lon, String zoom, String markerScript) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8"/>
                  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
                  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                  <style>body{margin:0;padding:0;}#map{width:100%%;height:100vh;}</style>
                </head>
                <body>
                  <div id="map"></div>
                  <script>
                    var map = L.map('map').setView([%s,%s],%s);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                      {attribution:'© OpenStreetMap contributors'}).addTo(map);
                    %s
                  </script>
                </body></html>
                """.formatted(lat, lon, zoom, markerScript);
    }

    private static String escapeJs(String s) {
        return s == null ? "" : s.replace("'", "\\'").replace("\n", " ");
    }

    // -------------------------------------------------------------------------
    // Data models
    // -------------------------------------------------------------------------

    /** ComboBox item carrying company id, name, and active site count. */
    private record CompanyItem(String companyId, String name, int activeSites) {
        @Override public String toString() {
            return name + " (" + activeSites + " sites)";
        }
    }

    /** Table row for the input sites display. */
    public static class SiteRow {
        private final SimpleStringProperty name, latitude, longitude, weightTons, distanceKm;

        public SiteRow(String name, String latitude, String longitude,
                       String weightTons, String distanceKm) {
            this.name        = new SimpleStringProperty(name);
            this.latitude    = new SimpleStringProperty(latitude);
            this.longitude   = new SimpleStringProperty(longitude);
            this.weightTons  = new SimpleStringProperty(weightTons);
            this.distanceKm  = new SimpleStringProperty(distanceKm);
        }

        public static SiteRow fromSite(ConsumptionSite site, String distanceKm) {
            return new SiteRow(
                    site.getName(),
                    String.format("%.4f", site.getCoordinate().latitude()),
                    String.format("%.4f", site.getCoordinate().longitude()),
                    site.getTrafficVolume().toDisplayString(),
                    distanceKm
            );
        }

        public String getName()       { return name.get(); }
        public String getLatitude()   { return latitude.get(); }
        public String getLongitude()  { return longitude.get(); }
        public String getWeightTons() { return weightTons.get(); }
        public String getDistanceKm() { return distanceKm.get(); }
    }

    // -------------------------------------------------------------------------
    // Column factory
    // -------------------------------------------------------------------------

    private static TableColumn<SiteRow, String> strCol(String header, String prop, double minWidth) {
        TableColumn<SiteRow, String> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setMinWidth(minWidth);
        return col;
    }
}
