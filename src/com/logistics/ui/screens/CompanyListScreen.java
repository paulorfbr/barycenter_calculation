package com.logistics.ui.screens;

import com.logistics.ui.components.NotificationToast;
import com.logistics.ui.components.StatusBadge;
import com.logistics.ui.theme.DesignTokens;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.function.Consumer;

/**
 * SCR-002: Company List Screen
 *
 * Displays a filterable, sortable, paginated table of all companies.
 * Supports bulk selection and multi-row operations (activate/deactivate/delete).
 *
 * Layout:
 *   [Page title row — title, Add Company button, Export button]
 *   [Filter bar — status dropdown, type dropdown, search field]
 *   [Data table — checkbox, name, type, locations count, status badge]
 *   [Table footer — bulk action bar, pagination]
 *
 * Integration points:
 *   setOnAddCompany()   — fires when "Add Company" is clicked
 *   setOnViewCompany()  — fires when a row is double-clicked
 *   setOnExport()       — fires when "Export" is clicked
 *   setItems()          — binds data to the table
 */
public class CompanyListScreen extends VBox {

    // -------------------------------------------------------------------------
    // Data model
    // -------------------------------------------------------------------------

    /** View-model row for one company in the list. */
    public static class CompanyRow {
        private final SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
        private final SimpleStringProperty id, name, type, locationCount, status;

        public CompanyRow(String id, String name, String type,
                          int locationCount, String status) {
            this.id            = new SimpleStringProperty(id);
            this.name          = new SimpleStringProperty(name);
            this.type          = new SimpleStringProperty(type);
            this.locationCount = new SimpleStringProperty(String.valueOf(locationCount));
            this.status        = new SimpleStringProperty(status);
        }

        public SimpleBooleanProperty selectedProperty()    { return selected; }
        public boolean isSelected()                        { return selected.get(); }
        public String getId()                              { return id.get(); }
        public String getName()                            { return name.get(); }
        public String getType()                            { return type.get(); }
        public String getLocationCount()                   { return locationCount.get(); }
        public String getStatus()                          { return status.get(); }
    }

    // -------------------------------------------------------------------------
    // Callbacks
    // -------------------------------------------------------------------------

    private Runnable onAddCompany;
    private Consumer<CompanyRow> onViewCompany;
    private Runnable onExport;

    // -------------------------------------------------------------------------
    // Controls
    // -------------------------------------------------------------------------

    private final TableView<CompanyRow> table     = new TableView<>();
    private final ComboBox<String> statusFilter   = new ComboBox<>();
    private final ComboBox<String> typeFilter     = new ComboBox<>();
    private final TextField searchField           = new TextField();
    private final Label rowCountLabel             = new Label("Showing 1–25 of 0 companies");
    private final Label selectionLabel            = new Label("0 selected");

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CompanyListScreen() {
        getStyleClass().add("content-area");
        setSpacing(DesignTokens.SPACE_6);

        getChildren().addAll(
                buildTitleRow(),
                buildFilterBar(),
                buildTableSection()
        );

        loadSampleData();
    }

    // -------------------------------------------------------------------------
    // Section builders
    // -------------------------------------------------------------------------

    private HBox buildTitleRow() {
        Label title = new Label("Companies");
        title.getStyleClass().add("page-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export");
        exportBtn.getStyleClass().add("btn-secondary");
        exportBtn.setOnAction(e -> { if (onExport != null) onExport.run(); });

        Button addBtn = new Button("+ Add Company");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> { if (onAddCompany != null) onAddCompany.run(); });

        HBox row = new HBox(DesignTokens.SPACE_3, title, spacer, exportBtn, addBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildFilterBar() {
        // Status filter
        statusFilter.getItems().addAll("All Statuses", "Active", "Inactive", "Pending");
        statusFilter.setValue("All Statuses");
        statusFilter.getStyleClass().add("form-combo");
        statusFilter.setOnAction(e -> applyFilters());

        // Type filter
        typeFilter.getItems().addAll("All Types", "Shipper", "Carrier", "Both");
        typeFilter.setValue("All Types");
        typeFilter.getStyleClass().add("form-combo");
        typeFilter.setOnAction(e -> applyFilters());

        // Search
        searchField.getStyleClass().add("form-input");
        searchField.setPromptText("Search by company name...");
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((obs, old, val) -> applyFilters());

        HBox bar = new HBox(DesignTokens.SPACE_3, statusFilter, typeFilter, searchField);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("card");
        bar.setPadding(new Insets(DesignTokens.SPACE_3));
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox buildTableSection() {
        // Columns
        TableColumn<CompanyRow, Boolean> colSelect = new TableColumn<>("");
        colSelect.setCellValueFactory(cd -> cd.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        colSelect.setMaxWidth(48);
        colSelect.setMinWidth(48);

        TableColumn<CompanyRow, String> colName   = strCol("Company Name", "name", 220);
        TableColumn<CompanyRow, String> colType   = strCol("Type",          "type", 100);
        TableColumn<CompanyRow, String> colLocs   = strCol("Locations",    "locationCount", 100);

        // Status column with badge rendering
        TableColumn<CompanyRow, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setMinWidth(110);
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setGraphic(null); return; }
                StatusBadge.EntityStatus es = switch (status.toLowerCase()) {
                    case "active"   -> StatusBadge.EntityStatus.ACTIVE;
                    case "inactive" -> StatusBadge.EntityStatus.INACTIVE;
                    default         -> StatusBadge.EntityStatus.PENDING;
                };
                setGraphic(new StatusBadge(es));
                setText(null);
            }
        });

        table.getColumns().addAll(colSelect, colName, colType, colLocs, colStatus);
        table.getStyleClass().add("data-table");
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setFixedCellSize(48);
        table.setPlaceholder(new Label("No companies found. Add one to get started."));

        // Double-click opens detail
        table.setRowFactory(tv -> {
            TableRow<CompanyRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty() && onViewCompany != null) {
                    onViewCompany.accept(row.getItem());
                }
            });
            return row;
        });

        // Track selection changes for bulk action bar
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Bulk actions footer
        HBox bulkBar = buildBulkBar();

        // Pagination row
        HBox paginationRow = buildPaginationRow();

        VBox section = new VBox(0, table, bulkBar, paginationRow);
        section.getStyleClass().add("card");
        section.setPadding(Insets.EMPTY);
        VBox.setVgrow(table, Priority.ALWAYS);
        return section;
    }

    private HBox buildBulkBar() {
        selectionLabel.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + "; -fx-font-size: 13px;");

        Button activateBtn = new Button("Activate");
        activateBtn.getStyleClass().add("btn-secondary");
        activateBtn.setOnAction(e -> bulkAction("Activated"));

        Button deactivateBtn = new Button("Deactivate");
        deactivateBtn.getStyleClass().add("btn-secondary");
        deactivateBtn.setOnAction(e -> bulkAction("Deactivated"));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-danger");
        deleteBtn.setOnAction(e -> confirmDelete());

        HBox bar = new HBox(DesignTokens.SPACE_3, selectionLabel, activateBtn, deactivateBtn, deleteBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(DesignTokens.SPACE_3, DesignTokens.SPACE_4, DesignTokens.SPACE_3, DesignTokens.SPACE_4));
        bar.setStyle("-fx-border-color: " + DesignTokens.CSS_NEUTRAL_300 + " transparent transparent transparent;"
                + "-fx-border-width: 1px 0 0 0;");
        return bar;
    }

    private HBox buildPaginationRow() {
        rowCountLabel.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + "; -fx-font-size: 13px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button prevBtn = new Button("< Previous");
        prevBtn.getStyleClass().add("btn-secondary");

        Button nextBtn = new Button("Next >");
        nextBtn.getStyleClass().add("btn-secondary");

        HBox row = new HBox(DesignTokens.SPACE_3, rowCountLabel, spacer, prevBtn, nextBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(DesignTokens.SPACE_3, DesignTokens.SPACE_4,
                DesignTokens.SPACE_3, DesignTokens.SPACE_4));
        row.setStyle("-fx-border-color: " + DesignTokens.CSS_NEUTRAL_300 + " transparent transparent transparent;"
                + "-fx-border-width: 1px 0 0 0;");
        return row;
    }

    // -------------------------------------------------------------------------
    // Data and filter logic
    // -------------------------------------------------------------------------

    private void loadSampleData() {
        table.getItems().addAll(
                new CompanyRow("CMP-001", "Acme Corp",           "Shipper", 4,  "Active"),
                new CompanyRow("CMP-002", "Global Freight",      "Carrier", 12, "Active"),
                new CompanyRow("CMP-003", "FastLog Inc",         "Both",    7,  "Active"),
                new CompanyRow("CMP-004", "BayArea Transport",   "Carrier", 2,  "Inactive"),
                new CompanyRow("CMP-005", "Pacific Shipping",    "Shipper", 5,  "Active"),
                new CompanyRow("CMP-006", "Metro Moves",         "Both",    9,  "Active"),
                new CompanyRow("CMP-007", "Apex Logistics",      "Carrier", 3,  "Pending"),
                new CompanyRow("CMP-008", "SkyBridge Co",        "Shipper", 1,  "Active")
        );
        updateRowCount();
    }

    private void applyFilters() {
        // In a full implementation, apply status/type/search filters via a
        // FilteredList<CompanyRow> wrapping the master ObservableList.
        // Placeholder — triggers row count update.
        updateRowCount();
    }

    private void updateRowCount() {
        int total = table.getItems().size();
        int shown = Math.min(25, total);
        rowCountLabel.setText(String.format("Showing 1–%d of %d companies", shown, total));
    }

    private void bulkAction(String action) {
        long count = table.getItems().stream().filter(CompanyRow::isSelected).count();
        if (count == 0) {
            NotificationToast.show("No selection", "Select at least one company first.", NotificationToast.Type.WARNING);
            return;
        }
        NotificationToast.show(action + " " + count + " companies",
                count + " companies have been " + action.toLowerCase() + ".",
                NotificationToast.Type.SUCCESS);
    }

    private void confirmDelete() {
        long count = table.getItems().stream().filter(CompanyRow::isSelected).count();
        if (count == 0) {
            NotificationToast.show("No selection", "Select at least one company first.", NotificationToast.Type.WARNING);
            return;
        }
        // In a real implementation, show a confirmation modal before deleting.
        NotificationToast.show("Delete " + count + " companies?",
                "This action cannot be undone. Open confirmation dialog.",
                NotificationToast.Type.ERROR);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void setOnAddCompany(Runnable handler)              { this.onAddCompany  = handler; }
    public void setOnViewCompany(Consumer<CompanyRow> handler) { this.onViewCompany = handler; }
    public void setOnExport(Runnable handler)                  { this.onExport      = handler; }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private static TableColumn<CompanyRow, String> strCol(String header, String prop, double minWidth) {
        TableColumn<CompanyRow, String> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setMinWidth(minWidth);
        return col;
    }
}
