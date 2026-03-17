import {
  require_leaflet_src
} from "./chunk-RYEWYC7T.js";
import {
  CheckboxControlValueAccessor,
  FormsModule,
  NgControlStatus,
  NgModel,
  NgSelectOption,
  SelectControlValueAccessor,
  ɵNgSelectMultipleOption
} from "./chunk-NJA75FPG.js";
import {
  DataService
} from "./chunk-E6436NOF.js";
import {
  CommonModule,
  RouterLink,
  __toESM,
  computed,
  inject,
  ɵsetClassDebugInfo,
  ɵɵStandaloneFeature,
  ɵɵadvance,
  ɵɵdefineComponent,
  ɵɵelement,
  ɵɵelementEnd,
  ɵɵelementStart,
  ɵɵgetCurrentView,
  ɵɵlistener,
  ɵɵloadQuery,
  ɵɵproperty,
  ɵɵqueryRefresh,
  ɵɵrepeater,
  ɵɵrepeaterCreate,
  ɵɵresetView,
  ɵɵrestoreView,
  ɵɵtext,
  ɵɵtextInterpolate,
  ɵɵtwoWayBindingSet,
  ɵɵtwoWayListener,
  ɵɵtwoWayProperty,
  ɵɵviewQuery
} from "./chunk-WAAQAFSM.js";

// src/app/features/barycenter/map-view.component.ts
var L = __toESM(require_leaflet_src());
var _c0 = ["mapEl"];
var _forTrack0 = ($index, $item) => $item.id;
function MapViewComponent_For_14_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "option", 10);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const c_r2 = ctx.$implicit;
    \u0275\u0275property("value", c_r2.id);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(c_r2.name);
  }
}
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png"
});
var MapViewComponent = class _MapViewComponent {
  constructor() {
    this.dataService = inject(DataService);
    this.selectedCompanyId = "ALL";
    this.showSites = true;
    this.showResults = true;
    this.showConnections = true;
    this.layers = [];
    this.visibleSiteCount = computed(() => {
      const sites = this.dataService.activeSites();
      return this.selectedCompanyId === "ALL" ? sites.length : sites.filter((s) => s.companyId === this.selectedCompanyId).length;
    });
    this.visibleResultCount = computed(() => {
      const results = this.dataService.results();
      return this.selectedCompanyId === "ALL" ? results.length : results.filter((r) => r.companyId === this.selectedCompanyId).length;
    });
  }
  ngAfterViewInit() {
    setTimeout(() => this.initMap(), 100);
  }
  ngOnDestroy() {
    this.map?.remove();
  }
  initMap() {
    if (!this.mapEl?.nativeElement || this.map)
      return;
    this.map = L.map(this.mapEl.nativeElement, {
      center: [39.5, -98.35],
      zoom: 4
    });
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "\xA9 OpenStreetMap contributors",
      maxZoom: 18
    }).addTo(this.map);
    this.refreshMap();
  }
  refreshMap() {
    if (!this.map)
      return;
    this.layers.forEach((l) => l.remove());
    this.layers = [];
    const allSites = this.dataService.sites();
    const allResults = this.dataService.results();
    const sites = this.selectedCompanyId === "ALL" ? allSites : allSites.filter((s) => s.companyId === this.selectedCompanyId);
    const results = this.selectedCompanyId === "ALL" ? allResults : allResults.filter((r) => r.companyId === this.selectedCompanyId);
    const COMPANY_COLORS = ["#1F4E79", "#2E6DA4", "#0891B2", "#16A34A", "#D97706", "#7C3AED", "#be185d", "#b45309"];
    const companyIds = [...new Set(allSites.map((s) => s.companyId))];
    const colorMap = {};
    companyIds.forEach((id, i) => {
      colorMap[id] = COMPANY_COLORS[i % COMPANY_COLORS.length];
    });
    if (this.showSites) {
      for (const site of sites) {
        const color = colorMap[site.companyId] ?? "#D97706";
        const m = L.circleMarker([site.latitude, site.longitude], {
          radius: 7 + Math.sqrt(site.weightTons / 200),
          fillColor: site.status === "ACTIVE" ? color : "#D1D5DB",
          color: "#fff",
          weight: 2,
          opacity: 1,
          fillOpacity: site.status === "ACTIVE" ? 0.85 : 0.4
        }).bindPopup(`
          <strong>${site.name}</strong><br/>
          <small style="color:#6B7280;">${this.dataService.getCompanyById(site.companyId)?.name ?? ""}</small><br/>
          <code style="font-size:11px;">${site.formattedCoordinate}</code><br/>
          <b>${site.weightFormatted}</b>
        `).addTo(this.map);
        this.layers.push(m);
      }
    }
    if (this.showResults) {
      for (const r of results) {
        const icon = L.divIcon({
          html: `<div style="
            width:18px; height:18px;
            background:#1F4E79;
            border:3px solid white;
            border-radius:50%;
            box-shadow: 0 2px 6px rgba(0,0,0,0.35);
          "></div>`,
          className: "",
          iconSize: [18, 18],
          iconAnchor: [9, 9]
        });
        const m = L.marker([r.optimalLatitude, r.optimalLongitude], { icon }).bindPopup(`
            <strong style="color:#1F4E79;">Optimal Center</strong><br/>
            <small>${this.dataService.getCompanyById(r.companyId)?.name ?? ""}</small><br/>
            <code style="font-size:11px;">${r.formattedCoordinate}</code><br/>
            Algorithm: ${r.algorithmDescription === "weiszfeld-iterative" ? "Weiszfeld" : "Simple"}<br/>
            Status: <b>${r.status}</b>
          `).addTo(this.map);
        this.layers.push(m);
        if (this.showConnections) {
          for (const s of r.inputSites) {
            const line = L.polyline([
              [s.latitude, s.longitude],
              [r.optimalLatitude, r.optimalLongitude]
            ], {
              color: "#5B9BD5",
              weight: 1,
              opacity: 0.25,
              dashArray: "4 6"
            }).addTo(this.map);
            this.layers.push(line);
          }
        }
      }
    }
  }
  static {
    this.\u0275fac = function MapViewComponent_Factory(t) {
      return new (t || _MapViewComponent)();
    };
  }
  static {
    this.\u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({ type: _MapViewComponent, selectors: [["app-map-view"]], viewQuery: function MapViewComponent_Query(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275viewQuery(_c0, 5);
      }
      if (rf & 2) {
        let _t;
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.mapEl = _t.first);
      }
    }, standalone: true, features: [\u0275\u0275StandaloneFeature], decls: 52, vars: 6, consts: [["mapEl", ""], [1, "map-view-layout", "page-enter"], [1, "map-overlay-panel", "card"], [1, "overlay-header"], [1, "material-icons", 2, "color", "var(--color-primary-700)"], [2, "font-size", "var(--font-size-h3)", "font-weight", "600"], [1, "overlay-section"], ["for", "mvCompany", 1, "form-label"], ["id", "mvCompany", 1, "form-control", 3, "ngModelChange", "ngModel"], ["value", "ALL"], [3, "value"], [1, "form-label"], [1, "toggle-row"], ["type", "checkbox", 3, "ngModelChange", "ngModel"], [1, "toggle-label"], [1, "legend-dot", 2, "background", "var(--color-warning-600)"], [1, "legend-dot", 2, "background", "var(--color-primary-700)", "border", "2px solid white", "box-shadow", "0 1px 4px rgba(0,0,0,0.3)"], [2, "display", "inline-block", "width", "20px", "height", "2px", "background", "var(--color-primary-400)", "border-radius", "1px", "vertical-align", "middle"], [1, "overlay-stat"], ["routerLink", "/barycenter", 1, "btn", "btn-primary", "btn-sm", 2, "margin-top", "var(--space-2)"], [1, "material-icons"], ["aria-label", "Full map view of all consumption sites and barycenter results", "role", "img", 1, "full-map"]], template: function MapViewComponent_Template(rf, ctx) {
      if (rf & 1) {
        const _r1 = \u0275\u0275getCurrentView();
        \u0275\u0275elementStart(0, "div", 1)(1, "div", 2)(2, "div", 3)(3, "span", 4);
        \u0275\u0275text(4, "map");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(5, "h2", 5);
        \u0275\u0275text(6, "Map View");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(7, "div", 6)(8, "label", 7);
        \u0275\u0275text(9, "Company Filter");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(10, "select", 8);
        \u0275\u0275twoWayListener("ngModelChange", function MapViewComponent_Template_select_ngModelChange_10_listener($event) {
          \u0275\u0275restoreView(_r1);
          \u0275\u0275twoWayBindingSet(ctx.selectedCompanyId, $event) || (ctx.selectedCompanyId = $event);
          return \u0275\u0275resetView($event);
        });
        \u0275\u0275listener("ngModelChange", function MapViewComponent_Template_select_ngModelChange_10_listener() {
          \u0275\u0275restoreView(_r1);
          return \u0275\u0275resetView(ctx.refreshMap());
        });
        \u0275\u0275elementStart(11, "option", 9);
        \u0275\u0275text(12, "All Companies");
        \u0275\u0275elementEnd();
        \u0275\u0275repeaterCreate(13, MapViewComponent_For_14_Template, 2, 2, "option", 10, _forTrack0);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(15, "div", 6)(16, "label", 11);
        \u0275\u0275text(17, "Layers");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(18, "label", 12)(19, "input", 13);
        \u0275\u0275twoWayListener("ngModelChange", function MapViewComponent_Template_input_ngModelChange_19_listener($event) {
          \u0275\u0275restoreView(_r1);
          \u0275\u0275twoWayBindingSet(ctx.showSites, $event) || (ctx.showSites = $event);
          return \u0275\u0275resetView($event);
        });
        \u0275\u0275listener("ngModelChange", function MapViewComponent_Template_input_ngModelChange_19_listener() {
          \u0275\u0275restoreView(_r1);
          return \u0275\u0275resetView(ctx.refreshMap());
        });
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(20, "span", 14);
        \u0275\u0275element(21, "span", 15);
        \u0275\u0275text(22, " Consumption Sites ");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(23, "label", 12)(24, "input", 13);
        \u0275\u0275twoWayListener("ngModelChange", function MapViewComponent_Template_input_ngModelChange_24_listener($event) {
          \u0275\u0275restoreView(_r1);
          \u0275\u0275twoWayBindingSet(ctx.showResults, $event) || (ctx.showResults = $event);
          return \u0275\u0275resetView($event);
        });
        \u0275\u0275listener("ngModelChange", function MapViewComponent_Template_input_ngModelChange_24_listener() {
          \u0275\u0275restoreView(_r1);
          return \u0275\u0275resetView(ctx.refreshMap());
        });
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(25, "span", 14);
        \u0275\u0275element(26, "span", 16);
        \u0275\u0275text(27, " Barycenter Results ");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(28, "label", 12)(29, "input", 13);
        \u0275\u0275twoWayListener("ngModelChange", function MapViewComponent_Template_input_ngModelChange_29_listener($event) {
          \u0275\u0275restoreView(_r1);
          \u0275\u0275twoWayBindingSet(ctx.showConnections, $event) || (ctx.showConnections = $event);
          return \u0275\u0275resetView($event);
        });
        \u0275\u0275listener("ngModelChange", function MapViewComponent_Template_input_ngModelChange_29_listener() {
          \u0275\u0275restoreView(_r1);
          return \u0275\u0275resetView(ctx.refreshMap());
        });
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(30, "span", 14);
        \u0275\u0275element(31, "span", 17);
        \u0275\u0275text(32, " Connection Lines ");
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(33, "div", 6)(34, "label", 11);
        \u0275\u0275text(35, "Stats");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(36, "div", 18)(37, "span");
        \u0275\u0275text(38, "Sites visible");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(39, "strong");
        \u0275\u0275text(40);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(41, "div", 18)(42, "span");
        \u0275\u0275text(43, "Results shown");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(44, "strong");
        \u0275\u0275text(45);
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(46, "a", 19)(47, "span", 20);
        \u0275\u0275text(48, "my_location");
        \u0275\u0275elementEnd();
        \u0275\u0275text(49, " Go to Calculator ");
        \u0275\u0275elementEnd()();
        \u0275\u0275element(50, "div", 21, 0);
        \u0275\u0275elementEnd();
      }
      if (rf & 2) {
        \u0275\u0275advance(10);
        \u0275\u0275twoWayProperty("ngModel", ctx.selectedCompanyId);
        \u0275\u0275advance(3);
        \u0275\u0275repeater(ctx.dataService.companies());
        \u0275\u0275advance(6);
        \u0275\u0275twoWayProperty("ngModel", ctx.showSites);
        \u0275\u0275advance(5);
        \u0275\u0275twoWayProperty("ngModel", ctx.showResults);
        \u0275\u0275advance(5);
        \u0275\u0275twoWayProperty("ngModel", ctx.showConnections);
        \u0275\u0275advance(11);
        \u0275\u0275textInterpolate(ctx.visibleSiteCount());
        \u0275\u0275advance(5);
        \u0275\u0275textInterpolate(ctx.visibleResultCount());
      }
    }, dependencies: [CommonModule, RouterLink, FormsModule, NgSelectOption, \u0275NgSelectMultipleOption, CheckboxControlValueAccessor, SelectControlValueAccessor, NgControlStatus, NgModel], styles: ["\n\n[_nghost-%COMP%] {\n  display: block;\n  height: 100%;\n}\n.map-view-layout[_ngcontent-%COMP%] {\n  position: relative;\n  height: calc(100vh - var(--topbar-height) - 2px);\n  overflow: hidden;\n}\n.full-map[_ngcontent-%COMP%] {\n  width: 100%;\n  height: 100%;\n  z-index: 0;\n}\n.map-overlay-panel[_ngcontent-%COMP%] {\n  position: absolute;\n  top: var(--space-4);\n  left: var(--space-4);\n  z-index: 500;\n  width: 240px;\n  padding: var(--space-4);\n  display: flex;\n  flex-direction: column;\n  gap: var(--space-4);\n  max-height: calc(100% - var(--space-8));\n  overflow-y: auto;\n}\n.overlay-header[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-2);\n}\n.overlay-section[_ngcontent-%COMP%] {\n  display: flex;\n  flex-direction: column;\n  gap: var(--space-2);\n}\n.toggle-row[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-2);\n  cursor: pointer;\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-700);\n  padding: var(--space-1) 0;\n  input[type=checkbox] {\n    width: 16px;\n    height: 16px;\n    accent-color: var(--color-primary-600);\n    flex-shrink: 0;\n  }\n}\n.toggle-label[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-2);\n}\n.legend-dot[_ngcontent-%COMP%] {\n  width: 12px;\n  height: 12px;\n  border-radius: 50%;\n  display: inline-block;\n  flex-shrink: 0;\n}\n.overlay-stat[_ngcontent-%COMP%] {\n  display: flex;\n  justify-content: space-between;\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-700);\n  padding: 2px 0;\n  strong {\n    color: var(--color-neutral-900);\n  }\n}\n/*# sourceMappingURL=map-view.component.css.map */"] });
  }
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && \u0275setClassDebugInfo(MapViewComponent, { className: "MapViewComponent", filePath: "src\\app\\features\\barycenter\\map-view.component.ts", lineNumber: 174 });
})();
export {
  MapViewComponent
};
//# sourceMappingURL=chunk-DXWXHKLT.js.map
