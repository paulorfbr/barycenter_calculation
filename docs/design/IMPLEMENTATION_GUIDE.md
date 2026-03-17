# Logistics Management System — Implementation Guide

## Project Structure

```
define-company-logistic-place/
├── src/
│   └── com/logistics/
│       ├── app/
│       │   └── MainApplication.java        ← JavaFX entry point, navigation host
│       └── ui/
│           ├── theme/
│           │   ├── DesignTokens.java        ← All color, spacing, radius, font constants
│           │   └── AppStylesheet.java       ← Master CSS generated from tokens
│           ├── components/
│           │   ├── SidebarNav.java          ← Collapsible navigation panel
│           │   ├── TopBar.java              ← Application header bar
│           │   ├── KpiCard.java             ← Dashboard metric card
│           │   ├── StatusBadge.java         ← Pill-shaped status label
│           │   └── NotificationToast.java   ← Animated toast + ToastHost
│           └── screens/
│               ├── LoginScreen.java         ← Full-window login form
│               ├── DashboardScreen.java     ← SCR-001
│               ├── CompanyListScreen.java   ← SCR-002
│               └── LocationListScreen.java  ← SCR-004 / SCR-005
└── docs/
    └── design/
        ├── DESIGN_SYSTEM.md                 ← Visual language reference
        ├── WIREFRAMES.md                    ← ASCII wireframes for all screens
        └── IMPLEMENTATION_GUIDE.md          ← This file
```

---

## Step 1: Project Setup

### 1.1 Add JavaFX to the Module

Create `module-info.java` at `src/module-info.java`:

```java
module com.logistics {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;       // for LocationListScreen map
    requires javafx.graphics;

    exports com.logistics.app;
}
```

### 1.2 Add Dependencies (Maven or Gradle)

**Maven `pom.xml`:**
```xml
<dependencies>
  <!-- JavaFX -->
  <dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.3</version>
  </dependency>
  <dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21.0.3</version>
  </dependency>
  <dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-web</artifactId>
    <version>21.0.3</version>
  </dependency>

  <!-- ControlsFX — SearchableComboBox, StatusBar, Notifications -->
  <dependency>
    <groupId>org.controlsfx</groupId>
    <artifactId>controlsfx</artifactId>
    <version>11.2.1</version>
  </dependency>

  <!-- Ikonli — icon font integration (Material Design Icons) -->
  <dependency>
    <groupId>org.kordamp.ikonli</groupId>
    <artifactId>ikonli-javafx</artifactId>
    <version>12.3.1</version>
  </dependency>
  <dependency>
    <groupId>org.kordamp.ikonli</groupId>
    <artifactId>ikonli-materialdesign2-pack</artifactId>
    <version>12.3.1</version>
  </dependency>
</dependencies>
```

**Gradle `build.gradle`:**
```groovy
javafx {
    version = "21.0.3"
    modules = ['javafx.controls', 'javafx.fxml', 'javafx.web']
}

dependencies {
    implementation 'org.controlsfx:controlsfx:11.2.1'
    implementation 'org.kordamp.ikonli:ikonli-javafx:12.3.1'
    implementation 'org.kordamp.ikonli:ikonli-materialdesign2-pack:12.3.1'
}
```

### 1.3 JVM Launch Arguments (IntelliJ Run Configuration)

Under Run > Edit Configurations > VM Options:
```
--module-path /path/to/javafx-sdk-21/lib
--add-modules javafx.controls,javafx.fxml,javafx.web
```

Or use the Maven/Gradle JavaFX plugin to handle this automatically.

---

## Step 2: Convert the IML Project to Maven/Gradle

The current project is a plain IML module. To use external dependencies
(JavaFX, ControlsFX, Ikonli) you must convert it.

**IntelliJ steps:**
1. File > New > Project from Existing Sources (or right-click module > Add Framework Support)
2. Select Maven or Gradle
3. Move the existing `src/` content into `src/main/java/`
4. Add the `pom.xml` or `build.gradle` from Step 1.2 above

---

## Step 3: Running the Application

The main class is `com.logistics.app.MainApplication`.

Set it as the run configuration's main class. JavaFX `Application.launch()` is
called from `main()`.

Default login credentials for development (replace with real auth):
- Any email address containing "@"
- Any non-blank password

---

## Step 4: Screens Still to Implement

| Screen ID | Class to Create            | Priority |
|-----------|----------------------------|----------|
| SCR-003   | CompanyDetailScreen.java   | P0       |
| SCR-006   | ShipmentListScreen.java    | P0       |
| SCR-007   | ShipmentDetailScreen.java  | P1       |
| SCR-008   | RouteManagementScreen.java | P1       |
| SCR-009   | WarehouseScreen.java       | P1       |
| SCR-010   | ReportsScreen.java         | P1       |
| SCR-011   | UserManagementScreen.java  | P2       |
| SCR-012   | SettingsScreen.java        | P2       |

All placeholder screens display a "pending" message in the app shell when
navigated to. Implement each by:
1. Creating the class in `com.logistics.ui.screens`
2. Extending `VBox` and adding `getStyleClass().add("content-area")`
3. Adding the case to `MainApplication.resolveScreen()`

---

## Step 5: Connecting to a Data Layer

All screens use simple POJO view-models (e.g., `CompanyRow`, `LocationRow`).
The pattern for connecting a real data service:

```java
// In your service layer (runs on a background thread):
CompanyService service = new CompanyService();
Task<List<CompanyListScreen.CompanyRow>> loadTask = new Task<>() {
    @Override
    protected List<CompanyListScreen.CompanyRow> call() {
        return service.fetchAll().stream()
            .map(c -> new CompanyListScreen.CompanyRow(
                c.getId(), c.getName(), c.getType(),
                c.getLocationCount(), c.getStatus()))
            .toList();
    }
};
loadTask.setOnSucceeded(e -> {
    // Back on the JavaFX Application Thread:
    companyListScreen.getTable().getItems().setAll(loadTask.getValue());
});
new Thread(loadTask).start();
```

For real-time location updates, use a `ScheduledService<Void>` that polls your
backend at a configurable interval and calls `mapWebView.getEngine().executeScript()`
to push updated coordinates to the Leaflet map.

---

## Step 6: Icon Integration (Ikonli)

After adding the Ikonli dependency, replace the text placeholder icons in
`SidebarNav.NavItemButton` with actual icon glyphs:

```java
// Example replacement in NavItemButton constructor:
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;

FontIcon icon = new FontIcon(MaterialDesignD.DOMAIN);
icon.setIconSize(20);
icon.setIconColor(javafx.scene.paint.Color.WHITE);
setGraphic(icon);
```

Available icon codes for this project — see DESIGN_SYSTEM.md section 7.

---

## Step 7: Dark Mode

`DesignTokens.java` contains light-mode values. To implement dark mode:

1. Create `DesignTokensDark.java` with the inverted palette.
2. Add a `ThemeManager` singleton that holds the current theme.
3. In `AppStylesheet.buildCss()`, accept a `boolean darkMode` parameter
   and switch the token set used for each CSS rule.
4. Re-apply the stylesheet to the scene on theme toggle:
   ```java
   scene.getStylesheets().setAll(AppStylesheet.toDataUri(darkMode));
   ```

Trigger this from the Settings screen (`SettingsScreen` > Theme section).

---

## Step 8: Accessibility Checklist

Before shipping each screen, verify:

- [ ] All `Button` nodes have a `setAccessibleText()` set if the label is icon-only
- [ ] All `TableView` columns have descriptive header text
- [ ] All `TextField` nodes have associated `Label` nodes (use `labelFor`)
- [ ] Error states in forms communicate via text, not color alone
- [ ] Focus traversal order matches visual order (check with Tab key)
- [ ] Minimum 44x44px touch target for all interactive elements
- [ ] Color contrast ratio 4.5:1 minimum for body text (verify with Colour Contrast Analyser)

---

## Design Token Quick Reference

| Need                  | Token                              | Value     |
|-----------------------|------------------------------------|-----------|
| Primary action color  | CSS_PRIMARY_700                    | #1F4E79   |
| Page background       | CSS_NEUTRAL_100                    | #F3F4F6   |
| Card background       | CSS_NEUTRAL_000                    | #FFFFFF   |
| Body text             | CSS_NEUTRAL_900                    | #111827   |
| Secondary text        | CSS_NEUTRAL_500                    | #6B7280   |
| Border / divider      | CSS_NEUTRAL_300                    | #D1D5DB   |
| Success green         | CSS_SUCCESS_600                    | #16A34A   |
| Warning amber         | CSS_WARNING_600                    | #D97706   |
| Danger red            | CSS_DANGER_600                     | #DC2626   |
| Sidebar bg            | CSS_PRIMARY_900                    | #0D2137   |
| Standard spacing      | SPACE_4                            | 16px      |
| Card border-radius    | RADIUS_3                           | 8px       |
| Sidebar expanded width| SIDEBAR_WIDTH_EXPANDED             | 240px     |
