# Logistics Management System — Design System

## Overview

This document defines the complete visual and interaction design language for the
Define Company Logistic Place application. All UI work must reference this document
to maintain visual consistency across every screen and component.

---

## 1. Framework Decision: JavaFX

### Recommendation: JavaFX with Scene Builder

**Rationale:**
- JDK 23 is the target runtime. JavaFX runs cleanly as a separate module on modern JDKs.
- CSS-based styling gives full control over the visual language without painting widgets manually.
- FXML + Scene Builder provides a clean separation between layout and logic (MVC/MVP friendly).
- Rich built-in controls: TableView, TreeView, Charts, Maps (via WebView + Leaflet.js bridge).
- Strong community libraries: ControlsFX, MaterialFX, AtlantaFX (modern flat themes).

**Rejected alternatives:**
- Swing: dated visual model, manual painting required for modern looks, no CSS.
- Web (Electron/Spring Boot + Vaadin): adds deployment complexity for a pure Java project.
- SWT: Eclipse-only ecosystem, heavyweight.

**Recommended dependency stack:**
```
javafx-controls     21+
javafx-fxml         21+
javafx-web          21+   (for embedded map via WebView + Leaflet)
AtlantaFX           2.x   (modern flat theme built on JavaFX CSS)
ControlsFX          11.x  (extra controls: SearchField, StatusBar, Notifications)
Ikonli              12.x  (icon font integration — Material Design Icons)
```

---

## 2. Color System

### Primary Palette

| Token                    | Hex       | Usage                                      |
|--------------------------|-----------|--------------------------------------------|
| `color-primary-900`      | `#0D2137` | Sidebar background, darkest chrome         |
| `color-primary-800`      | `#1A3A5C` | Navigation active state, headers           |
| `color-primary-700`      | `#1F4E79` | Button primary fill                        |
| `color-primary-600`      | `#2E6DA4` | Button hover, link color                   |
| `color-primary-400`      | `#5B9BD5` | Accent highlights, selected row tint       |
| `color-primary-100`      | `#DDEEFF` | Background tint for active sidebar items   |
| `color-primary-050`      | `#F0F7FF` | Table row alternate, card background       |

### Neutral Palette

| Token                    | Hex       | Usage                                      |
|--------------------------|-----------|--------------------------------------------|
| `color-neutral-900`      | `#111827` | Body text                                  |
| `color-neutral-700`      | `#374151` | Secondary text, labels                     |
| `color-neutral-500`      | `#6B7280` | Placeholder, disabled text                 |
| `color-neutral-300`      | `#D1D5DB` | Borders, dividers                          |
| `color-neutral-100`      | `#F3F4F6` | Page background                            |
| `color-neutral-000`      | `#FFFFFF` | Card surface, input background             |

### Semantic Palette

| Token                    | Hex       | Usage                                      |
|--------------------------|-----------|--------------------------------------------|
| `color-success-600`      | `#16A34A` | Delivered status, positive metrics         |
| `color-success-100`      | `#DCFCE7` | Success badge background                   |
| `color-warning-600`      | `#D97706` | In-transit status, warnings                |
| `color-warning-100`      | `#FEF3C7` | Warning badge background                   |
| `color-danger-600`       | `#DC2626` | Errors, overdue shipments                  |
| `color-danger-100`       | `#FEE2E2` | Error badge background                     |
| `color-info-600`         | `#0891B2` | Informational messages                     |
| `color-info-100`         | `#CFFAFE` | Info badge background                      |

### Dark Mode Adaptations

All semantic and primary tokens have dark-mode counterparts. Dark surfaces shift
from white/neutral-100 to neutral-900/800. Text reverses to neutral-000/100.
Shadows are replaced by subtle borders (1px neutral-700).

---

## 3. Typography

### Font Stack

**Primary:** Inter (bundled as a resource font via JavaFX @font-face)
**Fallback:** System UI, Segoe UI, sans-serif
**Monospace:** JetBrains Mono (for IDs, tracking numbers, coordinates)

### Type Scale

| Token          | Size  | Weight | Line Height | Usage                        |
|----------------|-------|--------|-------------|------------------------------|
| `text-display` | 28px  | 700    | 1.2         | Screen titles (rare)         |
| `text-h1`      | 22px  | 700    | 1.3         | Page headings                |
| `text-h2`      | 18px  | 600    | 1.4         | Section headings, card titles|
| `text-h3`      | 15px  | 600    | 1.4         | Sub-section labels           |
| `text-body`    | 14px  | 400    | 1.5         | General body copy            |
| `text-small`   | 12px  | 400    | 1.4         | Table meta, captions         |
| `text-mono`    | 13px  | 400    | 1.5         | Tracking IDs, coordinates    |
| `text-label`   | 11px  | 600    | 1.2         | Form labels, column headers  |

---

## 4. Spacing System

8px base grid. All margins, paddings, and gaps use multiples of 4px.

| Token      | Value | CSS Equivalent |
|------------|-------|----------------|
| `space-1`  | 4px   | 0.25rem        |
| `space-2`  | 8px   | 0.5rem         |
| `space-3`  | 12px  | 0.75rem        |
| `space-4`  | 16px  | 1rem           |
| `space-5`  | 20px  | 1.25rem        |
| `space-6`  | 24px  | 1.5rem         |
| `space-8`  | 32px  | 2rem           |
| `space-10` | 40px  | 2.5rem         |
| `space-12` | 48px  | 3rem           |

---

## 5. Elevation and Shadow

| Level | Usage                          | CSS Box Shadow                              |
|-------|--------------------------------|---------------------------------------------|
| 0     | Flat surface (tables, inputs)  | none                                        |
| 1     | Cards, dropdowns               | 0 1px 3px rgba(0,0,0,0.12)                  |
| 2     | Floating panels, tooltips      | 0 4px 12px rgba(0,0,0,0.15)                 |
| 3     | Modals, dialogs                | 0 8px 24px rgba(0,0,0,0.18)                 |
| 4     | Notifications, toasts          | 0 12px 32px rgba(0,0,0,0.22)                |

---

## 6. Border Radius

| Token      | Value | Usage                           |
|------------|-------|---------------------------------|
| `radius-1` | 4px   | Inputs, table cells             |
| `radius-2` | 6px   | Buttons, badges                 |
| `radius-3` | 8px   | Cards, panels                   |
| `radius-4` | 12px  | Modals, large containers        |
| `radius-full` | 9999px | Avatars, pill badges, FAB    |

---

## 7. Iconography

Use **Ikonli** with the **Material Design Icons** icon pack.

Key icons per domain:

| Domain             | Icon Names (MDI)                                          |
|--------------------|-----------------------------------------------------------|
| Dashboard          | mdi-view-dashboard                                        |
| Companies          | mdi-domain, mdi-office-building                           |
| Shipments          | mdi-truck, mdi-truck-delivery, mdi-truck-fast             |
| Locations          | mdi-map-marker, mdi-map-marker-path, mdi-crosshairs-gps  |
| Routes             | mdi-routes, mdi-map, mdi-directions                       |
| Warehouses         | mdi-warehouse                                             |
| Reports            | mdi-chart-bar, mdi-file-chart, mdi-poll                   |
| Settings           | mdi-cog, mdi-tune                                         |
| Users              | mdi-account, mdi-account-group                            |
| Alerts             | mdi-bell, mdi-alert-circle, mdi-check-circle              |
| Search             | mdi-magnify                                               |
| Filter             | mdi-filter-variant                                        |
| Export             | mdi-export, mdi-download                                  |

Icon sizes: 16px (inline), 20px (button), 24px (nav), 32px (feature icons).

---

## 8. Component Specifications

### 8.1 Navigation Sidebar

- Width: 240px (expanded), 64px (collapsed)
- Background: `color-primary-900`
- Logo area: 64px tall, centered
- Nav items: 48px tall, 16px horizontal padding
- Active item: `color-primary-800` background, left 3px `color-primary-400` accent bar
- Hover item: `color-primary-800` at 50% opacity
- Section label: `text-label`, `color-neutral-500`, 32px tall, 16px padding-top
- Collapse toggle: bottom of sidebar, 48px tall

### 8.2 Top Application Bar

- Height: 56px
- Background: `color-neutral-000`
- Border-bottom: 1px `color-neutral-300`
- Contains: breadcrumb (left), global search (center), notifications + user avatar (right)
- Elevation: Level 1

### 8.3 Button System

Primary Button:
- Background: `color-primary-700`
- Text: `color-neutral-000`, `text-body`, weight 600
- Padding: 10px 20px
- Border-radius: `radius-2`
- Hover: `color-primary-600`
- Active: `color-primary-800`
- Disabled: `color-neutral-300` background, `color-neutral-500` text

Secondary Button:
- Border: 1.5px `color-primary-700`
- Text: `color-primary-700`
- Background: transparent
- Hover: `color-primary-050`

Danger Button:
- Background: `color-danger-600`
- Hover: darken 10%

Icon Button:
- 36px x 36px
- Border-radius: `radius-2`
- Hover: `color-neutral-100`

### 8.4 Form Controls

Text Input:
- Height: 40px
- Border: 1.5px `color-neutral-300`
- Border-radius: `radius-1`
- Padding: 0 12px
- Focus border: `color-primary-600`, no outline ring (use border change only)
- Error border: `color-danger-600`
- Label: above input, `text-label`, `color-neutral-700`

Dropdown/ComboBox:
- Same dimensions as text input
- Dropdown panel: elevation Level 2, `radius-2` at bottom

Date Picker:
- Same as text input with calendar icon right-aligned
- Calendar popup: elevation Level 2

### 8.5 Data Table

- Header row: 40px, `color-neutral-100` background, `text-label`, `color-neutral-700`
- Data row: 48px, `color-neutral-000`
- Alternate row: `color-primary-050`
- Hover row: `color-primary-100`
- Selected row: `color-primary-100`, left 3px accent bar `color-primary-400`
- Cell padding: 0 16px
- Border: 1px `color-neutral-300` (horizontal only between rows)
- Sortable column header: shows sort icon on hover/active

Pagination bar:
- Height: 52px
- Shows: "Showing 1-25 of 142 records"
- Page size selector: 25 / 50 / 100
- Navigation: Previous / page numbers / Next

### 8.6 Status Badge

- Padding: 4px 10px
- Border-radius: `radius-full`
- Font: `text-small`, weight 600
- Uppercase: false

| Status          | Background             | Text                  |
|-----------------|------------------------|-----------------------|
| Delivered       | `color-success-100`    | `color-success-600`   |
| In Transit      | `color-warning-100`    | `color-warning-600`   |
| Pending         | `color-info-100`       | `color-info-600`      |
| Overdue         | `color-danger-100`     | `color-danger-600`    |
| Cancelled       | `color-neutral-100`    | `color-neutral-500`   |

### 8.7 Metric Card (KPI Card)

- Width: flex (fills grid column)
- Height: 100px
- Border-radius: `radius-3`
- Background: `color-neutral-000`
- Elevation: Level 1
- Layout: icon (32px, left accent color) | label (text-small, neutral-500) | value (text-h1, neutral-900) | trend indicator (text-small, semantic color)

### 8.8 Notification Toast

- Width: 340px
- Position: bottom-right, 24px margin
- Elevation: Level 4
- Border-left: 4px semantic color
- Auto-dismiss: 4 seconds (configurable)
- Contains: icon, title, message, close button

---

## 9. Screen Inventory

| Screen ID | Name                    | Priority |
|-----------|-------------------------|----------|
| SCR-001   | Dashboard               | P0       |
| SCR-002   | Company List            | P0       |
| SCR-003   | Company Detail / Edit   | P0       |
| SCR-004   | Location List           | P0       |
| SCR-005   | Location Detail / Map   | P0       |
| SCR-006   | Shipment List           | P0       |
| SCR-007   | Shipment Detail         | P1       |
| SCR-008   | Route Management        | P1       |
| SCR-009   | Warehouse Management    | P1       |
| SCR-010   | Reports & Analytics     | P1       |
| SCR-011   | User Management         | P2       |
| SCR-012   | System Settings         | P2       |
| SCR-013   | Login / Authentication  | P0       |

---

## 10. Accessibility Requirements (WCAG 2.1 AA)

- Minimum contrast ratio: 4.5:1 for normal text, 3:1 for large text and UI components.
- All interactive elements must be keyboard navigable (Tab, Enter, Space, Arrow keys).
- Focus indicators: 2px solid `color-primary-600` offset 2px (never removed, only styled).
- Screen reader support: all controls must carry accessible text (JavaFX accessible API).
- Form errors must be communicated programmatically, not just via color.
- Table headers must be properly associated with cells.
- Minimum touch target: 44x44px for any interactive element.
- Avoid conveying information by color alone — always pair with icon or label.

---

## 11. Responsive Breakpoints

This is a desktop application. The window is resizable with a minimum size of 1024x768.

| Breakpoint | Width Range     | Sidebar Behavior                     |
|------------|-----------------|--------------------------------------|
| Compact    | 1024 – 1280px   | Sidebar collapsed (64px icon-only)   |
| Standard   | 1280 – 1600px   | Sidebar expanded (240px)             |
| Wide       | 1600px+         | Sidebar expanded, content max-width  |

---

## 12. Animation and Motion

Guiding principle: motion communicates state, not decoration. Keep it fast and purposeful.

| Interaction                | Duration | Easing                    |
|----------------------------|----------|---------------------------|
| Button hover/press         | 100ms    | ease-out                  |
| Sidebar expand/collapse    | 200ms    | ease-in-out               |
| Modal open                 | 180ms    | ease-out (scale 0.95 → 1) |
| Modal close                | 140ms    | ease-in (scale 1 → 0.95)  |
| Table row selection        | 80ms     | ease-out                  |
| Toast in/out               | 220ms    | ease-out (slide from right)|
| Page transition            | 150ms    | ease-in-out (fade)         |
| Chart data load            | 400ms    | ease-out                  |

Reduce motion: always check `prefers-reduced-motion` equivalent (JavaFX: respect OS
accessibility settings) and skip non-essential animations.
