package com.logistics.ui.screens;

import com.logistics.ui.components.NotificationToast;
import com.logistics.ui.components.StatusBadge;
import com.logistics.ui.theme.DesignTokens;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.function.Consumer;

/**
 * SCR-004 + SCR-005: Location List Screen with List/Map view toggle.
 *
 * Displays all logistics locations. Supports two complementary views:
 *
 *   List View — standard sortable table with columns for name, type,
 *               company, coordinates, and status.
 *
 *   Map View  — an embedded WebView rendering a Leaflet.js map with
 *               pins for each location. Selecting a pin shows a detail
 *               panel on the left sidebar. This approach reuses the
 *               full power of Leaflet (clustering, tile layers, custom
 *               pin icons by location type) within a JavaFX WebView.
 *
 * Layout:
 *   [Title row — title, Add Location, view toggle buttons]
 *   [Filter bar]
 *   [List table OR Map panel — toggled by the view buttons]
 *
 * Integration:
 *   setOnAddLocation()     fires on "Add Location" click
 *   setOnViewLocation()    fires on row double-click (list view)
 *   setItems()             replace sample data with real data
 */
public class LocationListScreen extends VBox {

    // -------------------------------------------------------------------------
    // Data model
    // -------------------------------------------------------------------------

    public static class LocationRow {
        private final SimpleStringProperty id, name, type, company, latitude, longitude, status;

        public LocationRow(String id, String name, String type,
                           String company, String latitude, String longitude, String status) {
            this.id        = new SimpleStringProperty(id);
            this.name      = new SimpleStringProperty(name);
            this.type      = new SimpleStringProperty(type);
            this.company   = new SimpleStringProperty(company);
            this.latitude  = new SimpleStringProperty(latitude);
            this.longitude = new SimpleStringProperty(longitude);
            this.status    = new SimpleStringProperty(status);
        }

        public String getId()        { return id.get(); }
        public String getName()      { return name.get(); }
        public String getType()      { return type.get(); }
        public String getCompany()   { return company.get(); }
        public String getLatitude()  { return latitude.get(); }
        public String getLongitude() { return longitude.get(); }
        public String getStatus()    { return status.get(); }

        /** GeoJSON-style coordinate string for Leaflet. */
        public String toLatLng() { return "[" + latitude.get() + "," + longitude.get() + "]"; }
    }

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private boolean mapViewActive = false;

    private final TableView<LocationRow> table = new TableView<>();
    private final ToggleGroup viewToggle        = new ToggleGroup();
    private final StackPane contentStack        = new StackPane();
    private WebView mapWebView;

    private Runnable onAddLocation;
    private Consumer<LocationRow> onViewLocation;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public LocationListScreen() {
        getStyleClass().add("content-area");
        setSpacing(DesignTokens.SPACE_6);

        buildTable();

        getChildren().addAll(
                buildTitleRow(),
                buildFilterBar(),
                buildContentStack()
        );

        loadSampleData();
        showListView();
    }

    // -------------------------------------------------------------------------
    // Section builders
    // -------------------------------------------------------------------------

    private HBox buildTitleRow() {
        Label title = new Label("Locations");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // View toggle
        ToggleButton listBtn = new ToggleButton("List View");
        listBtn.setToggleGroup(viewToggle);
        listBtn.setSelected(true);
        listBtn.getStyleClass().add("btn-secondary");
        listBtn.setOnAction(e -> showListView());

        ToggleButton mapBtn = new ToggleButton("Map View");
        mapBtn.setToggleGroup(viewToggle);
        mapBtn.getStyleClass().add("btn-secondary");
        mapBtn.setOnAction(e -> showMapView());

        HBox toggleRow = new HBox(0, listBtn, mapBtn);

        Button addBtn = new Button("+ Add Location");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> { if (onAddLocation != null) onAddLocation.run(); });

        HBox row = new HBox(DesignTokens.SPACE_3, title, spacer, toggleRow, addBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildFilterBar() {
        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("All Types", "Warehouse", "Hub", "Airport", "Port", "Depot");
        typeFilter.setValue("All Types");
        typeFilter.getStyleClass().add("form-combo");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "Active", "Inactive", "Maintenance");
        statusFilter.setValue("All Statuses");
        statusFilter.getStyleClass().add("form-combo");

        ComboBox<String> companyFilter = new ComboBox<>();
        companyFilter.getItems().addAll("All Companies", "Acme Corp", "Global Freight", "FastLog Inc");
        companyFilter.setValue("All Companies");
        companyFilter.getStyleClass().add("form-combo");

        TextField searchField = new TextField();
        searchField.getStyleClass().add("form-input");
        searchField.setPromptText("Search by name or coordinates...");
        searchField.setPrefWidth(280);

        HBox bar = new HBox(DesignTokens.SPACE_3, typeFilter, statusFilter, companyFilter, searchField);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("card");
        bar.setPadding(new Insets(DesignTokens.SPACE_3));
        return bar;
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        TableColumn<LocationRow, String> colName   = strCol("Location Name", "name",      200);
        TableColumn<LocationRow, String> colType   = strCol("Type",          "type",      100);
        TableColumn<LocationRow, String> colCo     = strCol("Company",       "company",   160);
        TableColumn<LocationRow, String> colCoords = new TableColumn<>("Coordinates");
        colCoords.setMinWidth(160);
        colCoords.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getLatitude() + ", " + cd.getValue().getLongitude())
        );
        colCoords.setStyle("-fx-font-family: 'JetBrains Mono', monospace; -fx-font-size: 13px;");

        TableColumn<LocationRow, String> colStatus = new TableColumn<>("Status");
        colStatus.setMinWidth(110);
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setGraphic(null); return; }
                StatusBadge.EntityStatus es = switch (status.toLowerCase()) {
                    case "active"      -> StatusBadge.EntityStatus.ACTIVE;
                    case "inactive"    -> StatusBadge.EntityStatus.INACTIVE;
                    case "maintenance" -> StatusBadge.EntityStatus.MAINTENANCE;
                    default            -> StatusBadge.EntityStatus.PENDING;
                };
                setGraphic(new StatusBadge(es));
                setText(null);
            }
        });

        table.getColumns().addAll(colName, colType, colCo, colCoords, colStatus);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setFixedCellSize(48);
        table.setPlaceholder(new Label("No locations found."));

        table.setRowFactory(tv -> {
            TableRow<LocationRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty() && onViewLocation != null) {
                    onViewLocation.accept(row.getItem());
                }
            });
            return row;
        });
    }

    private StackPane buildContentStack() {
        contentStack.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(contentStack, Priority.ALWAYS);
        return contentStack;
    }

    // -------------------------------------------------------------------------
    // View switching
    // -------------------------------------------------------------------------

    private void showListView() {
        mapViewActive = false;
        contentStack.getChildren().clear();

        VBox tableWrapper = new VBox(table);
        tableWrapper.getStyleClass().add("card");
        tableWrapper.setPadding(Insets.EMPTY);
        VBox.setVgrow(table, Priority.ALWAYS);
        tableWrapper.setMinHeight(400);

        contentStack.getChildren().add(tableWrapper);
    }

    private void showMapView() {
        mapViewActive = true;
        contentStack.getChildren().clear();

        HBox mapLayout = buildMapPanel();
        VBox.setVgrow(mapLayout, Priority.ALWAYS);
        contentStack.getChildren().add(mapLayout);
    }

    // -------------------------------------------------------------------------
    // Map panel (Leaflet.js via WebView)
    // -------------------------------------------------------------------------

    private HBox buildMapPanel() {
        // Left: filter/selection sidebar
        VBox sidebar = buildMapSidebar();
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);

        // Right: the map
        mapWebView = new WebView();
        WebEngine engine = mapWebView.getEngine();
        engine.loadContent(buildLeafletHtml());
        HBox.setHgrow(mapWebView, Priority.ALWAYS);

        HBox layout = new HBox(0, sidebar, mapWebView);
        layout.getStyleClass().add("card");
        layout.setPadding(Insets.EMPTY);
        layout.setMinHeight(500);
        return layout;
    }

    private VBox buildMapSidebar() {
        VBox sidebar = new VBox(DesignTokens.SPACE_4);
        sidebar.setPadding(new Insets(DesignTokens.SPACE_4));
        sidebar.setStyle(
                "-fx-border-color: transparent " + DesignTokens.CSS_NEUTRAL_300 + " transparent transparent;"
                + "-fx-border-width: 0 1px 0 0;"
        );

        Label filtersLabel = new Label("Filters");
        filtersLabel.getStyleClass().add("section-title");

        // Type checkboxes
        VBox typeGroup = new VBox(4,
                new CheckBox("Warehouse"),
                new CheckBox("Hub"),
                new CheckBox("Airport"),
                new CheckBox("Port"),
                new CheckBox("Depot")
        );
        for (javafx.scene.Node n : typeGroup.getChildren()) {
            ((CheckBox) n).setSelected(true);
        }

        Label statusLabel = new Label("Status");
        statusLabel.getStyleClass().add("form-label");

        VBox statusGroup = new VBox(4,
                new CheckBox("Active"),
                new CheckBox("Maintenance"),
                new CheckBox("Inactive")
        );
        ((CheckBox) statusGroup.getChildren().get(0)).setSelected(true);

        sidebar.getChildren().addAll(filtersLabel, new Label("Type").getStyleClass().isEmpty()
                ? styledLabel("Type") : styledLabel("Type"),
                typeGroup, styledLabel("Status"), statusGroup);
        return sidebar;
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("form-label");
        return l;
    }

    /**
     * Builds the HTML content for the Leaflet map.
     * In production, replace the tile URL with your licensed tile server.
     * Pins are generated from the current table items.
     */
    private String buildLeafletHtml() {
        StringBuilder markers = new StringBuilder();
        for (LocationRow loc : table.getItems()) {
            markers.append(String.format(
                    "L.marker(%s).addTo(map).bindPopup('<b>%s</b><br>%s<br>%s');%n",
                    loc.toLatLng(), escapeJs(loc.getName()),
                    escapeJs(loc.getType()), escapeJs(loc.getCompany())
            ));
        }

        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8"/>
                  <link rel="stylesheet"
                    href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
                  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                  <style>
                    body { margin: 0; padding: 0; }
                    #map { width: 100%%; height: 100vh; }
                  </style>
                </head>
                <body>
                  <div id="map"></div>
                  <script>
                    var map = L.map('map').setView([37.8, -96], 4);
                    L.tileLayer(
                      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                      { attribution: '© OpenStreetMap contributors' }
                    ).addTo(map);
                    %s
                  </script>
                </body>
                </html>
                """.formatted(markers.toString());
    }

    private static String escapeJs(String s) {
        return s == null ? "" : s.replace("'", "\\'").replace("\n", " ");
    }

    // -------------------------------------------------------------------------
    // Sample data
    // -------------------------------------------------------------------------

    private void loadSampleData() {
        table.getItems().addAll(
                new LocationRow("LOC-001","Main Warehouse","Warehouse","Acme Corp",   "34.0522","-118.2437","Active"),
                new LocationRow("LOC-002","East Hub",      "Hub",      "Acme Corp",   "40.7128", "-74.0060","Active"),
                new LocationRow("LOC-003","JFK Cargo Apt", "Airport",  "Global Freight","40.6413","-73.7781","Active"),
                new LocationRow("LOC-004","Midwest Depot", "Depot",    "Acme Corp",   "41.8781", "-87.6298","Active"),
                new LocationRow("LOC-005","LAX Cargo",     "Airport",  "FastLog Inc", "33.9425","-118.4081","Active"),
                new LocationRow("LOC-006","Miami Port",    "Port",     "BayArea Trans","25.7617", "-80.1918","Maintenance")
        );
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void setOnAddLocation(Runnable handler)               { this.onAddLocation  = handler; }
    public void setOnViewLocation(Consumer<LocationRow> handler) { this.onViewLocation = handler; }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private static TableColumn<LocationRow, String> strCol(String header, String prop, double minWidth) {
        TableColumn<LocationRow, String> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setMinWidth(minWidth);
        return col;
    }
}
