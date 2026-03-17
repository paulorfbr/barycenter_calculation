import {
  DataService
} from "./chunk-E6436NOF.js";
import {
  CommonModule,
  RouterLink,
  computed,
  inject,
  ɵsetClassDebugInfo,
  ɵɵStandaloneFeature,
  ɵɵadvance,
  ɵɵattribute,
  ɵɵclassMap,
  ɵɵclassProp,
  ɵɵconditional,
  ɵɵdefineComponent,
  ɵɵelement,
  ɵɵelementEnd,
  ɵɵelementStart,
  ɵɵnextContext,
  ɵɵproperty,
  ɵɵrepeater,
  ɵɵrepeaterCreate,
  ɵɵstyleProp,
  ɵɵtemplate,
  ɵɵtext,
  ɵɵtextInterpolate,
  ɵɵtextInterpolate1
} from "./chunk-WAAQAFSM.js";

// src/app/shared/components/kpi-card/kpi-card.component.ts
function KpiCardComponent_Conditional_9_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 7)(1, "span", 8);
    \u0275\u0275text(2);
    \u0275\u0275elementEnd();
    \u0275\u0275text(3);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = \u0275\u0275nextContext();
    \u0275\u0275classProp("trend-up", ctx_r0.trendPositive)("trend-down", !ctx_r0.trendPositive);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate1(" ", ctx_r0.trendPositive ? "trending_up" : "trending_down", " ");
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", ctx_r0.trend, " ");
  }
}
var KpiCardComponent = class _KpiCardComponent {
  constructor() {
    this.trendPositive = true;
    this.iconBg = "var(--color-primary-050)";
    this.iconColor = "var(--color-primary-700)";
  }
  static {
    this.\u0275fac = function KpiCardComponent_Factory(t) {
      return new (t || _KpiCardComponent)();
    };
  }
  static {
    this.\u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({ type: _KpiCardComponent, selectors: [["app-kpi-card"]], inputs: { label: "label", value: "value", icon: "icon", trend: "trend", trendPositive: "trendPositive", iconBg: "iconBg", iconColor: "iconColor" }, standalone: true, features: [\u0275\u0275StandaloneFeature], decls: 10, vars: 9, consts: [["role", "figure", 1, "kpi-card", "card"], ["aria-hidden", "true", 1, "kpi-icon-wrap"], [1, "material-icons", "kpi-icon"], [1, "kpi-content"], [1, "kpi-label"], [1, "kpi-value"], [1, "kpi-trend", 3, "trend-up", "trend-down"], [1, "kpi-trend"], ["aria-hidden", "true", 1, "material-icons"]], template: function KpiCardComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275elementStart(0, "div", 0)(1, "div", 1)(2, "span", 2);
        \u0275\u0275text(3);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(4, "div", 3)(5, "p", 4);
        \u0275\u0275text(6);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(7, "p", 5);
        \u0275\u0275text(8);
        \u0275\u0275elementEnd();
        \u0275\u0275template(9, KpiCardComponent_Conditional_9_Template, 4, 6, "p", 6);
        \u0275\u0275elementEnd()();
      }
      if (rf & 2) {
        \u0275\u0275attribute("aria-label", ctx.label + ": " + ctx.value);
        \u0275\u0275advance();
        \u0275\u0275styleProp("background-color", ctx.iconBg);
        \u0275\u0275advance();
        \u0275\u0275styleProp("color", ctx.iconColor);
        \u0275\u0275advance();
        \u0275\u0275textInterpolate(ctx.icon);
        \u0275\u0275advance(3);
        \u0275\u0275textInterpolate(ctx.label);
        \u0275\u0275advance(2);
        \u0275\u0275textInterpolate(ctx.value);
        \u0275\u0275advance();
        \u0275\u0275conditional(9, ctx.trend ? 9 : -1);
      }
    }, dependencies: [CommonModule], styles: ["\n\n.kpi-card[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-4);\n  padding: var(--space-5);\n  min-height: 100px;\n}\n.kpi-icon-wrap[_ngcontent-%COMP%] {\n  width: 52px;\n  height: 52px;\n  border-radius: var(--radius-3);\n  display: flex;\n  align-items: center;\n  justify-content: center;\n  flex-shrink: 0;\n}\n.kpi-icon[_ngcontent-%COMP%] {\n  font-size: 28px;\n}\n.kpi-content[_ngcontent-%COMP%] {\n  flex: 1;\n  min-width: 0;\n}\n.kpi-label[_ngcontent-%COMP%] {\n  font-size: var(--font-size-label);\n  font-weight: 600;\n  color: var(--color-neutral-500);\n  text-transform: uppercase;\n  letter-spacing: 0.04em;\n  margin-bottom: var(--space-1);\n}\n.kpi-value[_ngcontent-%COMP%] {\n  font-size: var(--font-size-h1);\n  font-weight: 700;\n  color: var(--color-neutral-900);\n  font-variant-numeric: tabular-nums;\n  line-height: 1.1;\n}\n.kpi-trend[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: 3px;\n  margin-top: var(--space-1);\n  font-size: var(--font-size-small);\n  font-weight: 600;\n  .material-icons {\n    font-size: 14px;\n  }\n}\n.trend-up[_ngcontent-%COMP%] {\n  color: var(--color-success-600);\n}\n.trend-down[_ngcontent-%COMP%] {\n  color: var(--color-danger-600);\n}\n/*# sourceMappingURL=kpi-card.component.css.map */"] });
  }
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && \u0275setClassDebugInfo(KpiCardComponent, { className: "KpiCardComponent", filePath: "src\\app\\shared\\components\\kpi-card\\kpi-card.component.ts", lineNumber: 82 });
})();

// src/app/features/dashboard/dashboard.component.ts
var _forTrack0 = ($index, $item) => $item.id;
var _forTrack1 = ($index, $item) => $item.timeLabel + $item.message;
var _forTrack2 = ($index, $item) => $item.logisticsCenterId;
var _forTrack3 = ($index, $item) => $item.shipmentId;
function DashboardComponent_For_63_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 28)(1, "div", 37)(2, "span", 38);
    \u0275\u0275text(3);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(4, "span", 39);
    \u0275\u0275text(5);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(6, "div", 40)(7, "div", 41);
    \u0275\u0275element(8, "div", 42);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(9, "span", 43);
    \u0275\u0275text(10);
    \u0275\u0275elementEnd()()();
  }
  if (rf & 2) {
    const company_r1 = ctx.$implicit;
    const ctx_r1 = \u0275\u0275nextContext();
    \u0275\u0275advance(3);
    \u0275\u0275textInterpolate(company_r1.name);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate1("", company_r1.consumptionSiteCount, " sites");
    \u0275\u0275advance(3);
    \u0275\u0275styleProp("width", company_r1.totalTrafficTons / ctx_r1.maxTons() * 100, "%");
    \u0275\u0275attribute("aria-label", company_r1.totalTrafficTons + " tons");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(ctx_r1.formatTons(company_r1.totalTrafficTons));
  }
}
function DashboardComponent_Conditional_64_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 29);
    \u0275\u0275text(1, "No companies have consumption sites yet. ");
    \u0275\u0275elementStart(2, "a", 44);
    \u0275\u0275text(3, "Add sites");
    \u0275\u0275elementEnd();
    \u0275\u0275text(4, " to begin. ");
    \u0275\u0275elementEnd();
  }
}
function DashboardComponent_For_72_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 33);
    \u0275\u0275element(1, "span", 45);
    \u0275\u0275elementStart(2, "div", 46)(3, "p", 47);
    \u0275\u0275text(4);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "time", 48);
    \u0275\u0275text(6);
    \u0275\u0275elementEnd()()();
  }
  if (rf & 2) {
    const item_r3 = ctx.$implicit;
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate(item_r3.message);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(item_r3.timeLabel);
  }
}
function DashboardComponent_Conditional_80_For_13_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "tr")(1, "td");
    \u0275\u0275text(2);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(3, "td", 50);
    \u0275\u0275text(4);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "td")(6, "span", 51);
    \u0275\u0275text(7);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(8, "td")(9, "span", 52);
    \u0275\u0275text(10);
    \u0275\u0275elementEnd()()();
  }
  if (rf & 2) {
    const r_r4 = ctx.$implicit;
    const ctx_r1 = \u0275\u0275nextContext(2);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(ctx_r1.getCompanyName(r_r4.companyId));
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(r_r4.formattedCoordinate);
    \u0275\u0275advance(3);
    \u0275\u0275textInterpolate1(" ", r_r4.algorithmDescription === "weiszfeld-iterative" ? "Weiszfeld" : "Simple", " ");
    \u0275\u0275advance(2);
    \u0275\u0275classMap(ctx_r1.statusBadgeClass(r_r4.status));
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(r_r4.status);
  }
}
function DashboardComponent_Conditional_80_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "table", 36)(1, "thead")(2, "tr")(3, "th", 49);
    \u0275\u0275text(4, "Company");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "th", 49);
    \u0275\u0275text(6, "Coordinate");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(7, "th", 49);
    \u0275\u0275text(8, "Algorithm");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(9, "th", 49);
    \u0275\u0275text(10, "Status");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(11, "tbody");
    \u0275\u0275repeaterCreate(12, DashboardComponent_Conditional_80_For_13_Template, 11, 6, "tr", null, _forTrack2);
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    const ctx_r1 = \u0275\u0275nextContext();
    \u0275\u0275advance(12);
    \u0275\u0275repeater(ctx_r1.latestResults());
  }
}
function DashboardComponent_Conditional_81_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 53)(1, "span", 6);
    \u0275\u0275text(2, "my_location");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(3, "p");
    \u0275\u0275text(4, "No barycenter results yet.");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "a", 54);
    \u0275\u0275text(6, " Run Calculation ");
    \u0275\u0275elementEnd()();
  }
}
function DashboardComponent_Conditional_82_For_23_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "tr")(1, "td", 50);
    \u0275\u0275text(2);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(3, "td");
    \u0275\u0275text(4);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "td");
    \u0275\u0275text(6);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(7, "td");
    \u0275\u0275text(8);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(9, "td")(10, "span", 57);
    \u0275\u0275text(11);
    \u0275\u0275elementEnd()()();
  }
  if (rf & 2) {
    const s_r5 = ctx.$implicit;
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(s_r5.shipmentId);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(s_r5.companyName);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(s_r5.origin);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(s_r5.destination);
    \u0275\u0275advance(3);
    \u0275\u0275textInterpolate(s_r5.daysOverdue);
  }
}
function DashboardComponent_Conditional_82_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 31)(1, "div", 15)(2, "div")(3, "h2", 16);
    \u0275\u0275text(4, "Overdue Shipments");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "p", 17);
    \u0275\u0275text(6, "Require immediate attention");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(7, "div", 55)(8, "table", 56)(9, "thead")(10, "tr")(11, "th", 49);
    \u0275\u0275text(12, "Shipment ID");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(13, "th", 49);
    \u0275\u0275text(14, "Company");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(15, "th", 49);
    \u0275\u0275text(16, "Origin");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(17, "th", 49);
    \u0275\u0275text(18, "Destination");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(19, "th", 49);
    \u0275\u0275text(20, "Overdue");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(21, "tbody");
    \u0275\u0275repeaterCreate(22, DashboardComponent_Conditional_82_For_23_Template, 12, 5, "tr", null, _forTrack3);
    \u0275\u0275elementEnd()()()();
  }
  if (rf & 2) {
    const ctx_r1 = \u0275\u0275nextContext();
    \u0275\u0275advance(22);
    \u0275\u0275repeater(ctx_r1.summary().overdueShipments);
  }
}
var DashboardComponent = class _DashboardComponent {
  constructor() {
    this.dataService = inject(DataService);
    this.summary = this.dataService.dashboardSummary;
    this.today = (/* @__PURE__ */ new Date()).toLocaleDateString("en-US", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric"
    });
    this.companiesWithSites = computed(() => this.dataService.companies().filter((c) => c.consumptionSiteCount > 0));
    this.maxTons = computed(() => Math.max(...this.companiesWithSites().map((c) => c.totalTrafficTons), 1));
    this.latestResults = computed(() => this.dataService.results().slice(0, 5));
    this.totalTonsFormatted = computed(() => {
      const t = this.summary().totalTrafficTons;
      return t >= 1e3 ? `${(t / 1e3).toFixed(1)}k t` : `${t.toFixed(0)} t`;
    });
  }
  getCompanyName(id) {
    return this.dataService.getCompanyById(id)?.name ?? id;
  }
  formatTons(t) {
    return t >= 1e3 ? `${(t / 1e3).toFixed(1)}k t` : `${t.toFixed(0)} t`;
  }
  statusBadgeClass(status) {
    const map = {
      CANDIDATE: "badge-info",
      APPROVED: "badge-success",
      CONFIRMED: "badge-success",
      REJECTED: "badge-danger"
    };
    return `badge ${map[status] ?? "badge-neutral"}`;
  }
  static {
    this.\u0275fac = function DashboardComponent_Factory(t) {
      return new (t || _DashboardComponent)();
    };
  }
  static {
    this.\u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({ type: _DashboardComponent, selectors: [["app-dashboard"]], standalone: true, features: [\u0275\u0275StandaloneFeature], decls: 83, vars: 19, consts: [[1, "page-content", "page-enter"], [1, "page-header"], [1, "page-title"], [1, "page-subtitle"], [1, "page-actions"], ["routerLink", "/barycenter", 1, "btn", "btn-primary"], [1, "material-icons"], ["role", "list", "aria-label", "Key performance indicators", 1, "grid-4"], ["role", "listitem"], ["label", "Total Companies", "icon", "domain", "iconBg", "var(--color-primary-050)", "iconColor", "var(--color-primary-700)", 3, "value", "trend", "trendPositive"], ["label", "Active Shipments", "icon", "local_shipping", "iconBg", "var(--color-info-100)", "iconColor", "var(--color-info-600)", 3, "value", "trend", "trendPositive"], ["label", "Consumption Sites", "icon", "location_on", "iconBg", "var(--color-warning-100)", "iconColor", "var(--color-warning-600)", 3, "value", "trend", "trendPositive"], ["label", "On-Time Rate", "icon", "verified", "iconBg", "var(--color-success-100)", "iconColor", "var(--color-success-600)", 3, "value", "trend", "trendPositive"], [1, "dashboard-grid"], [1, "card", "barycenter-summary"], [1, "card-header"], [1, "card-title"], [1, "card-subtitle"], ["routerLink", "/barycenter", 1, "btn", "btn-secondary", "btn-sm"], [1, "card-body"], [1, "bary-stats"], [1, "bary-stat"], [1, "stat-value"], [1, "stat-label"], ["aria-hidden", "true", 1, "bary-stat-divider"], [1, "algorithm-info", 2, "margin-top", "var(--space-5)"], [1, "company-site-list", 2, "margin-top", "var(--space-5)"], [1, "section-title", 2, "margin-bottom", "var(--space-3)"], [1, "company-site-row"], [1, "empty-hint"], [1, "dashboard-right"], [1, "card"], ["role", "feed", "aria-label", "Recent activity", 1, "card-body", "activity-feed"], ["role", "article", 1, "activity-item"], ["routerLink", "/barycenter", 1, "btn", "btn-ghost", "btn-sm"], [1, "card-body", 2, "padding", "0"], ["aria-label", "Latest barycenter results", 1, "data-table"], [1, "company-site-info"], [1, "company-site-name"], [1, "badge", "badge-primary"], [1, "weight-bar-container", 2, "flex", "1", "max-width", "200px"], [1, "weight-bar-track"], [1, "weight-bar-fill"], [1, "weight-value", "mono"], ["routerLink", "/sites"], ["aria-hidden", "true", 1, "activity-dot"], [1, "activity-content"], [1, "activity-message"], [1, "activity-time"], ["scope", "col"], [1, "mono"], [1, "badge", "badge-neutral", 2, "font-size", "10px"], [1, "badge"], [1, "table-empty-state"], ["routerLink", "/barycenter", 1, "btn", "btn-primary", "btn-sm", 2, "margin-top", "var(--space-3)"], [2, "overflow-x", "auto"], ["aria-label", "Overdue shipments", 1, "data-table"], [1, "badge", "badge-danger"]], template: function DashboardComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275elementStart(0, "div", 0)(1, "div", 1)(2, "div")(3, "h1", 2);
        \u0275\u0275text(4, "Dashboard");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(5, "p", 3);
        \u0275\u0275text(6);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(7, "div", 4)(8, "a", 5)(9, "span", 6);
        \u0275\u0275text(10, "my_location");
        \u0275\u0275elementEnd();
        \u0275\u0275text(11, " Calculate Barycenter ");
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(12, "div", 7)(13, "div", 8);
        \u0275\u0275element(14, "app-kpi-card", 9);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(15, "div", 8);
        \u0275\u0275element(16, "app-kpi-card", 10);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(17, "div", 8);
        \u0275\u0275element(18, "app-kpi-card", 11);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(19, "div", 8);
        \u0275\u0275element(20, "app-kpi-card", 12);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(21, "div", 13)(22, "div", 14)(23, "div", 15)(24, "div")(25, "h2", 16);
        \u0275\u0275text(26, "Barycenter Analysis");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(27, "p", 17);
        \u0275\u0275text(28, "Weighted logistics center optimization");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(29, "a", 18)(30, "span", 6);
        \u0275\u0275text(31, "open_in_new");
        \u0275\u0275elementEnd();
        \u0275\u0275text(32, " Open ");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(33, "div", 19)(34, "div", 20)(35, "div", 21)(36, "span", 22);
        \u0275\u0275text(37);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(38, "span", 23);
        \u0275\u0275text(39, "Input Sites");
        \u0275\u0275elementEnd()();
        \u0275\u0275element(40, "div", 24);
        \u0275\u0275elementStart(41, "div", 21)(42, "span", 22);
        \u0275\u0275text(43);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(44, "span", 23);
        \u0275\u0275text(45, "Total Traffic");
        \u0275\u0275elementEnd()();
        \u0275\u0275element(46, "div", 24);
        \u0275\u0275elementStart(47, "div", 21)(48, "span", 22);
        \u0275\u0275text(49);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(50, "span", 23);
        \u0275\u0275text(51, "Candidates");
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(52, "div", 25)(53, "strong");
        \u0275\u0275text(54, "Weiszfeld Algorithm:");
        \u0275\u0275elementEnd();
        \u0275\u0275text(55, " minimises the weighted sum of geodesic (Haversine) distances to all consumption sites \u2014 the geometric median, optimal for logistics cost. ");
        \u0275\u0275elementStart(56, "strong");
        \u0275\u0275text(57, "Simple Barycenter:");
        \u0275\u0275elementEnd();
        \u0275\u0275text(58, " closed-form weighted centroid, instant convergence. ");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(59, "div", 26)(60, "p", 27);
        \u0275\u0275text(61, "Companies with Sites");
        \u0275\u0275elementEnd();
        \u0275\u0275repeaterCreate(62, DashboardComponent_For_63_Template, 11, 6, "div", 28, _forTrack0);
        \u0275\u0275template(64, DashboardComponent_Conditional_64_Template, 5, 0, "p", 29);
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(65, "div", 30)(66, "div", 31)(67, "div", 15)(68, "h2", 16);
        \u0275\u0275text(69, "Recent Activity");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(70, "div", 32);
        \u0275\u0275repeaterCreate(71, DashboardComponent_For_72_Template, 7, 2, "div", 33, _forTrack1);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(73, "div", 31)(74, "div", 15)(75, "h2", 16);
        \u0275\u0275text(76, "Latest Results");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(77, "a", 34);
        \u0275\u0275text(78, "View all");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(79, "div", 35);
        \u0275\u0275template(80, DashboardComponent_Conditional_80_Template, 14, 0, "table", 36)(81, DashboardComponent_Conditional_81_Template, 7, 0);
        \u0275\u0275elementEnd()()()();
        \u0275\u0275template(82, DashboardComponent_Conditional_82_Template, 24, 0, "div", 31);
        \u0275\u0275elementEnd();
      }
      if (rf & 2) {
        \u0275\u0275advance(6);
        \u0275\u0275textInterpolate1("Logistics barycenter overview \u2014 ", ctx.today, "");
        \u0275\u0275advance(8);
        \u0275\u0275property("value", ctx.summary().totalCompanies)("trend", ctx.summary().companiesTrend)("trendPositive", true);
        \u0275\u0275advance(2);
        \u0275\u0275property("value", ctx.summary().activeShipments)("trend", ctx.summary().shipmentsTrend)("trendPositive", true);
        \u0275\u0275advance(2);
        \u0275\u0275property("value", ctx.summary().totalConsumptionSites)("trend", ctx.summary().locationsTrend)("trendPositive", true);
        \u0275\u0275advance(2);
        \u0275\u0275property("value", ctx.summary().onTimeRatePercent + "%")("trend", ctx.summary().onTimeTrend)("trendPositive", true);
        \u0275\u0275advance(17);
        \u0275\u0275textInterpolate(ctx.summary().totalConsumptionSites);
        \u0275\u0275advance(6);
        \u0275\u0275textInterpolate(ctx.totalTonsFormatted());
        \u0275\u0275advance(6);
        \u0275\u0275textInterpolate(ctx.summary().logisticsCenterCandidates);
        \u0275\u0275advance(13);
        \u0275\u0275repeater(ctx.companiesWithSites());
        \u0275\u0275advance(2);
        \u0275\u0275conditional(64, ctx.companiesWithSites().length === 0 ? 64 : -1);
        \u0275\u0275advance(7);
        \u0275\u0275repeater(ctx.summary().recentActivity);
        \u0275\u0275advance(9);
        \u0275\u0275conditional(80, ctx.latestResults().length > 0 ? 80 : 81);
        \u0275\u0275advance(2);
        \u0275\u0275conditional(82, ctx.summary().overdueShipments.length > 0 ? 82 : -1);
      }
    }, dependencies: [CommonModule, RouterLink, KpiCardComponent], styles: ["\n\n.dashboard-grid[_ngcontent-%COMP%] {\n  display: grid;\n  grid-template-columns: 1fr 380px;\n  gap: var(--space-6);\n  align-items: start;\n}\n@media (max-width: 1279px) {\n  .dashboard-grid[_ngcontent-%COMP%] {\n    grid-template-columns: 1fr;\n  }\n}\n.dashboard-right[_ngcontent-%COMP%] {\n  display: flex;\n  flex-direction: column;\n  gap: var(--space-5);\n}\n.bary-stats[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-6);\n  padding: var(--space-4) 0;\n}\n.bary-stat[_ngcontent-%COMP%] {\n  display: flex;\n  flex-direction: column;\n  gap: 4px;\n}\n.bary-stat-divider[_ngcontent-%COMP%] {\n  width: 1px;\n  height: 40px;\n  background-color: var(--color-neutral-300);\n}\n.company-site-row[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  justify-content: space-between;\n  gap: var(--space-4);\n  padding: var(--space-2) 0;\n  border-bottom: 1px solid var(--color-neutral-300);\n  &:last-child {\n    border-bottom: none;\n  }\n}\n.company-site-info[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-3);\n  min-width: 160px;\n}\n.company-site-name[_ngcontent-%COMP%] {\n  font-size: var(--font-size-body);\n  font-weight: 500;\n  color: var(--color-neutral-900);\n}\n.weight-value[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-700);\n  min-width: 54px;\n  text-align: right;\n}\n.empty-hint[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-500);\n  a {\n    color: var(--color-primary-600);\n  }\n}\n.activity-feed[_ngcontent-%COMP%] {\n  padding: var(--space-2) var(--space-6);\n}\n.activity-item[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: flex-start;\n  gap: var(--space-3);\n  padding: var(--space-3) 0;\n  border-bottom: 1px solid var(--color-neutral-300);\n  &:last-child {\n    border-bottom: none;\n  }\n}\n.activity-dot[_ngcontent-%COMP%] {\n  width: 8px;\n  height: 8px;\n  border-radius: 50%;\n  background-color: var(--color-primary-400);\n  flex-shrink: 0;\n  margin-top: 5px;\n}\n.activity-message[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-900);\n  line-height: 1.4;\n}\n.activity-time[_ngcontent-%COMP%] {\n  font-size: 11px;\n  color: var(--color-neutral-500);\n  display: block;\n  margin-top: 2px;\n}\n/*# sourceMappingURL=dashboard.component.css.map */"] });
  }
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && \u0275setClassDebugInfo(DashboardComponent, { className: "DashboardComponent", filePath: "src\\app\\features\\dashboard\\dashboard.component.ts", lineNumber: 361 });
})();
export {
  DashboardComponent
};
//# sourceMappingURL=chunk-OEXFYITO.js.map
