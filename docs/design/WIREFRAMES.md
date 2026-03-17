# Logistics Management System — Screen Wireframes (ASCII)

All wireframes use a 1280x800 viewport as the canonical reference.
Sidebar is in expanded state (240px) unless noted.

Legend:
  [ ]   Button or clickable control
  [_]   Text input field
  (o)   Radio button
  [x]   Checkbox
  |     Vertical border / column separator
  ---   Horizontal border / row separator
  >>>   Scroll indicator
  ...   Truncated content

---

## SCR-013: Login Screen

```
+------------------------------------------------------------------+
|                                                                  |
|                   [  LOGO  ]                                     |
|                Define Logistics Platform                         |
|                                                                  |
|          +----------------------------------------------+       |
|          |              Sign In                         |       |
|          |                                              |       |
|          |  Email address                               |       |
|          |  [________________________________________]  |       |
|          |                                              |       |
|          |  Password                                    |       |
|          |  [________________________________________]  |       |
|          |                                              |       |
|          |  [x] Remember me        [Forgot password?]  |       |
|          |                                              |       |
|          |  [         Sign In          ]                |       |
|          |                                              |       |
|          |  ----------------------------------------   |       |
|          |  Don't have an account?  [Contact Admin]    |       |
|          +----------------------------------------------+       |
|                                                                  |
|                     Version 1.0.0                                |
+------------------------------------------------------------------+
```

---

## SCR-001: Dashboard

```
+--------+----------------------------------------------------------+
| LOGO   | Breadcrumb: Home > Dashboard     [Search...]  [!][User] |
+--------+----------------------------------------------------------+
|        |                                                          |
| [dash] | Dashboard                         [  Export  ] [Refresh]|
|        |                                                          |
| [comp] | +----------+ +----------+ +----------+ +----------+     |
|        | | COMPANIES| | SHIPMENTS| | LOCATIONS| | ON TIME  |     |
| [locs] | |          | |          | |          | |          |     |
|        | | [icon]   | | [icon]   | | [icon]   | | [icon]   |     |
| [ship] | |  142     | |  1,847   | |  38      | |  94.2%   |     |
|        | | +2 today | | 23 active| | 3 new    | | +1.3%    |     |
| [rout] | +----------+ +----------+ +----------+ +----------+     |
|        |                                                          |
| [ware] | +---------------------------+ +----------------------+  |
|        | | Shipments This Week       | | Recent Activity      |  |
| [rept] | |                           | |                      |  |
|        | | [Bar chart: Mon-Sun]      | | 09:41 Shipment #1823 |  |
| [sett] | |  ^                        | |   Delivered to XYZ   |  |
|        | |  |  [###]      [###]      | |                      |  |
|        | |  | [#####]    [#####]     | | 09:30 New company    |  |
|        | |  | [#####][##][#####][##] | |   Acme Corp added    |  |
|        | |  +--M--T--W--T--F--S--S  | |                      |  |
|        | |  Delivered  In-Transit   | | 09:15 Route updated  |  |
|        | +---------------------------+ |   Route #R-44        |  |
|        |                               |                      |  |
|        | +---------------------------+ | 08:55 Alert: Overdue |  |
|        | | Shipment Status Map       | |   Shipment #1799     |  |
|        | |  [Embedded Map View]      | |                      |  |
|        | |  * active pins shown      | | 08:40 Location #L-12 |  |
|        | |  * color by status        | |   GPS synced OK      |  |
|        | +---------------------------+ +----------------------+  |
|        |                                                          |
|        | +--------------------------------------------------+    |
|        | | Overdue Shipments (3)                            |    |
|        | |--------------------------------------------------|    |
|        | | # | Shipment   | Company   | From  | To | Days  |    |
|        | |---|------------|-----------|-------|----+-------|    |
|        | | 1 | #SHP-1799  | Acme Corp | LAX   | JFK| 3 days|   |
|        | | 2 | #SHP-1755  | GlobalFrt | ORD   | MIA| 2 days|   |
|        | | 3 | #SHP-1701  | FastLog   | SEA   | BOS| 1 day |   |
|        | |                        [View all overdue >>]     |    |
|        | +--------------------------------------------------+    |
+--------+----------------------------------------------------------+
```

---

## SCR-002: Company List

```
+--------+----------------------------------------------------------+
| LOGO   | Breadcrumb: Home > Companies          [Search...][User] |
+--------+----------------------------------------------------------+
|        |                                                          |
| [dash] | Companies                    [+ Add Company] [Export]   |
|        |                                                          |
| [COMP] | +--Filter bar-----------------------------------------+ |
|        | | Status: [All v] | Type: [All v] | [___ Search ___]  | |
| [locs] | +------------------------------------------------------+ |
|        |                                                          |
| [ship] | +------------------------------------------------------+ |
|        | | [x] | Name          | Type    | Locations | Status  | |
| [rout] | |-----|---------------|---------|-----------|---------|  |
|        | | [ ] | Acme Corp     | Shipper | 4         | Active  | |
| [ware] | | [ ] | Global Freight| Carrier | 12        | Active  | |
|        | | [ ] | FastLog Inc   | Both    | 7         | Active  | |
| [rept] | | [ ] | BayArea Trans | Carrier | 2         | Inactive| |
|        | | [ ] | Pacific Ship  | Shipper | 5         | Active  | |
| [sett] | | [ ] | Metro Moves   | Both    | 9         | Active  | |
|        | | [ ] | Apex Logistics| Carrier | 3         | Pending | |
|        | | [ ] | SkyBridge Co  | Shipper | 1         | Active  | |
|        | | ...                                                  | |
|        | |------------------------------------------------------|  |
|        | | [ ] With selected: [Activate] [Deactivate] [Delete] | |
|        | |                  Showing 1-25 of 142  [< 1 2 3 >]   | |
|        | +------------------------------------------------------+ |
+--------+----------------------------------------------------------+
```

---

## SCR-003: Company Detail / Edit

```
+--------+----------------------------------------------------------+
| LOGO   | Breadcrumb: Home > Companies > Acme Corp      [User]    |
+--------+----------------------------------------------------------+
|        |                                                          |
| [dash] | Acme Corp                        [Edit] [Delete] [...]  |
|        |                                                          |
| [COMP] | +-- Tabs: Overview | Locations | Shipments | Contacts --+|
|        |                                                          |
| [locs] | [OVERVIEW TAB ACTIVE]                                    |
|        |                                                          |
| [ship] | +--Company Info---------+ +--Key Metrics-----------+    |
|        | | Company ID: CMP-0042  | | Active Shipments:  23  |    |
| [rout] | | Type:       Shipper   | | Total Shipments: 1,204 |    |
|        | | Status:     Active    | | On-Time Rate:    96.1% |    |
| [ware] | | Created:  2023-01-15  | | Locations:          4  |    |
|        | | Tax ID:   12-3456789  | | Avg. Transit Time: 2.4d|    |
| [rept] | |                       | |                        |    |
|        | | Contact: Jane Smith   | +------------------------+    |
| [sett] | | Email: j@acme.com     |                               |
|        | | Phone: +1-555-0100    | +--Notes------------------+   |
|        | +----------------------+ | No special notes.        |   |
|        |                          +--------------------------+   |
|        | +--Locations (4)--------------------------------------+ |
|        | | Name           | Type      | City    | Status      | |
|        | |----------------|-----------|---------|-------------|  |
|        | | Main Warehouse | Origin    | Los Ang.| Active      | |
|        | | East Hub       | Dest.     | New York| Active      | |
|        | | Midwest Depot  | Both      | Chicago | Active      | |
|        | | South Station  | Dest.     | Miami   | Maintenance | |
|        | +-----------------------------------------------------+ |
|        |                                                          |
|        | +--Recent Shipments-----------------------------------+ |
|        | | #SHP-1823 | East Hub → Main WH  | Delivered | 1d ago| |
|        | | #SHP-1799 | Main WH → East Hub  | Overdue   | 3d    | |
|        | | #SHP-1744 | Midwest → South St. | In Transit| 1d    | |
|        | |                               [View all shipments >>]| |
|        | +-----------------------------------------------------+ |
+--------+----------------------------------------------------------+
```

---

## SCR-004: Location List

```
+--------+----------------------------------------------------------+
| LOGO   | Breadcrumb: Home > Locations           [Search...][User]|
+--------+----------------------------------------------------------+
|        |                                                          |
| [dash] | Locations                   [+ Add Location] [Export]   |
|        |                                                          |
| [comp] | +--Filter bar-----------------------------------------+ |
|        | | Type: [All v] | Status: [All v] | Company: [All v]  | |
| [LOCS] | | [___ Search by name or coordinates _______________]  | |
|        | +------------------------------------------------------+ |
| [ship] |                                                          |
|        | [  List View  ] [  Map View  ]   <-- Toggle            |
| [rout] |                                                          |
| [ware] | +------------------------------------------------------+ |
|        | | Name          | Type    | Company     | Coords      | |
| [rept] | |---------------|---------|-------------|-------------|  |
|        | | Main Warehouse| Warehouse| Acme Corp  | 34.05,-118.2| |
| [sett] | | East Hub      | Hub     | Acme Corp   | 40.71,-74.00| |
|        | | JFK Cargo Apt | Airport | Global Frgt | 40.63,-73.77| |
|        | | Midwest Depot | Depot   | Acme Corp   | 41.87,-87.62| |
|        | | LAX Cargo     | Airport | FastLog     | 33.94,-118.4| |
|        | | Miami Port    | Port    | BayArea     | 25.77,-80.19| |
|        | |                                                      | |
|        | |                  Showing 1-25 of 38  [< 1 2 >]      | |
|        | +------------------------------------------------------+ |
+--------+----------------------------------------------------------+

[Map View Toggle Active]:

+--------+----------------------------------------------------------+
|        |                                                          |
|        | Locations — Map View      [+ Add Location] [List View]  |
|        |                                                          |
|        | +--Sidebar---------+ +--Map Canvas--------------------+ |
|        | | Filters          | |                                | |
|        | | [___ Search ___] | |    * JFK Cargo (pin)           | |
|        | |                  | |                                | |
|        | | Type             | |         * Midwest Depot        | |
|        | | [x] Warehouse    | |  * LAX Cargo                   | |
|        | | [x] Hub          | |                                | |
|        | | [x] Airport      | |          * Main Warehouse      | |
|        | | [x] Port         | |                                | |
|        | | [x] Depot        | |                                | |
|        | |                  | |  * Miami Port                  | |
|        | | Status           | |                                | |
|        | | [x] Active       | | [Zoom In][Zoom Out][My Layer]  | |
|        | | [ ] Maintenance  | +--------------------------------+ |
|        | | [ ] Inactive     |                                    |
|        | |                  |                                    |
|        | | Selected:        |                                    |
|        | | JFK Cargo Apt    |                                    |
|        | | Global Freight   |                                    |
|        | | 40.63, -73.77    |                                    |
|        | | Airport          |                                    |
|        | | [View Details]   |                                    |
|        | +------------------+                                    |
+--------+----------------------------------------------------------+
```

---

## SCR-006: Shipment List

```
+--------+----------------------------------------------------------+
| LOGO   | Breadcrumb: Home > Shipments          [Search...][User] |
+--------+----------------------------------------------------------+
|        |                                                          |
| [dash] | Shipments              [+ New Shipment] [Import] [Export]|
|        |                                                          |
| [comp] | +--Status Tabs---------------------------------------+  |
|        | | All (1847) | Active (234) | Transit (89) |         |  |
| [locs] | | Delivered (1498) | Overdue (3) | Cancelled (23)    |  |
|        | +----------------------------------------------------+  |
| [SHIP] |                                                          |
|        | +--Filter bar-----------------------------------------+ |
| [rout] | | Date: [Last 30 days v] | Company: [All v] | [Search]| |
|        | +------------------------------------------------------+ |
| [ware] |                                                          |
|        | +------------------------------------------------------+ |
| [rept] | | [x] | Shipment  | Company   |Origin|Dest|Status|ETA | |
|        | |-----|-----------|-----------|------|-----|------|----| |
| [sett] | | [ ] | #SHP-1823 | Acme Corp | LAX  | JFK |[Dlvd]1d  | |
|        | | [ ] | #SHP-1822 | Global F  | ORD  | SEA |[Trns]2d  | |
|        | | [ ] | #SHP-1821 | FastLog   | MIA  | BOS |[Pend]3d  | |
|        | | [ ] | #SHP-1799 | Acme Corp | JFK  | LAX |[Ovrd]--- | |
|        | | [ ] | #SHP-1788 | Global F  | SEA  | ORD |[Trns]1d  | |
|        | | [ ] | #SHP-1744 | FastLog   | BOS  | MIA |[Trns]1d  | |
|        | | [ ] | #SHP-1699 | Acme Corp | LAX  | CHI |[Dlvd]5d  | |
|        | |                                                      | |
|        | | Selected (0) [Bulk Update Status v]  [1-25 of 1847] | |
|        | +------------------------------------------------------+ |
+--------+----------------------------------------------------------+
```

---

## SCR-007: Shipment Detail

```
+--------+----------------------------------------------------------+
| LOGO   | Breadcrumb: Home > Shipments > #SHP-1823      [User]    |
+--------+----------------------------------------------------------+
|        |                                                          |
| [dash] | Shipment #SHP-1823            [Edit] [Duplicate] [...]  |
|        |                                  Status: [Delivered]    |
| [comp] |                                                          |
|        | +--Journey Timeline----------------------------------+  |
| [locs] | |                                                    |  |
|        | | (O)----[====]----(O)----[====]----(O)----[====]---(O)|  |
| [SHIP] | |  |               |               |               |  |  |
|        | | LAX          Chicago          New York         JFK |  |
| [rout] | | Origin        Transit          Transit        Dest. |  |
|        | | 2026-03-08    2026-03-09       2026-03-10   2026-03-10  |
| [ware] | | 09:00         14:22            08:17        11:43   |  |
|        | +----------------------------------------------------+  |
| [rept] |                                                          |
|        | +--Shipment Info-------+ +--Cargo Details-----------+  |
| [sett] | | ID:    #SHP-1823    | | Weight:   142 kg          |  |
|        | | Type:  Standard     | | Volume:   0.8 m3          |  |
|        | | Company: Acme Corp  | | Category: Electronics     |  |
|        | | Created: 2026-03-08 | | Declared: $12,400         |  |
|        | | Carrier: FastLog    | | Insurance: Yes            |  |
|        | +---------------------+ +---------------------------+  |
|        |                                                          |
|        | +--Route Map------------------------------------------+ |
|        | |  [Map showing path: LAX > ORD > EWR > JFK with      | |
|        | |   waypoints marked and completed path highlighted]   | |
|        | +-----------------------------------------------------+ |
|        |                                                          |
|        | +--Event Log-----------------------------------------+  |
|        | | Time             | Event                | Location | |
|        | |------------------|----------------------|----------| |
|        | | 2026-03-10 11:43 | Delivered            | JFK      | |
|        | | 2026-03-10 08:17 | Arrived at transit   | EWR      | |
|        | | 2026-03-09 20:01 | Departed transit     | ORD      | |
|        | | 2026-03-09 14:22 | Arrived at transit   | ORD      | |
|        | | 2026-03-08 09:00 | Picked up / Departed | LAX      | |
|        | +-----------------------------------------------------+ |
+--------+----------------------------------------------------------+
```

---

## SCR-010: Reports and Analytics

```
+--------+----------------------------------------------------------+
| LOGO   | Breadcrumb: Home > Reports             [Search...][User]|
+--------+----------------------------------------------------------+
|        |                                                          |
| [dash] | Reports & Analytics      [Date: Last 30 days v][Export] |
|        |                                                          |
| [comp] | +-- Report Type Tabs --------------------------------+   |
|        | | Performance | Shipment Volume | Company | Geo     |   |
| [locs] | +----------------------------------------------------+   |
|        |                                                          |
| [ship] | +--KPI Row----------------------------------------------+|
|        | | On-Time:94.2% | Avg Transit:2.4d | Delivered:1,498    ||
| [rout] | | Cost/Km:$1.24 | Active Routes:44 | Incidents: 3       ||
|        | +-------------------------------------------------------+|
| [ware] |                                                          |
|        | +--Volume Chart (2/3 width)-+ +--Top Companies (1/3)-+ |
| [REPT] | | [Area chart: Daily        | | 1. Acme Corp    342  | |
|        | |  shipment volume,         | | 2. Global Frt   289  | |
| [sett] | |  30 day window,           | | 3. FastLog Inc  201  | |
|        | |  stacked by status]       | | 4. Pacific Ship 145  | |
|        | |                           | | 5. Metro Moves  138  | |
|        | +---------------------------+ +----------------------+ |
|        |                                                          |
|        | +--Geo Distribution Map (full width)------------------+ |
|        | | [Choropleth / heat map: shipment density by region  | |
|        | |  with origin/destination flow lines overlaid]       | |
|        | +-----------------------------------------------------+ |
|        |                                                          |
|        | +--On-Time Performance Trend--------------------------+ |
|        | | [Line chart: on-time % per week, last 12 weeks,     | |
|        | |  with target line at 95%, tooltips on hover]        | |
|        | +-----------------------------------------------------+ |
+--------+----------------------------------------------------------+
```

---

## SCR-012: System Settings

```
+--------+----------------------------------------------------------+
| LOGO   | Breadcrumb: Home > Settings            [Search...][User]|
+--------+----------------------------------------------------------+
|        |                                                          |
| [dash] | Settings                                                 |
|        |                                                          |
| [comp] | +--Settings Nav (left) ---+ +--Content (right)-------+ |
|        | |                         | |                         | |
| [locs] | | > General               | | General Settings        | |
|        | |   Appearance            | |                         | |
| [ship] | |   Notifications         | | Application Name        | |
|        | |   Data & Import         | | [Define Logistics _____]| |
| [rout] | |   API / Integrations    | |                         | |
|        | |   User Preferences      | | Default Language        | |
| [ware] | |   Security              | | [English (US)        v] | |
|        | |   About                 | |                         | |
| [rept] | |                         | | Default Timezone        | |
|        | |                         | | [UTC-08:00 Pacific   v] | |
| [SETT] | |                         | |                         | |
|        | |                         | | Date Format             | |
|        | |                         | | (o) YYYY-MM-DD          | |
|        | |                         | | ( ) MM/DD/YYYY          | |
|        | |                         | | ( ) DD/MM/YYYY          | |
|        | |                         | |                         | |
|        | |                         | | Theme                   | |
|        | |                         | | (o) Light               | |
|        | |                         | | ( ) Dark                | |
|        | |                         | | ( ) System default      | |
|        | |                         | |                         | |
|        | |                         | | [Save Changes] [Reset]  | |
|        | +-------------------------+ +-------------------------+ |
+--------+----------------------------------------------------------+
```

---

## Modal: Add / Edit Company

```
+------------------------------------------------------------------+
|  OVERLAY (background dimmed 50%)                                |
|  +----------------------------------------------------------+   |
|  | Add Company                                          [X]  |   |
|  |----------------------------------------------------------|   |
|  | Company Name *                                           |   |
|  | [____________________________________________]           |   |
|  |                                                          |   |
|  | Company Type *           Status *                        |   |
|  | [Shipper              v] [Active              v]         |   |
|  |                                                          |   |
|  | Tax ID / Registration Number                             |   |
|  | [____________________________________________]           |   |
|  |                                                          |   |
|  | Primary Contact Name     Primary Contact Email           |   |
|  | [___________________]    [_______________________]       |   |
|  |                                                          |   |
|  | Primary Contact Phone                                    |   |
|  | [____________________________________________]           |   |
|  |                                                          |   |
|  | Notes                                                    |   |
|  | [                                            ]           |   |
|  | [                                            ]           |   |
|  |                                                          |   |
|  |              [Cancel]    [Save Company]                  |   |
|  +----------------------------------------------------------+   |
+------------------------------------------------------------------+
```

---

## Modal: Add / Edit Location

```
+------------------------------------------------------------------+
|  +----------------------------------------------------------+   |
|  | Add Location                                         [X]  |   |
|  |----------------------------------------------------------|   |
|  | Location Name *          Type *                          |   |
|  | [___________________]    [Warehouse           v]         |   |
|  |                                                          |   |
|  | Associated Company *                                     |   |
|  | [Select company...                          v]           |   |
|  |                                                          |   |
|  | Address                                                  |   |
|  | [____________________________________________]           |   |
|  |                                                          |   |
|  | City               State/Region     Country              |   |
|  | [_____________]    [____________]   [___________]        |   |
|  |                                                          |   |
|  | Latitude *                 Longitude *                   |   |
|  | [___________________]      [___________________]         |   |
|  |                     [Pick on Map]                        |   |
|  |                                                          |   |
|  | +-- Mini Map Preview --------------------------------+   |   |
|  | | [Interactive map, click to set pin location      ] |   |   |
|  | +----------------------------------------------------+   |   |
|  |                                                          |   |
|  | Operating Hours        Status *                          |   |
|  | [___________________]  [Active              v]           |   |
|  |                                                          |   |
|  |              [Cancel]    [Save Location]                 |   |
|  +----------------------------------------------------------+   |
+------------------------------------------------------------------+
```
