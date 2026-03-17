import {
  ToastService
} from "./chunk-7XCFZB4I.js";
import {
  DefaultValueAccessor,
  FormBuilder,
  FormControlName,
  FormGroupDirective,
  FormsModule,
  MinValidator,
  NgControlStatus,
  NgControlStatusGroup,
  NgModel,
  NgSelectOption,
  NumberValueAccessor,
  ReactiveFormsModule,
  SelectControlValueAccessor,
  Validators,
  ɵNgNoValidate,
  ɵNgSelectMultipleOption
} from "./chunk-NJA75FPG.js";
import {
  DataService
} from "./chunk-E6436NOF.js";
import {
  ActivatedRoute,
  CommonModule,
  RouterLink,
  computed,
  inject,
  signal,
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
  ɵɵgetCurrentView,
  ɵɵlistener,
  ɵɵnextContext,
  ɵɵproperty,
  ɵɵpropertyInterpolate2,
  ɵɵpureFunction0,
  ɵɵrepeater,
  ɵɵrepeaterCreate,
  ɵɵrepeaterTrackByIdentity,
  ɵɵresetView,
  ɵɵrestoreView,
  ɵɵstyleProp,
  ɵɵtemplate,
  ɵɵtext,
  ɵɵtextInterpolate,
  ɵɵtextInterpolate1,
  ɵɵtextInterpolate2,
  ɵɵtextInterpolate3,
  ɵɵtwoWayBindingSet,
  ɵɵtwoWayListener,
  ɵɵtwoWayProperty
} from "./chunk-WAAQAFSM.js";

// src/app/features/sites/sites.component.ts
var _forTrack0 = ($index, $item) => $item.id;
var _c0 = () => ["/companies"];
function SitesComponent_For_23_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "option", 15);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const c_r1 = ctx.$implicit;
    \u0275\u0275property("value", c_r1.id);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(c_r1.name);
  }
}
function SitesComponent_Conditional_31_Template(rf, ctx) {
  if (rf & 1) {
    const _r2 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "button", 27);
    \u0275\u0275listener("click", function SitesComponent_Conditional_31_Template_button_click_0_listener() {
      \u0275\u0275restoreView(_r2);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.clearFilters());
    });
    \u0275\u0275elementStart(1, "span", 6);
    \u0275\u0275text(2, "filter_list_off");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Clear ");
    \u0275\u0275elementEnd();
  }
}
function SitesComponent_Conditional_39_Conditional_25_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "tr")(1, "td", 39)(2, "div", 40)(3, "span", 6);
    \u0275\u0275text(4, "location_off");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "p");
    \u0275\u0275text(6, "No sites found.");
    \u0275\u0275elementEnd()()()();
  }
}
function SitesComponent_Conditional_39_For_27_Conditional_9_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 44);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const site_r6 = \u0275\u0275nextContext().$implicit;
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(site_r6.description);
  }
}
function SitesComponent_Conditional_39_For_27_Conditional_14_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "span");
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const site_r6 = \u0275\u0275nextContext().$implicit;
    \u0275\u0275advance();
    \u0275\u0275textInterpolate2("", site_r6.city, "", site_r6.country ? ", " + site_r6.country : "", "");
  }
}
function SitesComponent_Conditional_39_For_27_Conditional_15_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "span", 57);
    \u0275\u0275text(1, "\u2014");
    \u0275\u0275elementEnd();
  }
}
function SitesComponent_Conditional_39_For_27_Template(rf, ctx) {
  if (rf & 1) {
    const _r5 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "tr")(1, "td")(2, "div", 41)(3, "span", 42)(4, "span", 6);
    \u0275\u0275text(5, "location_on");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(6, "div")(7, "p", 43);
    \u0275\u0275text(8);
    \u0275\u0275elementEnd();
    \u0275\u0275template(9, SitesComponent_Conditional_39_For_27_Conditional_9_Template, 2, 1, "p", 44);
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(10, "td")(11, "a", 45);
    \u0275\u0275text(12);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(13, "td");
    \u0275\u0275template(14, SitesComponent_Conditional_39_For_27_Conditional_14_Template, 2, 2, "span")(15, SitesComponent_Conditional_39_For_27_Conditional_15_Template, 2, 0);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(16, "td")(17, "span", 46);
    \u0275\u0275text(18);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(19, "td", 47);
    \u0275\u0275text(20);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(21, "td")(22, "div", 48)(23, "div", 49);
    \u0275\u0275element(24, "div", 50);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(25, "span", 51);
    \u0275\u0275text(26);
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(27, "td")(28, "span", 52);
    \u0275\u0275text(29);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(30, "td", 53)(31, "button", 54);
    \u0275\u0275listener("click", function SitesComponent_Conditional_39_For_27_Template_button_click_31_listener() {
      const site_r6 = \u0275\u0275restoreView(_r5).$implicit;
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.openEditModal(site_r6));
    });
    \u0275\u0275elementStart(32, "span", 6);
    \u0275\u0275text(33, "edit");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(34, "button", 55);
    \u0275\u0275listener("click", function SitesComponent_Conditional_39_For_27_Template_button_click_34_listener() {
      const site_r6 = \u0275\u0275restoreView(_r5).$implicit;
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.toggleStatus(site_r6));
    });
    \u0275\u0275elementStart(35, "span", 6);
    \u0275\u0275text(36);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(37, "button", 56);
    \u0275\u0275listener("click", function SitesComponent_Conditional_39_For_27_Template_button_click_37_listener() {
      const site_r6 = \u0275\u0275restoreView(_r5).$implicit;
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.confirmDelete(site_r6));
    });
    \u0275\u0275elementStart(38, "span", 6);
    \u0275\u0275text(39, "delete_outline");
    \u0275\u0275elementEnd()()()();
  }
  if (rf & 2) {
    const site_r6 = ctx.$implicit;
    const ctx_r2 = \u0275\u0275nextContext(2);
    \u0275\u0275advance(4);
    \u0275\u0275styleProp("color", site_r6.status === "ACTIVE" ? "var(--color-warning-600)" : "var(--color-neutral-400)");
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate(site_r6.name);
    \u0275\u0275advance();
    \u0275\u0275conditional(9, site_r6.description ? 9 : -1);
    \u0275\u0275advance(2);
    \u0275\u0275property("routerLink", \u0275\u0275pureFunction0(24, _c0));
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r2.getCompanyName(site_r6.companyId));
    \u0275\u0275advance(2);
    \u0275\u0275conditional(14, site_r6.city ? 14 : 15);
    \u0275\u0275advance(3);
    \u0275\u0275propertyInterpolate2("title", "Latitude: ", site_r6.latitude, ", Longitude: ", site_r6.longitude, "");
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", site_r6.formattedCoordinate, " ");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(site_r6.weightFormatted);
    \u0275\u0275advance(4);
    \u0275\u0275styleProp("width", ctx_r2.weightPercent(site_r6), "%");
    \u0275\u0275attribute("aria-label", ctx_r2.weightPercent(site_r6).toFixed(1) + "%");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate1("", ctx_r2.weightPercent(site_r6).toFixed(0), "%");
    \u0275\u0275advance(2);
    \u0275\u0275classMap(site_r6.status === "ACTIVE" ? "badge-success" : "badge-neutral");
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(site_r6.status);
    \u0275\u0275advance(2);
    \u0275\u0275attribute("aria-label", "Edit " + site_r6.name);
    \u0275\u0275advance(3);
    \u0275\u0275property("title", site_r6.status === "ACTIVE" ? "Deactivate" : "Activate");
    \u0275\u0275attribute("aria-label", (site_r6.status === "ACTIVE" ? "Deactivate" : "Activate") + " " + site_r6.name);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(site_r6.status === "ACTIVE" ? "pause_circle" : "play_circle");
    \u0275\u0275advance();
    \u0275\u0275attribute("aria-label", "Delete " + site_r6.name);
  }
}
function SitesComponent_Conditional_39_For_36_Template(rf, ctx) {
  if (rf & 1) {
    const _r7 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "button", 58);
    \u0275\u0275listener("click", function SitesComponent_Conditional_39_For_36_Template_button_click_0_listener() {
      const p_r8 = \u0275\u0275restoreView(_r7).$implicit;
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.page.set(p_r8));
    });
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const p_r8 = ctx.$implicit;
    const ctx_r2 = \u0275\u0275nextContext(2);
    \u0275\u0275classProp("active", p_r8 === ctx_r2.page());
    \u0275\u0275attribute("aria-label", "Page " + p_r8);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(p_r8);
  }
}
function SitesComponent_Conditional_39_Template(rf, ctx) {
  if (rf & 1) {
    const _r4 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 23)(1, "table", 28)(2, "thead")(3, "tr")(4, "th", 29);
    \u0275\u0275listener("click", function SitesComponent_Conditional_39_Template_th_click_4_listener() {
      \u0275\u0275restoreView(_r4);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.sort("name"));
    });
    \u0275\u0275text(5, " Site ");
    \u0275\u0275elementStart(6, "span", 30);
    \u0275\u0275text(7, "unfold_more");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(8, "th", 31);
    \u0275\u0275text(9, "Company");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(10, "th", 31);
    \u0275\u0275text(11, "Location");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(12, "th", 31);
    \u0275\u0275text(13, "Coordinates");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(14, "th", 29);
    \u0275\u0275listener("click", function SitesComponent_Conditional_39_Template_th_click_14_listener() {
      \u0275\u0275restoreView(_r4);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.sort("weightTons"));
    });
    \u0275\u0275text(15, " Weight ");
    \u0275\u0275elementStart(16, "span", 30);
    \u0275\u0275text(17, "unfold_more");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(18, "th", 31);
    \u0275\u0275text(19, "Weight %");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(20, "th", 31);
    \u0275\u0275text(21, "Status");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(22, "th", 32);
    \u0275\u0275text(23, "Actions");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(24, "tbody");
    \u0275\u0275template(25, SitesComponent_Conditional_39_Conditional_25_Template, 7, 0, "tr");
    \u0275\u0275repeaterCreate(26, SitesComponent_Conditional_39_For_27_Template, 40, 25, "tr", null, _forTrack0);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(28, "div", 33)(29, "span");
    \u0275\u0275text(30);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(31, "div", 34)(32, "button", 35);
    \u0275\u0275listener("click", function SitesComponent_Conditional_39_Template_button_click_32_listener() {
      \u0275\u0275restoreView(_r4);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.prevPage());
    });
    \u0275\u0275elementStart(33, "span", 36);
    \u0275\u0275text(34, "chevron_left");
    \u0275\u0275elementEnd()();
    \u0275\u0275repeaterCreate(35, SitesComponent_Conditional_39_For_36_Template, 2, 4, "button", 37, \u0275\u0275repeaterTrackByIdentity);
    \u0275\u0275elementStart(37, "button", 38);
    \u0275\u0275listener("click", function SitesComponent_Conditional_39_Template_button_click_37_listener() {
      \u0275\u0275restoreView(_r4);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.nextPage());
    });
    \u0275\u0275elementStart(38, "span", 36);
    \u0275\u0275text(39, "chevron_right");
    \u0275\u0275elementEnd()()()()();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275advance(4);
    \u0275\u0275classProp("sort-asc", ctx_r2.sortCol === "name" && ctx_r2.sortDir === "asc")("sort-desc", ctx_r2.sortCol === "name" && ctx_r2.sortDir === "desc");
    \u0275\u0275advance(10);
    \u0275\u0275classProp("sort-asc", ctx_r2.sortCol === "weightTons" && ctx_r2.sortDir === "asc")("sort-desc", ctx_r2.sortCol === "weightTons" && ctx_r2.sortDir === "desc");
    \u0275\u0275advance(11);
    \u0275\u0275conditional(25, ctx_r2.filteredSites().length === 0 ? 25 : -1);
    \u0275\u0275advance();
    \u0275\u0275repeater(ctx_r2.pagedSites());
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate3("Showing ", ctx_r2.pageStart(), "\u2013", ctx_r2.pageEnd(), " of ", ctx_r2.filteredSites().length, " sites");
    \u0275\u0275advance(2);
    \u0275\u0275property("disabled", ctx_r2.page() === 1);
    \u0275\u0275advance(3);
    \u0275\u0275repeater(ctx_r2.pageNumbers());
    \u0275\u0275advance(2);
    \u0275\u0275property("disabled", ctx_r2.page() === ctx_r2.totalPages());
  }
}
function SitesComponent_Conditional_40_Conditional_1_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 59)(1, "span", 61);
    \u0275\u0275text(2, "location_off");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(3, "p", 62);
    \u0275\u0275text(4, "No sites found.");
    \u0275\u0275elementEnd()();
  }
}
function SitesComponent_Conditional_40_For_3_Conditional_12_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 70)(1, "span", 77);
    \u0275\u0275text(2, "place");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const site_r10 = \u0275\u0275nextContext().$implicit;
    \u0275\u0275advance(3);
    \u0275\u0275textInterpolate2(" ", site_r10.city, "", site_r10.country ? ", " + site_r10.country : "", " ");
  }
}
function SitesComponent_Conditional_40_For_3_Template(rf, ctx) {
  if (rf & 1) {
    const _r9 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 60)(1, "div", 63)(2, "span", 64);
    \u0275\u0275text(3, "location_on");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(4, "div", 65)(5, "h3", 66);
    \u0275\u0275text(6);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(7, "p", 67);
    \u0275\u0275text(8);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(9, "span", 68);
    \u0275\u0275text(10);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(11, "div", 69);
    \u0275\u0275template(12, SitesComponent_Conditional_40_For_3_Conditional_12_Template, 4, 2, "p", 70);
    \u0275\u0275elementStart(13, "p", 71);
    \u0275\u0275text(14);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(15, "div", 72)(16, "span", 73);
    \u0275\u0275text(17);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(18, "div", 74)(19, "div", 49);
    \u0275\u0275element(20, "div", 50);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(21, "span", 75);
    \u0275\u0275text(22);
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(23, "div", 76)(24, "button", 27);
    \u0275\u0275listener("click", function SitesComponent_Conditional_40_For_3_Template_button_click_24_listener() {
      const site_r10 = \u0275\u0275restoreView(_r9).$implicit;
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.openEditModal(site_r10));
    });
    \u0275\u0275elementStart(25, "span", 6);
    \u0275\u0275text(26, "edit");
    \u0275\u0275elementEnd();
    \u0275\u0275text(27, " Edit ");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(28, "button", 27);
    \u0275\u0275listener("click", function SitesComponent_Conditional_40_For_3_Template_button_click_28_listener() {
      const site_r10 = \u0275\u0275restoreView(_r9).$implicit;
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.toggleStatus(site_r10));
    });
    \u0275\u0275elementStart(29, "span", 6);
    \u0275\u0275text(30);
    \u0275\u0275elementEnd();
    \u0275\u0275text(31);
    \u0275\u0275elementEnd()()();
  }
  if (rf & 2) {
    const site_r10 = ctx.$implicit;
    const ctx_r2 = \u0275\u0275nextContext(2);
    \u0275\u0275advance(2);
    \u0275\u0275styleProp("color", site_r10.status === "ACTIVE" ? "var(--color-warning-600)" : "var(--color-neutral-300)");
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate(site_r10.name);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(ctx_r2.getCompanyName(site_r10.companyId));
    \u0275\u0275advance();
    \u0275\u0275classMap(site_r10.status === "ACTIVE" ? "badge-success" : "badge-neutral");
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(site_r10.status);
    \u0275\u0275advance(2);
    \u0275\u0275conditional(12, site_r10.city ? 12 : -1);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(site_r10.formattedCoordinate);
    \u0275\u0275advance(3);
    \u0275\u0275textInterpolate(site_r10.weightFormatted);
    \u0275\u0275advance(3);
    \u0275\u0275styleProp("width", ctx_r2.weightPercent(site_r10), "%");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate1("", ctx_r2.weightPercent(site_r10).toFixed(0), "%");
    \u0275\u0275advance(2);
    \u0275\u0275attribute("aria-label", "Edit " + site_r10.name);
    \u0275\u0275advance(4);
    \u0275\u0275attribute("aria-label", (site_r10.status === "ACTIVE" ? "Deactivate" : "Activate") + " " + site_r10.name);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(site_r10.status === "ACTIVE" ? "pause_circle" : "play_circle");
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", site_r10.status === "ACTIVE" ? "Deactivate" : "Activate", " ");
  }
}
function SitesComponent_Conditional_40_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 24);
    \u0275\u0275template(1, SitesComponent_Conditional_40_Conditional_1_Template, 5, 0, "div", 59);
    \u0275\u0275repeaterCreate(2, SitesComponent_Conditional_40_For_3_Template, 32, 17, "div", 60, _forTrack0);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275advance();
    \u0275\u0275conditional(1, ctx_r2.filteredSites().length === 0 ? 1 : -1);
    \u0275\u0275advance();
    \u0275\u0275repeater(ctx_r2.pagedSites());
  }
}
function SitesComponent_Conditional_41_Conditional_15_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 88)(1, "span", 6);
    \u0275\u0275text(2, "error_outline");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Name is required");
    \u0275\u0275elementEnd();
  }
}
function SitesComponent_Conditional_41_For_23_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "option", 15);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const c_r12 = ctx.$implicit;
    \u0275\u0275property("value", c_r12.id);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(c_r12.name);
  }
}
function SitesComponent_Conditional_41_Conditional_24_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 88)(1, "span", 6);
    \u0275\u0275text(2, "error_outline");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Company is required");
    \u0275\u0275elementEnd();
  }
}
function SitesComponent_Conditional_41_Conditional_39_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 88)(1, "span", 6);
    \u0275\u0275text(2, "error_outline");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Valid latitude (\u221290 to 90) required");
    \u0275\u0275elementEnd();
  }
}
function SitesComponent_Conditional_41_Conditional_44_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 88)(1, "span", 6);
    \u0275\u0275text(2, "error_outline");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Valid longitude (\u2212180 to 180) required");
    \u0275\u0275elementEnd();
  }
}
function SitesComponent_Conditional_41_Conditional_52_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 88)(1, "span", 6);
    \u0275\u0275text(2, "error_outline");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Weight must be a positive number");
    \u0275\u0275elementEnd();
  }
}
function SitesComponent_Conditional_41_Template(rf, ctx) {
  if (rf & 1) {
    const _r11 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 78);
    \u0275\u0275listener("click", function SitesComponent_Conditional_41_Template_div_click_0_listener() {
      \u0275\u0275restoreView(_r11);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.closeModal());
    });
    \u0275\u0275elementStart(1, "div", 79);
    \u0275\u0275listener("click", function SitesComponent_Conditional_41_Template_div_click_1_listener($event) {
      \u0275\u0275restoreView(_r11);
      return \u0275\u0275resetView($event.stopPropagation());
    });
    \u0275\u0275elementStart(2, "div", 80)(3, "h2");
    \u0275\u0275text(4);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "button", 81);
    \u0275\u0275listener("click", function SitesComponent_Conditional_41_Template_button_click_5_listener() {
      \u0275\u0275restoreView(_r11);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.closeModal());
    });
    \u0275\u0275elementStart(6, "span", 6);
    \u0275\u0275text(7, "close");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(8, "form", 82);
    \u0275\u0275listener("ngSubmit", function SitesComponent_Conditional_41_Template_form_ngSubmit_8_listener() {
      \u0275\u0275restoreView(_r11);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.submitForm());
    });
    \u0275\u0275elementStart(9, "div", 83)(10, "div", 84)(11, "div", 85)(12, "label", 86);
    \u0275\u0275text(13, "Site Name *");
    \u0275\u0275elementEnd();
    \u0275\u0275element(14, "input", 87);
    \u0275\u0275template(15, SitesComponent_Conditional_41_Conditional_15_Template, 4, 0, "p", 88);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(16, "div", 89)(17, "label", 90);
    \u0275\u0275text(18, "Company *");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(19, "select", 91)(20, "option", 92);
    \u0275\u0275text(21, "Select a company\u2026");
    \u0275\u0275elementEnd();
    \u0275\u0275repeaterCreate(22, SitesComponent_Conditional_41_For_23_Template, 2, 2, "option", 15, _forTrack0);
    \u0275\u0275elementEnd();
    \u0275\u0275template(24, SitesComponent_Conditional_41_Conditional_24_Template, 4, 0, "p", 88);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(25, "div", 89)(26, "label", 93);
    \u0275\u0275text(27, "Description");
    \u0275\u0275elementEnd();
    \u0275\u0275element(28, "input", 94);
    \u0275\u0275elementEnd();
    \u0275\u0275element(29, "hr", 95);
    \u0275\u0275elementStart(30, "p", 96);
    \u0275\u0275text(31, "Geographic Coordinates");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(32, "p", 97);
    \u0275\u0275text(33, " Enter WGS-84 decimal-degree coordinates. Latitude: \u221290 to 90, Longitude: \u2212180 to 180. ");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(34, "div", 84)(35, "div", 98)(36, "label", 99);
    \u0275\u0275text(37, "Latitude *");
    \u0275\u0275elementEnd();
    \u0275\u0275element(38, "input", 100);
    \u0275\u0275template(39, SitesComponent_Conditional_41_Conditional_39_Template, 4, 0, "p", 88);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(40, "div", 98)(41, "label", 101);
    \u0275\u0275text(42, "Longitude *");
    \u0275\u0275elementEnd();
    \u0275\u0275element(43, "input", 102);
    \u0275\u0275template(44, SitesComponent_Conditional_41_Conditional_44_Template, 4, 0, "p", 88);
    \u0275\u0275elementEnd()();
    \u0275\u0275element(45, "hr", 95);
    \u0275\u0275elementStart(46, "p", 96);
    \u0275\u0275text(47, "Traffic Weight");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(48, "div", 98)(49, "label", 103);
    \u0275\u0275text(50, "Annual Traffic Volume (tons) *");
    \u0275\u0275elementEnd();
    \u0275\u0275element(51, "input", 104);
    \u0275\u0275template(52, SitesComponent_Conditional_41_Conditional_52_Template, 4, 0, "p", 88);
    \u0275\u0275elementStart(53, "p", 105);
    \u0275\u0275text(54, "Tonnage used as the weight in barycenter calculation.");
    \u0275\u0275elementEnd()();
    \u0275\u0275element(55, "hr", 95);
    \u0275\u0275elementStart(56, "p", 96);
    \u0275\u0275text(57, "Address (Optional)");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(58, "div", 106)(59, "div", 98)(60, "label", 107);
    \u0275\u0275text(61, "Street Address");
    \u0275\u0275elementEnd();
    \u0275\u0275element(62, "input", 108);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(63, "div", 98)(64, "label", 109);
    \u0275\u0275text(65, "City");
    \u0275\u0275elementEnd();
    \u0275\u0275element(66, "input", 110);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(67, "div", 98)(68, "label", 111);
    \u0275\u0275text(69, "Country");
    \u0275\u0275elementEnd();
    \u0275\u0275element(70, "input", 112);
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(71, "div", 113)(72, "button", 114);
    \u0275\u0275listener("click", function SitesComponent_Conditional_41_Template_button_click_72_listener() {
      \u0275\u0275restoreView(_r11);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.closeModal());
    });
    \u0275\u0275text(73, "Cancel");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(74, "button", 115)(75, "span", 6);
    \u0275\u0275text(76);
    \u0275\u0275elementEnd();
    \u0275\u0275text(77);
    \u0275\u0275elementEnd()()()()();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275attribute("aria-label", ctx_r2.editTarget() ? "Edit site" : "Add site");
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate(ctx_r2.editTarget() ? "Edit Site" : "Add Consumption Site");
    \u0275\u0275advance(4);
    \u0275\u0275property("formGroup", ctx_r2.form);
    \u0275\u0275advance(6);
    \u0275\u0275classProp("error", ctx_r2.fieldError("name"));
    \u0275\u0275advance();
    \u0275\u0275conditional(15, ctx_r2.fieldError("name") ? 15 : -1);
    \u0275\u0275advance(4);
    \u0275\u0275classProp("error", ctx_r2.fieldError("companyId"));
    \u0275\u0275advance(3);
    \u0275\u0275repeater(ctx_r2.dataService.companies());
    \u0275\u0275advance(2);
    \u0275\u0275conditional(24, ctx_r2.fieldError("companyId") ? 24 : -1);
    \u0275\u0275advance(14);
    \u0275\u0275classProp("error", ctx_r2.fieldError("latitude"));
    \u0275\u0275advance();
    \u0275\u0275conditional(39, ctx_r2.fieldError("latitude") ? 39 : -1);
    \u0275\u0275advance(4);
    \u0275\u0275classProp("error", ctx_r2.fieldError("longitude"));
    \u0275\u0275advance();
    \u0275\u0275conditional(44, ctx_r2.fieldError("longitude") ? 44 : -1);
    \u0275\u0275advance(7);
    \u0275\u0275classProp("error", ctx_r2.fieldError("weightTons"));
    \u0275\u0275advance();
    \u0275\u0275conditional(52, ctx_r2.fieldError("weightTons") ? 52 : -1);
    \u0275\u0275advance(22);
    \u0275\u0275property("disabled", ctx_r2.form.invalid);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(ctx_r2.editTarget() ? "save" : "add_location");
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", ctx_r2.editTarget() ? "Save Changes" : "Add Site", " ");
  }
}
function SitesComponent_Conditional_42_Template(rf, ctx) {
  if (rf & 1) {
    const _r13 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 26)(1, "div", 116)(2, "div", 80)(3, "h2");
    \u0275\u0275text(4, "Delete Site");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "button", 117);
    \u0275\u0275listener("click", function SitesComponent_Conditional_42_Template_button_click_5_listener() {
      \u0275\u0275restoreView(_r13);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.deleteTarget.set(null));
    });
    \u0275\u0275elementStart(6, "span", 6);
    \u0275\u0275text(7, "close");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(8, "div", 83)(9, "p");
    \u0275\u0275text(10, "Delete ");
    \u0275\u0275elementStart(11, "strong");
    \u0275\u0275text(12);
    \u0275\u0275elementEnd();
    \u0275\u0275text(13, "?");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(14, "p", 118);
    \u0275\u0275text(15, " This will remove it from all barycenter calculations. ");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(16, "div", 113)(17, "button", 119);
    \u0275\u0275listener("click", function SitesComponent_Conditional_42_Template_button_click_17_listener() {
      \u0275\u0275restoreView(_r13);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.deleteTarget.set(null));
    });
    \u0275\u0275text(18, "Cancel");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(19, "button", 120);
    \u0275\u0275listener("click", function SitesComponent_Conditional_42_Template_button_click_19_listener() {
      \u0275\u0275restoreView(_r13);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.executeDelete());
    });
    \u0275\u0275elementStart(20, "span", 6);
    \u0275\u0275text(21, "delete");
    \u0275\u0275elementEnd();
    \u0275\u0275text(22, " Delete ");
    \u0275\u0275elementEnd()()()();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275advance(12);
    \u0275\u0275textInterpolate(ctx_r2.deleteTarget().name);
  }
}
var SitesComponent = class _SitesComponent {
  constructor() {
    this.dataService = inject(DataService);
    this.toast = inject(ToastService);
    this.fb = inject(FormBuilder);
    this.route = inject(ActivatedRoute);
    this.filter = { search: "", status: "ALL", companyId: "ALL" };
    this.sortCol = "name";
    this.sortDir = "asc";
    this.page = signal(1);
    this.pageSize = 10;
    this.viewMode = signal("table");
    this.showModal = signal(false);
    this.editTarget = signal(null);
    this.deleteTarget = signal(null);
    this.form = this.fb.group({
      companyId: ["", Validators.required],
      name: ["", [Validators.required, Validators.minLength(2)]],
      description: [""],
      latitude: [null, [Validators.required, Validators.min(-90), Validators.max(90)]],
      longitude: [null, [Validators.required, Validators.min(-180), Validators.max(180)]],
      weightTons: [null, [Validators.required, Validators.min(0)]],
      address: [""],
      city: [""],
      country: [""]
    });
    this.filteredSites = computed(() => {
      let list = [...this.dataService.sites()];
      const f = this.filter;
      if (f.search) {
        const q = f.search.toLowerCase();
        list = list.filter((s) => s.name.toLowerCase().includes(q) || (s.city ?? "").toLowerCase().includes(q) || (s.address ?? "").toLowerCase().includes(q));
      }
      if (f.status !== "ALL")
        list = list.filter((s) => s.status === f.status);
      if (f.companyId !== "ALL")
        list = list.filter((s) => s.companyId === f.companyId);
      list.sort((a, b) => {
        const av = a[this.sortCol];
        const bv = b[this.sortCol];
        const cmp = typeof av === "number" ? av - bv : String(av).localeCompare(String(bv));
        return this.sortDir === "asc" ? cmp : -cmp;
      });
      return list;
    });
    this.totalTonsFormatted = computed(() => {
      const t = this.filteredSites().filter((s) => s.status === "ACTIVE").reduce((sum, s) => sum + s.weightTons, 0);
      return t >= 1e3 ? `${(t / 1e3).toFixed(1)}k t` : `${t.toFixed(0)} t`;
    });
    this.maxTons = computed(() => Math.max(...this.dataService.activeSites().map((s) => s.weightTons), 1));
    this.totalPages = computed(() => Math.ceil(this.filteredSites().length / this.pageSize) || 1);
    this.pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i + 1));
    this.pageStart = computed(() => (this.page() - 1) * this.pageSize + 1);
    this.pageEnd = computed(() => Math.min(this.page() * this.pageSize, this.filteredSites().length));
    this.pagedSites = computed(() => {
      const start = (this.page() - 1) * this.pageSize;
      return this.filteredSites().slice(start, start + this.pageSize);
    });
  }
  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      if (params["companyId"])
        this.filter.companyId = params["companyId"];
      if (params["search"])
        this.filter.search = params["search"];
    });
  }
  sort(col) {
    if (this.sortCol === col) {
      this.sortDir = this.sortDir === "asc" ? "desc" : "asc";
    } else {
      this.sortCol = col;
      this.sortDir = "desc";
    }
    this.page.set(1);
  }
  prevPage() {
    if (this.page() > 1)
      this.page.update((p) => p - 1);
  }
  nextPage() {
    if (this.page() < this.totalPages())
      this.page.update((p) => p + 1);
  }
  clearFilters() {
    this.filter = { search: "", status: "ALL", companyId: "ALL" };
    this.page.set(1);
  }
  openCreateModal() {
    this.editTarget.set(null);
    this.form.reset();
    this.showModal.set(true);
  }
  openEditModal(site) {
    this.editTarget.set(site);
    this.form.patchValue({
      companyId: site.companyId,
      name: site.name,
      description: site.description ?? "",
      latitude: site.latitude,
      longitude: site.longitude,
      weightTons: site.weightTons,
      address: site.address ?? "",
      city: site.city ?? "",
      country: site.country ?? ""
    });
    this.showModal.set(true);
  }
  closeModal() {
    this.showModal.set(false);
    this.editTarget.set(null);
  }
  submitForm() {
    if (this.form.invalid)
      return;
    const v = this.form.getRawValue();
    if (this.editTarget()) {
      const payload = {
        name: v.name,
        description: v.description || void 0,
        latitude: v.latitude,
        longitude: v.longitude,
        weightTons: v.weightTons,
        address: v.address || void 0,
        city: v.city || void 0,
        country: v.country || void 0
      };
      this.dataService.updateSite(this.editTarget().id, payload);
      this.toast.success("Site updated", `${v.name} has been saved.`);
    } else {
      const payload = {
        companyId: v.companyId,
        name: v.name,
        description: v.description || void 0,
        latitude: v.latitude,
        longitude: v.longitude,
        weightTons: v.weightTons,
        address: v.address || void 0,
        city: v.city || void 0,
        country: v.country || void 0
      };
      this.dataService.createSite(payload);
      this.toast.success("Site added", `${v.name} has been created.`);
    }
    this.closeModal();
  }
  toggleStatus(site) {
    const newStatus = site.status === "ACTIVE" ? "INACTIVE" : "ACTIVE";
    this.dataService.updateSite(site.id, { status: newStatus });
    this.toast.info(`Site ${newStatus === "ACTIVE" ? "activated" : "deactivated"}`, site.name);
  }
  confirmDelete(site) {
    this.deleteTarget.set(site);
  }
  executeDelete() {
    const s = this.deleteTarget();
    if (!s)
      return;
    this.dataService.deleteSite(s.id);
    this.toast.success("Site deleted", `${s.name} has been removed.`);
    this.deleteTarget.set(null);
  }
  fieldError(field) {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && (ctrl.dirty || ctrl.touched));
  }
  getCompanyName(id) {
    return this.dataService.getCompanyById(id)?.name ?? id;
  }
  weightPercent(site) {
    const max = this.maxTons();
    return max > 0 ? site.weightTons / max * 100 : 0;
  }
  static {
    this.\u0275fac = function SitesComponent_Factory(t) {
      return new (t || _SitesComponent)();
    };
  }
  static {
    this.\u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({ type: _SitesComponent, selectors: [["app-sites"]], standalone: true, features: [\u0275\u0275StandaloneFeature], decls: 43, vars: 14, consts: [[1, "page-content", "page-enter"], [1, "page-header"], [1, "page-title"], [1, "page-subtitle"], [1, "page-actions"], [1, "btn", "btn-primary", 3, "click"], [1, "material-icons"], [1, "card"], [1, "card-body", 2, "padding", "var(--space-3) var(--space-5)"], [1, "filter-row"], [1, "search-field"], ["aria-hidden", "true", 1, "material-icons", "search-icon-sm"], ["type", "search", "placeholder", "Search sites, cities\u2026", "aria-label", "Search sites", 1, "form-control", 3, "ngModelChange", "ngModel"], ["aria-label", "Filter by company", 1, "form-control", "filter-select", 3, "ngModelChange", "ngModel"], ["value", "ALL"], [3, "value"], ["aria-label", "Filter by status", 1, "form-control", "filter-select", 3, "ngModelChange", "ngModel"], ["value", "ACTIVE"], ["value", "INACTIVE"], [1, "btn", "btn-ghost", "btn-sm"], [2, "margin-left", "auto"], ["title", "Table view", "aria-label", "Table view", 1, "btn", "btn-icon", 3, "click"], ["title", "Card view", "aria-label", "Card view", 1, "btn", "btn-icon", 3, "click"], [1, "data-table-wrapper"], [1, "site-cards-grid"], ["role", "dialog", "aria-modal", "true", 1, "modal-overlay"], ["role", "alertdialog", "aria-modal", "true", "aria-label", "Confirm site deletion", 1, "modal-overlay"], [1, "btn", "btn-ghost", "btn-sm", 3, "click"], ["aria-label", "Consumption sites list", 1, "data-table"], ["scope", "col", 1, "sortable", 3, "click"], ["aria-hidden", "true", 1, "sort-indicator", "material-icons"], ["scope", "col"], ["scope", "col", 1, "actions"], [1, "pagination-bar"], [1, "pagination-controls"], ["aria-label", "Previous page", 1, "page-btn", 3, "click", "disabled"], [1, "material-icons", 2, "font-size", "18px"], [1, "page-btn", 3, "active"], ["aria-label", "Next page", 1, "page-btn", 3, "click", "disabled"], ["colspan", "8"], [1, "table-empty-state"], [1, "site-name-cell"], ["aria-hidden", "true", 1, "site-pin"], [1, "name-primary"], [1, "name-secondary"], [1, "company-link", 3, "routerLink"], [1, "coord-display", 3, "title"], [1, "mono"], [1, "weight-bar-container", 2, "min-width", "100px"], [1, "weight-bar-track"], [1, "weight-bar-fill"], [2, "font-size", "11px", "color", "var(--color-neutral-500)", "min-width", "34px", "text-align", "right"], [1, "badge"], [1, "actions"], ["title", "Edit", 1, "btn", "btn-icon", 3, "click"], [1, "btn", "btn-icon", 3, "click", "title"], ["title", "Delete", 1, "btn", "btn-icon", 2, "color", "var(--color-danger-600)", 3, "click"], [1, "muted"], [1, "page-btn", 3, "click"], [1, "card", 2, "padding", "var(--space-8)", "text-align", "center", "grid-column", "1 / -1"], [1, "site-card", "card"], [1, "material-icons", 2, "font-size", "48px", "color", "var(--color-neutral-300)"], [2, "color", "var(--color-neutral-500)"], [1, "site-card-header"], [1, "material-icons", "site-card-pin"], [1, "site-card-title-area"], [1, "site-card-name"], [1, "site-card-company"], [1, "badge", 2, "margin-left", "auto"], [1, "site-card-body"], [1, "site-card-location"], [1, "coord-display", "site-card-coord"], [1, "site-card-weight"], [1, "site-weight-value"], [1, "weight-bar-container", 2, "flex", "1"], [1, "site-weight-pct"], [1, "site-card-actions"], [1, "material-icons", 2, "font-size", "14px"], ["role", "dialog", "aria-modal", "true", 1, "modal-overlay", 3, "click"], [1, "modal", "modal-lg", 3, "click"], [1, "modal-header"], ["aria-label", "Close", 1, "btn", "btn-icon", 3, "click"], ["novalidate", "", 3, "ngSubmit", "formGroup"], [1, "modal-body"], [1, "grid-2"], [1, "form-group", 2, "grid-column", "span 2"], ["for", "sSiteName", 1, "form-label"], ["id", "sSiteName", "formControlName", "name", 1, "form-control"], ["role", "alert", 1, "form-error"], [1, "form-group", 2, "margin-top", "var(--space-4)"], ["for", "sCompany", 1, "form-label"], ["id", "sCompany", "formControlName", "companyId", 1, "form-control"], ["value", ""], ["for", "sDesc", 1, "form-label"], ["id", "sDesc", "formControlName", "description", "placeholder", "Optional description", 1, "form-control"], [1, "divider"], [1, "section-title"], [1, "form-hint", 2, "margin-bottom", "var(--space-3)"], [1, "form-group"], ["for", "sLat", 1, "form-label"], ["id", "sLat", "type", "number", "step", "0.0001", "formControlName", "latitude", "placeholder", "e.g. 40.7128", 1, "form-control"], ["for", "sLon", 1, "form-label"], ["id", "sLon", "type", "number", "step", "0.0001", "formControlName", "longitude", "placeholder", "e.g. \u221274.0060", 1, "form-control"], ["for", "sWeight", 1, "form-label"], ["id", "sWeight", "type", "number", "min", "0", "step", "1", "formControlName", "weightTons", "placeholder", "e.g. 450", 1, "form-control", 2, "max-width", "240px"], [1, "form-hint"], [1, "grid-3"], ["for", "sAddr", 1, "form-label"], ["id", "sAddr", "formControlName", "address", "autocomplete", "street-address", 1, "form-control"], ["for", "sCity", 1, "form-label"], ["id", "sCity", "formControlName", "city", "autocomplete", "address-level2", 1, "form-control"], ["for", "sCountry", 1, "form-label"], ["id", "sCountry", "formControlName", "country", "autocomplete", "country-name", 1, "form-control"], [1, "modal-footer"], ["type", "button", 1, "btn", "btn-secondary", 3, "click"], ["type", "submit", 1, "btn", "btn-primary", 3, "disabled"], [1, "modal", "modal-sm"], ["aria-label", "Cancel", 1, "btn", "btn-icon", 3, "click"], [2, "margin-top", "var(--space-2)", "font-size", "var(--font-size-small)", "color", "var(--color-neutral-500)"], [1, "btn", "btn-secondary", 3, "click"], [1, "btn", "btn-danger", 3, "click"]], template: function SitesComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275elementStart(0, "div", 0)(1, "div", 1)(2, "div")(3, "h1", 2);
        \u0275\u0275text(4, "Consumption Sites");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(5, "p", 3);
        \u0275\u0275text(6);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(7, "div", 4)(8, "button", 5);
        \u0275\u0275listener("click", function SitesComponent_Template_button_click_8_listener() {
          return ctx.openCreateModal();
        });
        \u0275\u0275elementStart(9, "span", 6);
        \u0275\u0275text(10, "add_location");
        \u0275\u0275elementEnd();
        \u0275\u0275text(11, " Add Site ");
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(12, "div", 7)(13, "div", 8)(14, "div", 9)(15, "div", 10)(16, "span", 11);
        \u0275\u0275text(17, "search");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(18, "input", 12);
        \u0275\u0275twoWayListener("ngModelChange", function SitesComponent_Template_input_ngModelChange_18_listener($event) {
          \u0275\u0275twoWayBindingSet(ctx.filter.search, $event) || (ctx.filter.search = $event);
          return $event;
        });
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(19, "select", 13);
        \u0275\u0275twoWayListener("ngModelChange", function SitesComponent_Template_select_ngModelChange_19_listener($event) {
          \u0275\u0275twoWayBindingSet(ctx.filter.companyId, $event) || (ctx.filter.companyId = $event);
          return $event;
        });
        \u0275\u0275elementStart(20, "option", 14);
        \u0275\u0275text(21, "All Companies");
        \u0275\u0275elementEnd();
        \u0275\u0275repeaterCreate(22, SitesComponent_For_23_Template, 2, 2, "option", 15, _forTrack0);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(24, "select", 16);
        \u0275\u0275twoWayListener("ngModelChange", function SitesComponent_Template_select_ngModelChange_24_listener($event) {
          \u0275\u0275twoWayBindingSet(ctx.filter.status, $event) || (ctx.filter.status = $event);
          return $event;
        });
        \u0275\u0275elementStart(25, "option", 14);
        \u0275\u0275text(26, "All Statuses");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(27, "option", 17);
        \u0275\u0275text(28, "Active");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(29, "option", 18);
        \u0275\u0275text(30, "Inactive");
        \u0275\u0275elementEnd()();
        \u0275\u0275template(31, SitesComponent_Conditional_31_Template, 4, 0, "button", 19);
        \u0275\u0275elementStart(32, "div", 20)(33, "button", 21);
        \u0275\u0275listener("click", function SitesComponent_Template_button_click_33_listener() {
          return ctx.viewMode.set("table");
        });
        \u0275\u0275elementStart(34, "span", 6);
        \u0275\u0275text(35, "table_rows");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(36, "button", 22);
        \u0275\u0275listener("click", function SitesComponent_Template_button_click_36_listener() {
          return ctx.viewMode.set("cards");
        });
        \u0275\u0275elementStart(37, "span", 6);
        \u0275\u0275text(38, "grid_view");
        \u0275\u0275elementEnd()()()()()();
        \u0275\u0275template(39, SitesComponent_Conditional_39_Template, 40, 14, "div", 23)(40, SitesComponent_Conditional_40_Template, 4, 1, "div", 24);
        \u0275\u0275elementEnd();
        \u0275\u0275template(41, SitesComponent_Conditional_41_Template, 78, 21, "div", 25)(42, SitesComponent_Conditional_42_Template, 23, 1, "div", 26);
      }
      if (rf & 2) {
        \u0275\u0275advance(6);
        \u0275\u0275textInterpolate2("", ctx.filteredSites().length, " sites \xB7 ", ctx.totalTonsFormatted(), " total traffic");
        \u0275\u0275advance(12);
        \u0275\u0275twoWayProperty("ngModel", ctx.filter.search);
        \u0275\u0275advance();
        \u0275\u0275twoWayProperty("ngModel", ctx.filter.companyId);
        \u0275\u0275advance(3);
        \u0275\u0275repeater(ctx.dataService.companies());
        \u0275\u0275advance(2);
        \u0275\u0275twoWayProperty("ngModel", ctx.filter.status);
        \u0275\u0275advance(7);
        \u0275\u0275conditional(31, ctx.filter.search || ctx.filter.status !== "ALL" || ctx.filter.companyId !== "ALL" ? 31 : -1);
        \u0275\u0275advance(2);
        \u0275\u0275classProp("active-mode", ctx.viewMode() === "table");
        \u0275\u0275advance(3);
        \u0275\u0275classProp("active-mode", ctx.viewMode() === "cards");
        \u0275\u0275advance(3);
        \u0275\u0275conditional(39, ctx.viewMode() === "table" ? 39 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(40, ctx.viewMode() === "cards" ? 40 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(41, ctx.showModal() ? 41 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(42, ctx.deleteTarget() ? 42 : -1);
      }
    }, dependencies: [CommonModule, RouterLink, FormsModule, \u0275NgNoValidate, NgSelectOption, \u0275NgSelectMultipleOption, DefaultValueAccessor, NumberValueAccessor, SelectControlValueAccessor, NgControlStatus, NgControlStatusGroup, MinValidator, NgModel, ReactiveFormsModule, FormGroupDirective, FormControlName], styles: ["\n\n.filter-row[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-3);\n  flex-wrap: wrap;\n}\n.search-field[_ngcontent-%COMP%] {\n  position: relative;\n  flex: 1;\n  min-width: 200px;\n  .form-control {\n    padding-left: 36px;\n  }\n}\n.search-icon-sm[_ngcontent-%COMP%] {\n  position: absolute;\n  left: 10px;\n  top: 50%;\n  transform: translateY(-50%);\n  font-size: 18px;\n  color: var(--color-neutral-500);\n  pointer-events: none;\n  z-index: 1;\n}\n.filter-select[_ngcontent-%COMP%] {\n  max-width: 180px;\n}\n.active-mode[_ngcontent-%COMP%] {\n  background-color: var(--color-primary-050);\n  color: var(--color-primary-700);\n}\n.site-name-cell[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-2);\n}\n.site-pin[_ngcontent-%COMP%]   .material-icons[_ngcontent-%COMP%] {\n  font-size: 22px;\n}\n.name-primary[_ngcontent-%COMP%] {\n  font-weight: 500;\n}\n.name-secondary[_ngcontent-%COMP%] {\n  font-size: 11px;\n  color: var(--color-neutral-500);\n}\n.company-link[_ngcontent-%COMP%] {\n  color: var(--color-primary-600);\n  text-decoration: none;\n  font-size: var(--font-size-small);\n  &:hover {\n    text-decoration: underline;\n  }\n}\n.muted[_ngcontent-%COMP%] {\n  color: var(--color-neutral-400);\n}\n.site-cards-grid[_ngcontent-%COMP%] {\n  display: grid;\n  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));\n  gap: var(--space-5);\n}\n.site-card[_ngcontent-%COMP%] {\n  display: flex;\n  flex-direction: column;\n}\n.site-card-header[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: flex-start;\n  gap: var(--space-3);\n  padding: var(--space-4) var(--space-5) var(--space-3);\n}\n.site-card-pin[_ngcontent-%COMP%] {\n  font-size: 28px;\n  flex-shrink: 0;\n  margin-top: -2px;\n}\n.site-card-title-area[_ngcontent-%COMP%] {\n  flex: 1;\n}\n.site-card-name[_ngcontent-%COMP%] {\n  font-size: var(--font-size-h3);\n  font-weight: 600;\n}\n.site-card-company[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  color: var(--color-primary-600);\n  margin-top: 2px;\n}\n.site-card-body[_ngcontent-%COMP%] {\n  padding: var(--space-2) var(--space-5) var(--space-4);\n  flex: 1;\n}\n.site-card-location[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: 4px;\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-700);\n  margin-bottom: var(--space-2);\n}\n.site-card-coord[_ngcontent-%COMP%] {\n  font-size: 12px;\n  margin-bottom: var(--space-3);\n}\n.site-card-weight[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-2);\n}\n.site-weight-value[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  font-weight: 600;\n  min-width: 50px;\n}\n.site-weight-pct[_ngcontent-%COMP%] {\n  font-size: 11px;\n  color: var(--color-neutral-500);\n  min-width: 28px;\n}\n.site-card-actions[_ngcontent-%COMP%] {\n  display: flex;\n  gap: var(--space-2);\n  padding: var(--space-3) var(--space-5);\n  border-top: 1px solid var(--color-neutral-300);\n}\n/*# sourceMappingURL=sites.component.css.map */"] });
  }
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && \u0275setClassDebugInfo(SitesComponent, { className: "SitesComponent", filePath: "src\\app\\features\\sites\\sites.component.ts", lineNumber: 484 });
})();
export {
  SitesComponent
};
//# sourceMappingURL=chunk-QZDUQ3UQ.js.map
