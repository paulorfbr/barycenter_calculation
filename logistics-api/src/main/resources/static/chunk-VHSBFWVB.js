import {
  ToastService
} from "./chunk-7XCFZB4I.js";
import {
  require_leaflet_src
} from "./chunk-RYEWYC7T.js";
import {
  DefaultValueAccessor,
  FormsModule,
  MaxValidator,
  MinValidator,
  NgControlStatus,
  NgModel,
  NgSelectOption,
  NumberValueAccessor,
  ReactiveFormsModule,
  SelectControlValueAccessor,
  ɵNgSelectMultipleOption
} from "./chunk-NJA75FPG.js";
import {
  DataService
} from "./chunk-E6436NOF.js";
import {
  ActivatedRoute,
  CommonModule,
  DecimalPipe,
  RouterLink,
  __spreadProps,
  __spreadValues,
  __toESM,
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
  ɵɵloadQuery,
  ɵɵnextContext,
  ɵɵpipe,
  ɵɵpipeBind2,
  ɵɵproperty,
  ɵɵpureFunction0,
  ɵɵpureFunction1,
  ɵɵqueryRefresh,
  ɵɵrepeater,
  ɵɵrepeaterCreate,
  ɵɵresetView,
  ɵɵrestoreView,
  ɵɵstyleProp,
  ɵɵtemplate,
  ɵɵtext,
  ɵɵtextInterpolate,
  ɵɵtextInterpolate1,
  ɵɵtextInterpolate2,
  ɵɵtwoWayBindingSet,
  ɵɵtwoWayListener,
  ɵɵtwoWayProperty,
  ɵɵviewQuery
} from "./chunk-WAAQAFSM.js";

// src/app/features/barycenter/barycenter.component.ts
var L = __toESM(require_leaflet_src());
var _c0 = ["mapEl"];
var _forTrack0 = ($index, $item) => $item.id;
var _forTrack1 = ($index, $item) => $item.logisticsCenterId;
var _forTrack2 = ($index, $item) => $item.siteId;
var _c1 = () => ["/sites"];
var _c2 = (a0) => ({ companyId: a0 });
function BarycenterComponent_For_16_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "option", 12);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const c_r2 = ctx.$implicit;
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275property("value", c_r2.id);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate2("", c_r2.name, " (", ctx_r2.getSiteCount(c_r2.id), " sites)");
  }
}
function BarycenterComponent_Conditional_17_Conditional_9_Template(rf, ctx) {
  if (rf & 1) {
    const _r5 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 37)(1, "div", 8)(2, "label", 41);
    \u0275\u0275text(3, "Max Iterations");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(4, "input", 42);
    \u0275\u0275twoWayListener("ngModelChange", function BarycenterComponent_Conditional_17_Conditional_9_Template_input_ngModelChange_4_listener($event) {
      \u0275\u0275restoreView(_r5);
      const ctx_r2 = \u0275\u0275nextContext(2);
      \u0275\u0275twoWayBindingSet(ctx_r2.maxIterations, $event) || (ctx_r2.maxIterations = $event);
      return \u0275\u0275resetView($event);
    });
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(5, "div", 8)(6, "label", 43);
    \u0275\u0275text(7, "Tolerance (km)");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(8, "input", 44);
    \u0275\u0275twoWayListener("ngModelChange", function BarycenterComponent_Conditional_17_Conditional_9_Template_input_ngModelChange_8_listener($event) {
      \u0275\u0275restoreView(_r5);
      const ctx_r2 = \u0275\u0275nextContext(2);
      \u0275\u0275twoWayBindingSet(ctx_r2.toleranceKm, $event) || (ctx_r2.toleranceKm = $event);
      return \u0275\u0275resetView($event);
    });
    \u0275\u0275elementEnd()()();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext(2);
    \u0275\u0275advance(4);
    \u0275\u0275twoWayProperty("ngModel", ctx_r2.maxIterations);
    \u0275\u0275advance(4);
    \u0275\u0275twoWayProperty("ngModel", ctx_r2.toleranceKm);
  }
}
function BarycenterComponent_Conditional_17_Conditional_11_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "strong");
    \u0275\u0275text(1, "Weiszfeld:");
    \u0275\u0275elementEnd();
    \u0275\u0275text(2, " Minimises sum of weighted geodesic distances (geometric median). Best for logistics cost optimisation. ");
  }
}
function BarycenterComponent_Conditional_17_Conditional_12_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "strong");
    \u0275\u0275text(1, "Simple Barycenter:");
    \u0275\u0275elementEnd();
    \u0275\u0275text(2, " Weighted arithmetic mean of coordinates. Instant convergence, minimises squared distances. ");
  }
}
function BarycenterComponent_Conditional_17_Conditional_14_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275element(0, "div", 45);
    \u0275\u0275text(1, " Calculating\u2026 ");
  }
}
function BarycenterComponent_Conditional_17_Conditional_15_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "span", 22);
    \u0275\u0275text(1, "my_location");
    \u0275\u0275elementEnd();
    \u0275\u0275text(2, " Calculate Barycenter ");
  }
}
function BarycenterComponent_Conditional_17_Conditional_16_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 40)(1, "span", 22);
    \u0275\u0275text(2, "warning");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3);
    \u0275\u0275elementStart(4, "a", 46);
    \u0275\u0275text(5, "Add sites");
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext(2);
    \u0275\u0275advance(3);
    \u0275\u0275textInterpolate1(" At least 2 active sites required (currently ", ctx_r2.activeSiteCount(), "). ");
  }
}
function BarycenterComponent_Conditional_17_Template(rf, ctx) {
  if (rf & 1) {
    const _r4 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 13)(1, "div", 8)(2, "label", 33);
    \u0275\u0275text(3, "Algorithm");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(4, "select", 34);
    \u0275\u0275twoWayListener("ngModelChange", function BarycenterComponent_Conditional_17_Template_select_ngModelChange_4_listener($event) {
      \u0275\u0275restoreView(_r4);
      const ctx_r2 = \u0275\u0275nextContext();
      \u0275\u0275twoWayBindingSet(ctx_r2.algorithm, $event) || (ctx_r2.algorithm = $event);
      return \u0275\u0275resetView($event);
    });
    \u0275\u0275elementStart(5, "option", 35);
    \u0275\u0275text(6, "Simple Weighted Barycenter");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(7, "option", 36);
    \u0275\u0275text(8, "Weiszfeld Iterative Refinement");
    \u0275\u0275elementEnd()()();
    \u0275\u0275template(9, BarycenterComponent_Conditional_17_Conditional_9_Template, 9, 2, "div", 37);
    \u0275\u0275elementStart(10, "div", 38);
    \u0275\u0275template(11, BarycenterComponent_Conditional_17_Conditional_11_Template, 3, 0)(12, BarycenterComponent_Conditional_17_Conditional_12_Template, 3, 0);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(13, "button", 39);
    \u0275\u0275listener("click", function BarycenterComponent_Conditional_17_Template_button_click_13_listener() {
      \u0275\u0275restoreView(_r4);
      const ctx_r2 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r2.runCalculation());
    });
    \u0275\u0275template(14, BarycenterComponent_Conditional_17_Conditional_14_Template, 2, 0)(15, BarycenterComponent_Conditional_17_Conditional_15_Template, 3, 0);
    \u0275\u0275elementEnd();
    \u0275\u0275template(16, BarycenterComponent_Conditional_17_Conditional_16_Template, 6, 1, "p", 40);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275advance(4);
    \u0275\u0275twoWayProperty("ngModel", ctx_r2.algorithm);
    \u0275\u0275advance(5);
    \u0275\u0275conditional(9, ctx_r2.algorithm === "weiszfeld-iterative" ? 9 : -1);
    \u0275\u0275advance(2);
    \u0275\u0275conditional(11, ctx_r2.algorithm === "weiszfeld-iterative" ? 11 : 12);
    \u0275\u0275advance(2);
    \u0275\u0275property("disabled", ctx_r2.activeSiteCount() < 2 || ctx_r2.calculating());
    \u0275\u0275advance();
    \u0275\u0275conditional(14, ctx_r2.calculating() ? 14 : 15);
    \u0275\u0275advance(2);
    \u0275\u0275conditional(16, ctx_r2.activeSiteCount() < 2 ? 16 : -1);
  }
}
function BarycenterComponent_Conditional_18_For_13_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 51)(1, "span", 52);
    \u0275\u0275text(2, "location_on");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(3, "div", 53)(4, "p", 54);
    \u0275\u0275text(5);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(6, "p", 55);
    \u0275\u0275text(7);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(8, "div", 56)(9, "div", 57);
    \u0275\u0275element(10, "div", 58);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(11, "span", 59);
    \u0275\u0275text(12);
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    const site_r6 = ctx.$implicit;
    const ctx_r2 = \u0275\u0275nextContext(2);
    \u0275\u0275classProp("inactive-site", site_r6.status === "INACTIVE");
    \u0275\u0275advance();
    \u0275\u0275styleProp("color", site_r6.status === "ACTIVE" ? "var(--color-warning-600)" : "var(--color-neutral-300)");
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate(site_r6.name);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(site_r6.city || site_r6.formattedCoordinate);
    \u0275\u0275advance(3);
    \u0275\u0275styleProp("width", ctx_r2.siteWeightPercent(site_r6), "%")("background-color", site_r6.status === "ACTIVE" ? "var(--color-primary-600)" : "var(--color-neutral-300)");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(site_r6.weightFormatted);
  }
}
function BarycenterComponent_Conditional_18_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 6)(1, "div", 16)(2, "div")(3, "h2", 47);
    \u0275\u0275text(4, "Input Sites");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "p", 18);
    \u0275\u0275text(6);
    \u0275\u0275pipe(7, "number");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(8, "a", 48)(9, "span", 22);
    \u0275\u0275text(10, "edit");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(11, "div", 49);
    \u0275\u0275repeaterCreate(12, BarycenterComponent_Conditional_18_For_13_Template, 13, 11, "div", 50, _forTrack0);
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275advance(6);
    \u0275\u0275textInterpolate2("", ctx_r2.activeSiteCount(), " active \xB7 ", \u0275\u0275pipeBind2(7, 4, ctx_r2.totalTons(), "1.0-0"), " t total");
    \u0275\u0275advance(2);
    \u0275\u0275property("routerLink", \u0275\u0275pureFunction0(7, _c1))("queryParams", \u0275\u0275pureFunction1(8, _c2, ctx_r2.selectedCompanyId));
    \u0275\u0275advance(4);
    \u0275\u0275repeater(ctx_r2.selectedCompanySites());
  }
}
function BarycenterComponent_Conditional_19_For_6_Conditional_9_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "span", 68);
    \u0275\u0275text(1, "Latest");
    \u0275\u0275elementEnd();
  }
}
function BarycenterComponent_Conditional_19_For_6_Template(rf, ctx) {
  if (rf & 1) {
    const _r7 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 62);
    \u0275\u0275listener("click", function BarycenterComponent_Conditional_19_For_6_Template_div_click_0_listener() {
      const r_r8 = \u0275\u0275restoreView(_r7).$implicit;
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.selectResult(r_r8));
    })("keydown.enter", function BarycenterComponent_Conditional_19_For_6_Template_div_keydown_enter_0_listener() {
      const r_r8 = \u0275\u0275restoreView(_r7).$implicit;
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.selectResult(r_r8));
    });
    \u0275\u0275elementStart(1, "div", 63)(2, "span", 64);
    \u0275\u0275text(3, "my_location");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(4, "div", 65)(5, "p", 66);
    \u0275\u0275text(6);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(7, "p", 67);
    \u0275\u0275text(8);
    \u0275\u0275template(9, BarycenterComponent_Conditional_19_For_6_Conditional_9_Template, 2, 0, "span", 68);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(10, "span", 69);
    \u0275\u0275text(11);
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    let tmp_12_0;
    const r_r8 = ctx.$implicit;
    const \u0275$index_151_r9 = ctx.$index;
    const ctx_r2 = \u0275\u0275nextContext(2);
    \u0275\u0275classProp("history-selected", ((tmp_12_0 = ctx_r2.activeResult()) == null ? null : tmp_12_0.logisticsCenterId) === r_r8.logisticsCenterId);
    \u0275\u0275attribute("aria-label", "Select result " + r_r8.formattedCoordinate);
    \u0275\u0275advance();
    \u0275\u0275styleProp("background-color", ctx_r2.resultColor(r_r8.status));
    \u0275\u0275advance(5);
    \u0275\u0275textInterpolate(r_r8.formattedCoordinate);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate2(" ", r_r8.algorithmDescription === "weiszfeld-iterative" ? "Weiszfeld" : "Simple", " \xB7 ", r_r8.inputSiteCount, " sites ");
    \u0275\u0275advance();
    \u0275\u0275conditional(9, \u0275$index_151_r9 === 0 ? 9 : -1);
    \u0275\u0275advance();
    \u0275\u0275classMap(ctx_r2.statusBadgeClass(r_r8.status));
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(r_r8.status);
  }
}
function BarycenterComponent_Conditional_19_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 6)(1, "div", 16)(2, "h2", 47);
    \u0275\u0275text(3, "Calculation History");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(4, "div", 60);
    \u0275\u0275repeaterCreate(5, BarycenterComponent_Conditional_19_For_6_Template, 12, 12, "div", 61, _forTrack1);
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275advance(5);
    \u0275\u0275repeater(ctx_r2.companyResults());
  }
}
function BarycenterComponent_Conditional_27_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275text(0, " Optimal logistics center: ");
    \u0275\u0275elementStart(1, "strong", 19);
    \u0275\u0275text(2);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(ctx_r2.activeResult().formattedCoordinate);
  }
}
function BarycenterComponent_Conditional_28_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275text(0, " Select a company and run calculation to see the optimal location ");
  }
}
function BarycenterComponent_Conditional_48_Conditional_10_Template(rf, ctx) {
  if (rf & 1) {
    const _r10 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "button", 91);
    \u0275\u0275listener("click", function BarycenterComponent_Conditional_48_Conditional_10_Template_button_click_0_listener() {
      \u0275\u0275restoreView(_r10);
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.approveResult());
    });
    \u0275\u0275elementStart(1, "span", 22);
    \u0275\u0275text(2, "check");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Approve ");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(4, "button", 92);
    \u0275\u0275listener("click", function BarycenterComponent_Conditional_48_Conditional_10_Template_button_click_4_listener() {
      \u0275\u0275restoreView(_r10);
      const ctx_r2 = \u0275\u0275nextContext(2);
      return \u0275\u0275resetView(ctx_r2.rejectResult());
    });
    \u0275\u0275elementStart(5, "span", 22);
    \u0275\u0275text(6, "close");
    \u0275\u0275elementEnd();
    \u0275\u0275text(7, " Reject ");
    \u0275\u0275elementEnd();
  }
}
function BarycenterComponent_Conditional_48_For_60_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "tr")(1, "td")(2, "div", 93)(3, "span", 94);
    \u0275\u0275text(4, "location_on");
    \u0275\u0275elementEnd();
    \u0275\u0275text(5);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(6, "td", 95);
    \u0275\u0275text(7);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(8, "td", 19);
    \u0275\u0275text(9);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(10, "td")(11, "div", 96)(12, "div", 57);
    \u0275\u0275element(13, "div", 58);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(14, "span", 97);
    \u0275\u0275text(15);
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(16, "td", 19);
    \u0275\u0275text(17);
    \u0275\u0275elementEnd()();
  }
  if (rf & 2) {
    const s_r11 = ctx.$implicit;
    const ctx_r2 = \u0275\u0275nextContext(2);
    \u0275\u0275advance(5);
    \u0275\u0275textInterpolate1(" ", s_r11.siteName, " ");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate2("", s_r11.latitude.toFixed(4), ", ", s_r11.longitude.toFixed(4), "");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate1("", s_r11.weightTons >= 1e3 ? (s_r11.weightTons / 1e3).toFixed(1) + "k" : s_r11.weightTons.toFixed(0), " t");
    \u0275\u0275advance(4);
    \u0275\u0275styleProp("width", s_r11.weightTons / ctx_r2.activeResult().totalWeightedTons * 100, "%");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate1(" ", (s_r11.weightTons / ctx_r2.activeResult().totalWeightedTons * 100).toFixed(1), "% ");
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate1("", s_r11.distanceToOptimalKm.toFixed(1), " km");
  }
}
function BarycenterComponent_Conditional_48_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 31)(1, "div", 16)(2, "div")(3, "h2", 17);
    \u0275\u0275text(4, "Calculation Result");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "p", 18);
    \u0275\u0275text(6);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(7, "div", 70)(8, "span", 69);
    \u0275\u0275text(9);
    \u0275\u0275elementEnd();
    \u0275\u0275template(10, BarycenterComponent_Conditional_48_Conditional_10_Template, 8, 0);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(11, "div", 7)(12, "div", 71)(13, "div", 72)(14, "p", 73);
    \u0275\u0275text(15, "Optimal Location");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(16, "p", 74);
    \u0275\u0275text(17);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(18, "p", 75);
    \u0275\u0275text(19);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(20, "div", 76)(21, "div", 77)(22, "span", 78);
    \u0275\u0275text(23);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(24, "span", 79);
    \u0275\u0275text(25, "Algorithm");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(26, "div", 77)(27, "span", 78);
    \u0275\u0275text(28);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(29, "span", 79);
    \u0275\u0275text(30, "Iterations");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(31, "div", 77)(32, "span", 78);
    \u0275\u0275text(33);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(34, "span", 79);
    \u0275\u0275text(35, "Convergence Error");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(36, "div", 77)(37, "span", 78);
    \u0275\u0275text(38);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(39, "span", 79);
    \u0275\u0275text(40, "Total Weight");
    \u0275\u0275elementEnd()()()();
    \u0275\u0275element(41, "hr", 80);
    \u0275\u0275elementStart(42, "h3", 81);
    \u0275\u0275text(43, "Site Contribution Analysis");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(44, "div", 82)(45, "table", 83)(46, "thead")(47, "tr")(48, "th", 84);
    \u0275\u0275text(49, "Site");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(50, "th", 84);
    \u0275\u0275text(51, "Coordinates");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(52, "th", 84);
    \u0275\u0275text(53, "Weight");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(54, "th", 84);
    \u0275\u0275text(55, "Weight %");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(56, "th", 84);
    \u0275\u0275text(57, "Dist. to Optimal");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(58, "tbody");
    \u0275\u0275repeaterCreate(59, BarycenterComponent_Conditional_48_For_60_Template, 18, 8, "tr", null, _forTrack2);
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(61, "div", 85)(62, "div", 86)(63, "span", 87);
    \u0275\u0275text(64, "route");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(65, "div")(66, "p", 88);
    \u0275\u0275text(67);
    \u0275\u0275pipe(68, "number");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(69, "p", 89);
    \u0275\u0275text(70, "Avg weighted distance to all sites");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(71, "div", 86)(72, "span", 90);
    \u0275\u0275text(73, "check_circle");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(74, "div")(75, "p", 88);
    \u0275\u0275text(76);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(77, "p", 89);
    \u0275\u0275text(78, "Sites in calculation");
    \u0275\u0275elementEnd()()()()()();
  }
  if (rf & 2) {
    const ctx_r2 = \u0275\u0275nextContext();
    \u0275\u0275advance(6);
    \u0275\u0275textInterpolate(ctx_r2.getCompanyName(ctx_r2.activeResult().companyId));
    \u0275\u0275advance(2);
    \u0275\u0275classMap(ctx_r2.statusBadgeClass(ctx_r2.activeResult().status));
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(ctx_r2.activeResult().status);
    \u0275\u0275advance();
    \u0275\u0275conditional(10, ctx_r2.activeResult().status === "CANDIDATE" ? 10 : -1);
    \u0275\u0275advance(7);
    \u0275\u0275textInterpolate(ctx_r2.activeResult().formattedCoordinate);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate2(" Lat ", ctx_r2.activeResult().optimalLatitude.toFixed(6), ", Lon ", ctx_r2.activeResult().optimalLongitude.toFixed(6), " ");
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate(ctx_r2.activeResult().algorithmDescription === "weiszfeld-iterative" ? "Weiszfeld" : "Simple");
    \u0275\u0275advance(5);
    \u0275\u0275textInterpolate(ctx_r2.activeResult().iterationCount);
    \u0275\u0275advance(5);
    \u0275\u0275textInterpolate1("", ctx_r2.activeResult().convergenceErrorKm.toFixed(4), " km");
    \u0275\u0275advance(5);
    \u0275\u0275textInterpolate1("", (ctx_r2.activeResult().totalWeightedTons / 1e3).toFixed(1), "k t");
    \u0275\u0275advance(21);
    \u0275\u0275repeater(ctx_r2.sortedInputSites());
    \u0275\u0275advance(8);
    \u0275\u0275textInterpolate1("", \u0275\u0275pipeBind2(68, 14, ctx_r2.totalWeightedDistance(), "1.0-0"), " km");
    \u0275\u0275advance(9);
    \u0275\u0275textInterpolate(ctx_r2.activeResult().inputSiteCount);
  }
}
function BarycenterComponent_Conditional_49_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 32)(1, "span", 98);
    \u0275\u0275text(2, "my_location");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(3, "h2");
    \u0275\u0275text(4, "Start a Calculation");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "p");
    \u0275\u0275text(6, "Select a company from the left panel, choose an algorithm, and run the barycenter calculation to find the optimal logistics center location.");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(7, "div", 99)(8, "a", 100)(9, "span", 22);
    \u0275\u0275text(10, "domain");
    \u0275\u0275elementEnd();
    \u0275\u0275text(11, " Manage Companies ");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(12, "a", 101)(13, "span", 22);
    \u0275\u0275text(14, "add_location");
    \u0275\u0275elementEnd();
    \u0275\u0275text(15, " Add Sites ");
    \u0275\u0275elementEnd()()();
  }
}
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png"
});
var BarycenterComponent = class _BarycenterComponent {
  constructor() {
    this.dataService = inject(DataService);
    this.toast = inject(ToastService);
    this.route = inject(ActivatedRoute);
    this.selectedCompanyId = "";
    this.algorithm = "weiszfeld-iterative";
    this.maxIterations = 1e3;
    this.toleranceKm = 0.01;
    this.calculating = signal(false);
    this.activeResult = signal(null);
    this.satelliteMode = false;
    this.siteMarkers = [];
    this.radiusLines = [];
    this.selectedCompanySites = computed(() => this.selectedCompanyId ? this.dataService.getSitesByCompany(this.selectedCompanyId) : []);
    this.activeSiteCount = computed(() => this.selectedCompanySites().filter((s) => s.status === "ACTIVE").length);
    this.totalTons = computed(() => this.selectedCompanySites().filter((s) => s.status === "ACTIVE").reduce((sum, s) => sum + s.weightTons, 0));
    this.companyResults = computed(() => this.selectedCompanyId ? this.dataService.getResultsByCompany(this.selectedCompanyId) : []);
    this.sortedInputSites = computed(() => this.activeResult() ? [...this.activeResult().inputSites].sort((a, b) => b.weightTons - a.weightTons) : []);
    this.totalWeightedDistance = computed(() => {
      if (!this.activeResult())
        return 0;
      return this.activeResult().inputSites.reduce((sum, s) => sum + s.distanceToOptimalKm * s.weightTons / this.activeResult().totalWeightedTons, 0);
    });
  }
  // ── Lifecycle ──────────────────────────────────────────────────────────────
  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      if (params["companyId"]) {
        this.selectedCompanyId = params["companyId"];
        this.onCompanyChange();
      }
    });
  }
  ngAfterViewInit() {
    setTimeout(() => this.initMap(), 80);
  }
  ngOnDestroy() {
    this.map?.remove();
  }
  // ── Map ────────────────────────────────────────────────────────────────────
  initMap() {
    if (!this.mapEl?.nativeElement || this.map)
      return;
    this.streetLayer = L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", { attribution: "\xA9 OpenStreetMap contributors", maxZoom: 18 });
    this.satelliteLayer = L.tileLayer("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}", { attribution: "Tiles \xA9 Esri", maxZoom: 18 });
    this.map = L.map(this.mapEl.nativeElement, {
      center: [39.5, -98.35],
      zoom: 4,
      layers: [this.streetLayer],
      zoomControl: true
    });
    this.refreshMapMarkers();
  }
  refreshMapMarkers() {
    if (!this.map)
      return;
    this.siteMarkers.forEach((m) => m.remove());
    this.siteMarkers = [];
    this.radiusLines.forEach((l) => l.remove());
    this.radiusLines = [];
    const sites = this.selectedCompanySites();
    const result = this.activeResult();
    for (const site of sites) {
      const isActive = site.status === "ACTIVE";
      const marker2 = L.circleMarker([site.latitude, site.longitude], {
        radius: 8 + Math.sqrt(site.weightTons / 100),
        fillColor: isActive ? "#D97706" : "#D1D5DB",
        color: "#fff",
        weight: 2,
        opacity: 1,
        fillOpacity: isActive ? 0.85 : 0.4
      }).addTo(this.map);
      marker2.bindPopup(`
        <div style="min-width:180px;">
          <strong style="font-size:13px;">${site.name}</strong>
          ${site.city ? `<br><span style="color:#6B7280; font-size:11px;">${site.city}, ${site.country ?? ""}</span>` : ""}
          <hr style="margin:6px 0; border-color:#E5E7EB;"/>
          <span style="font-size:11px; color:#374151;">
            <b>Weight:</b> ${site.weightFormatted}<br/>
            <b>Coord:</b> <code>${site.formattedCoordinate}</code><br/>
            <b>Status:</b> ${site.status}
          </span>
        </div>
      `);
      this.siteMarkers.push(marker2);
      if (result && isActive) {
        const line = L.polyline([
          [site.latitude, site.longitude],
          [result.optimalLatitude, result.optimalLongitude]
        ], {
          color: "#5B9BD5",
          weight: 1.5,
          opacity: 0.35,
          dashArray: "5 5"
        }).addTo(this.map);
        this.radiusLines.push(line);
      }
    }
    if (this.baryMarker) {
      this.baryMarker.remove();
      this.baryMarker = void 0;
    }
    if (result) {
      const icon = L.divIcon({
        html: `<div style="
          width:22px; height:22px;
          background:#1F4E79;
          border:3px solid white;
          border-radius:50%;
          box-shadow:0 2px 8px rgba(0,0,0,0.35);
        "></div>`,
        className: "",
        iconSize: [22, 22],
        iconAnchor: [11, 11]
      });
      this.baryMarker = L.marker([result.optimalLatitude, result.optimalLongitude], { icon }).bindPopup(`
          <div style="min-width:200px;">
            <strong style="font-size:13px; color:#1F4E79;">Optimal Logistics Center</strong>
            <hr style="margin:6px 0; border-color:#E5E7EB;"/>
            <code style="font-size:12px;">${result.formattedCoordinate}</code><br/>
            <span style="font-size:11px; color:#374151;">
              Algorithm: ${result.algorithmDescription === "weiszfeld-iterative" ? "Weiszfeld" : "Simple"}<br/>
              Iterations: ${result.iterationCount}<br/>
              Sites: ${result.inputSiteCount}<br/>
              Total weight: ${(result.totalWeightedTons / 1e3).toFixed(1)}k t
            </span>
          </div>
        `).addTo(this.map);
    }
    const allLatLngs = sites.map((s) => [s.latitude, s.longitude]);
    if (result)
      allLatLngs.push([result.optimalLatitude, result.optimalLongitude]);
    if (allLatLngs.length > 0) {
      this.map.fitBounds(L.latLngBounds(allLatLngs), { padding: [40, 40] });
    } else {
      this.map.setView([39.5, -98.35], 4);
    }
  }
  resetMapView() {
    if (!this.map)
      return;
    const sites = this.selectedCompanySites();
    const result = this.activeResult();
    const allLatLngs = sites.map((s) => [s.latitude, s.longitude]);
    if (result)
      allLatLngs.push([result.optimalLatitude, result.optimalLongitude]);
    if (allLatLngs.length > 0) {
      this.map.fitBounds(L.latLngBounds(allLatLngs), { padding: [40, 40] });
    } else {
      this.map.setView([39.5, -98.35], 4);
    }
  }
  toggleSatellite() {
    if (!this.map)
      return;
    this.satelliteMode = !this.satelliteMode;
    if (this.satelliteMode) {
      this.map.removeLayer(this.streetLayer);
      this.map.addLayer(this.satelliteLayer);
    } else {
      this.map.removeLayer(this.satelliteLayer);
      this.map.addLayer(this.streetLayer);
    }
  }
  // ── Calculation ────────────────────────────────────────────────────────────
  onCompanyChange() {
    this.activeResult.set(null);
    const results = this.dataService.getResultsByCompany(this.selectedCompanyId);
    if (results.length > 0)
      this.activeResult.set(results[0]);
    setTimeout(() => {
      this.refreshMapMarkers();
      this.map?.invalidateSize();
    }, 100);
  }
  runCalculation() {
    if (this.activeSiteCount() < 2)
      return;
    this.calculating.set(true);
    setTimeout(() => {
      try {
        const result = this.dataService.calculate({
          companyId: this.selectedCompanyId,
          algorithm: this.algorithm,
          maxIterations: this.maxIterations,
          toleranceKm: this.toleranceKm
        });
        this.activeResult.set(result);
        this.refreshMapMarkers();
        this.toast.success("Barycenter calculated", `Optimal location: ${result.formattedCoordinate} \xB7 ${result.iterationCount} iteration(s)`);
      } catch (err) {
        this.toast.error("Calculation failed", err.message);
      } finally {
        this.calculating.set(false);
      }
    }, 600);
  }
  selectResult(result) {
    this.activeResult.set(result);
    this.refreshMapMarkers();
  }
  approveResult() {
    if (!this.activeResult())
      return;
    this.dataService.updateResultStatus(this.activeResult().logisticsCenterId, "APPROVED");
    this.activeResult.update((r) => r ? __spreadProps(__spreadValues({}, r), { status: "APPROVED" }) : r);
    this.toast.success("Result approved", "Logistics center candidate has been approved.");
  }
  rejectResult() {
    if (!this.activeResult())
      return;
    this.dataService.updateResultStatus(this.activeResult().logisticsCenterId, "REJECTED");
    this.activeResult.update((r) => r ? __spreadProps(__spreadValues({}, r), { status: "REJECTED" }) : r);
    this.toast.warning("Result rejected", "Logistics center candidate has been rejected.");
  }
  // ── Helpers ────────────────────────────────────────────────────────────────
  getSiteCount(companyId) {
    return this.dataService.getSitesByCompany(companyId).filter((s) => s.status === "ACTIVE").length;
  }
  getCompanyName(id) {
    return this.dataService.getCompanyById(id)?.name ?? id;
  }
  siteWeightPercent(site) {
    const max = this.totalTons();
    return max > 0 ? site.weightTons / max * 100 : 0;
  }
  statusBadgeClass(status) {
    const map2 = {
      CANDIDATE: "badge badge-info",
      APPROVED: "badge badge-success",
      CONFIRMED: "badge badge-success",
      REJECTED: "badge badge-danger"
    };
    return map2[status] ?? "badge badge-neutral";
  }
  resultColor(status) {
    const map2 = {
      CANDIDATE: "var(--color-info-600)",
      APPROVED: "var(--color-success-600)",
      CONFIRMED: "var(--color-success-600)",
      REJECTED: "var(--color-danger-600)"
    };
    return map2[status] ?? "var(--color-neutral-500)";
  }
  static {
    this.\u0275fac = function BarycenterComponent_Factory(t) {
      return new (t || _BarycenterComponent)();
    };
  }
  static {
    this.\u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({ type: _BarycenterComponent, selectors: [["app-barycenter"]], viewQuery: function BarycenterComponent_Query(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275viewQuery(_c0, 5);
      }
      if (rf & 2) {
        let _t;
        \u0275\u0275queryRefresh(_t = \u0275\u0275loadQuery()) && (ctx.mapEl = _t.first);
      }
    }, standalone: true, features: [\u0275\u0275StandaloneFeature], decls: 50, vars: 10, consts: [["mapEl", ""], [1, "page-content", "page-enter", "bary-layout"], [1, "bary-panel"], [1, "panel-header"], [1, "page-title", 2, "font-size", "var(--font-size-h2)"], [1, "page-subtitle"], [1, "card", "panel-card"], [1, "card-body"], [1, "form-group"], ["for", "bCompany", 1, "form-label"], ["id", "bCompany", 1, "form-control", 3, "ngModelChange", "ngModel"], ["value", ""], [3, "value"], [2, "margin-top", "var(--space-4)"], [1, "bary-main"], [1, "card", "map-card"], [1, "card-header"], [1, "card-title"], [1, "card-subtitle"], [1, "mono"], [1, "map-controls"], ["title", "Reset view", "aria-label", "Reset map view", 1, "btn", "btn-icon", 3, "click"], [1, "material-icons"], [1, "btn", "btn-icon", 3, "click", "title"], ["aria-label", "Interactive map showing consumption sites and optimal logistics center location", "role", "img", 1, "map-container"], ["aria-label", "Map legend", 1, "map-legend"], [1, "legend-item"], [1, "legend-dot", 2, "background", "var(--color-warning-600)"], [1, "legend-item", 2, "opacity", "0.4"], [1, "legend-dot", 2, "background", "var(--color-neutral-300)"], [1, "legend-dot", 2, "background", "var(--color-primary-700)", "width", "16px", "height", "16px", "border", "2px solid white", "box-shadow", "0 2px 6px rgba(0,0,0,0.3)"], [1, "result-detail", "card", "page-enter"], [1, "card", "empty-panel"], ["for", "bAlgo", 1, "form-label"], ["id", "bAlgo", 1, "form-control", 3, "ngModelChange", "ngModel"], ["value", "weighted-barycenter"], ["value", "weiszfeld-iterative"], [1, "grid-2", 2, "margin-top", "var(--space-3)"], [1, "algorithm-info", 2, "margin-top", "var(--space-3)"], [1, "btn", "btn-primary", "w-full", 2, "margin-top", "var(--space-4)", 3, "click", "disabled"], ["role", "alert", 1, "form-error", 2, "margin-top", "var(--space-2)"], ["for", "bMaxIter", 1, "form-label"], ["id", "bMaxIter", "type", "number", "min", "1", "max", "10000", 1, "form-control", 3, "ngModelChange", "ngModel"], ["for", "bTol", 1, "form-label"], ["id", "bTol", "type", "number", "min", "0.0001", "step", "0.001", 1, "form-control", 3, "ngModelChange", "ngModel"], [1, "spinner", 2, "width", "18px", "height", "18px", "border-width", "2px"], ["routerLink", "/sites"], [1, "card-title", 2, "font-size", "var(--font-size-h3)"], [1, "btn", "btn-ghost", "btn-sm", 3, "routerLink", "queryParams"], [1, "card-body", 2, "padding", "var(--space-2) var(--space-4)"], [1, "site-weight-row", 3, "inactive-site"], [1, "site-weight-row"], [1, "material-icons", "site-weight-icon"], [1, "site-weight-info"], [1, "site-weight-name"], [1, "site-weight-city"], [1, "site-weight-bar-col"], [1, "weight-bar-track"], [1, "weight-bar-fill"], [1, "site-weight-tons", "mono"], [1, "card-body", 2, "padding", "0"], ["role", "button", "tabindex", "0", 1, "history-row", 3, "history-selected"], ["role", "button", "tabindex", "0", 1, "history-row", 3, "click", "keydown.enter"], [1, "history-icon-wrap"], [1, "material-icons", 2, "font-size", "16px", "color", "#fff"], [1, "history-info"], [1, "history-coord", "mono"], [1, "history-meta"], [1, "badge", "badge-primary", 2, "font-size", "9px", "padding", "1px 6px"], [1, "badge"], [2, "display", "flex", "gap", "var(--space-2)"], [1, "result-primary"], [1, "result-coord-block"], [1, "result-label"], [1, "result-coord", "mono"], [1, "result-coord-detail"], [1, "result-meta-grid"], [1, "result-meta-item"], [1, "result-meta-value"], [1, "result-meta-label"], [1, "divider"], [2, "font-size", "var(--font-size-h3)", "margin-bottom", "var(--space-4)"], [2, "overflow-x", "auto"], ["aria-label", "Site contribution analysis", 1, "data-table", 2, "min-width", "560px"], ["scope", "col"], [1, "result-summary-bar"], [1, "result-summary-item"], [1, "material-icons", 2, "color", "var(--color-primary-600)"], [1, "result-summary-value"], [1, "result-summary-label"], [1, "material-icons", 2, "color", "var(--color-success-600)"], ["aria-label", "Approve this result", 1, "btn", "btn-primary", "btn-sm", 3, "click"], ["aria-label", "Reject this result", 1, "btn", "btn-secondary", "btn-sm", 3, "click"], [2, "display", "flex", "align-items", "center", "gap", "var(--space-2)"], [1, "material-icons", 2, "font-size", "18px", "color", "var(--color-warning-600)"], [1, "mono", 2, "font-size", "12px"], [1, "weight-bar-container", 2, "min-width", "120px"], [2, "font-size", "11px", "min-width", "32px", "text-align", "right", "color", "var(--color-neutral-500)"], [1, "material-icons", "empty-icon"], [1, "empty-actions"], ["routerLink", "/companies", 1, "btn", "btn-secondary"], ["routerLink", "/sites", 1, "btn", "btn-primary"]], template: function BarycenterComponent_Template(rf, ctx) {
      if (rf & 1) {
        const _r1 = \u0275\u0275getCurrentView();
        \u0275\u0275elementStart(0, "div", 1)(1, "aside", 2)(2, "div", 3)(3, "h1", 4);
        \u0275\u0275text(4, "Barycenter Calculator");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(5, "p", 5);
        \u0275\u0275text(6, "Optimal logistics center from weighted sites");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(7, "div", 6)(8, "div", 7)(9, "div", 8)(10, "label", 9);
        \u0275\u0275text(11, "Select Company");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(12, "select", 10);
        \u0275\u0275twoWayListener("ngModelChange", function BarycenterComponent_Template_select_ngModelChange_12_listener($event) {
          \u0275\u0275restoreView(_r1);
          \u0275\u0275twoWayBindingSet(ctx.selectedCompanyId, $event) || (ctx.selectedCompanyId = $event);
          return \u0275\u0275resetView($event);
        });
        \u0275\u0275listener("ngModelChange", function BarycenterComponent_Template_select_ngModelChange_12_listener() {
          \u0275\u0275restoreView(_r1);
          return \u0275\u0275resetView(ctx.onCompanyChange());
        });
        \u0275\u0275elementStart(13, "option", 11);
        \u0275\u0275text(14, "\u2014 Choose a company \u2014");
        \u0275\u0275elementEnd();
        \u0275\u0275repeaterCreate(15, BarycenterComponent_For_16_Template, 2, 3, "option", 12, _forTrack0);
        \u0275\u0275elementEnd()();
        \u0275\u0275template(17, BarycenterComponent_Conditional_17_Template, 17, 6, "div", 13);
        \u0275\u0275elementEnd()();
        \u0275\u0275template(18, BarycenterComponent_Conditional_18_Template, 14, 10, "div", 6)(19, BarycenterComponent_Conditional_19_Template, 7, 0, "div", 6);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(20, "main", 14)(21, "div", 15)(22, "div", 16)(23, "div")(24, "h2", 17);
        \u0275\u0275text(25, "Geographic Visualization");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(26, "p", 18);
        \u0275\u0275template(27, BarycenterComponent_Conditional_27_Template, 3, 1, "strong", 19)(28, BarycenterComponent_Conditional_28_Template, 1, 0);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(29, "div", 20)(30, "button", 21);
        \u0275\u0275listener("click", function BarycenterComponent_Template_button_click_30_listener() {
          \u0275\u0275restoreView(_r1);
          return \u0275\u0275resetView(ctx.resetMapView());
        });
        \u0275\u0275elementStart(31, "span", 22);
        \u0275\u0275text(32, "center_focus_strong");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(33, "button", 23);
        \u0275\u0275listener("click", function BarycenterComponent_Template_button_click_33_listener() {
          \u0275\u0275restoreView(_r1);
          return \u0275\u0275resetView(ctx.toggleSatellite());
        });
        \u0275\u0275elementStart(34, "span", 22);
        \u0275\u0275text(35);
        \u0275\u0275elementEnd()()()();
        \u0275\u0275element(36, "div", 24, 0);
        \u0275\u0275elementStart(38, "div", 25)(39, "div", 26);
        \u0275\u0275element(40, "span", 27);
        \u0275\u0275text(41, " Consumption Site ");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(42, "div", 28);
        \u0275\u0275element(43, "span", 29);
        \u0275\u0275text(44, " Inactive Site ");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(45, "div", 26);
        \u0275\u0275element(46, "span", 30);
        \u0275\u0275text(47, " Optimal Center ");
        \u0275\u0275elementEnd()()();
        \u0275\u0275template(48, BarycenterComponent_Conditional_48_Template, 79, 17, "div", 31)(49, BarycenterComponent_Conditional_49_Template, 16, 0, "div", 32);
        \u0275\u0275elementEnd()();
      }
      if (rf & 2) {
        \u0275\u0275advance(12);
        \u0275\u0275twoWayProperty("ngModel", ctx.selectedCompanyId);
        \u0275\u0275advance(3);
        \u0275\u0275repeater(ctx.dataService.companies());
        \u0275\u0275advance(2);
        \u0275\u0275conditional(17, ctx.selectedCompanyId ? 17 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(18, ctx.selectedCompanySites().length > 0 ? 18 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(19, ctx.companyResults().length > 0 ? 19 : -1);
        \u0275\u0275advance(8);
        \u0275\u0275conditional(27, ctx.activeResult() ? 27 : 28);
        \u0275\u0275advance(6);
        \u0275\u0275property("title", ctx.satelliteMode ? "Street map" : "Satellite");
        \u0275\u0275attribute("aria-label", ctx.satelliteMode ? "Switch to street map" : "Switch to satellite");
        \u0275\u0275advance(2);
        \u0275\u0275textInterpolate(ctx.satelliteMode ? "map" : "satellite");
        \u0275\u0275advance(13);
        \u0275\u0275conditional(48, ctx.activeResult() ? 48 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(49, !ctx.selectedCompanyId ? 49 : -1);
      }
    }, dependencies: [CommonModule, DecimalPipe, RouterLink, FormsModule, NgSelectOption, \u0275NgSelectMultipleOption, DefaultValueAccessor, NumberValueAccessor, SelectControlValueAccessor, NgControlStatus, MinValidator, MaxValidator, NgModel, ReactiveFormsModule], styles: ["\n\n.bary-layout[_ngcontent-%COMP%] {\n  display: grid;\n  grid-template-columns: 360px 1fr;\n  gap: var(--space-5);\n  align-items: start;\n  padding-bottom: var(--space-10);\n}\n@media (max-width: 1023px) {\n  .bary-layout[_ngcontent-%COMP%] {\n    grid-template-columns: 1fr;\n  }\n}\n.bary-panel[_ngcontent-%COMP%] {\n  display: flex;\n  flex-direction: column;\n  gap: var(--space-4);\n  position: sticky;\n  top: var(--space-6);\n  max-height: calc(100vh - var(--topbar-height) - var(--space-10));\n  overflow-y: auto;\n  padding-right: 4px;\n  &::-webkit-scrollbar {\n    width: 4px;\n  }\n  &::-webkit-scrollbar-track {\n    background: transparent;\n  }\n  &::-webkit-scrollbar-thumb {\n    background: var(--color-neutral-300);\n    border-radius: 2px;\n  }\n}\n.bary-main[_ngcontent-%COMP%] {\n  display: flex;\n  flex-direction: column;\n  gap: var(--space-5);\n}\n.panel-header[_ngcontent-%COMP%] {\n  padding: 0 var(--space-1);\n}\n.panel-card[_ngcontent-%COMP%] {\n}\n.map-card[_ngcontent-%COMP%] {\n  overflow: visible;\n}\n.map-container[_ngcontent-%COMP%] {\n  height: 440px;\n  width: 100%;\n}\n.map-controls[_ngcontent-%COMP%] {\n  display: flex;\n  gap: var(--space-1);\n  margin-left: auto;\n}\n.map-legend[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-5);\n  padding: var(--space-3) var(--space-5);\n  border-top: 1px solid var(--color-neutral-300);\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-700);\n}\n.legend-item[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-2);\n}\n.legend-dot[_ngcontent-%COMP%] {\n  width: 12px;\n  height: 12px;\n  border-radius: 50%;\n  display: inline-block;\n}\n.site-weight-row[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-2);\n  padding: var(--space-2) 0;\n  border-bottom: 1px solid var(--color-neutral-300);\n  &:last-child {\n    border-bottom: none;\n  }\n}\n.inactive-site[_ngcontent-%COMP%] {\n  opacity: 0.45;\n}\n.site-weight-icon[_ngcontent-%COMP%] {\n  font-size: 20px;\n  flex-shrink: 0;\n}\n.site-weight-info[_ngcontent-%COMP%] {\n  flex: 1;\n  min-width: 0;\n}\n.site-weight-name[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  font-weight: 500;\n  white-space: nowrap;\n  overflow: hidden;\n  text-overflow: ellipsis;\n}\n.site-weight-city[_ngcontent-%COMP%] {\n  font-size: 11px;\n  color: var(--color-neutral-500);\n}\n.site-weight-bar-col[_ngcontent-%COMP%] {\n  width: 60px;\n  flex-shrink: 0;\n}\n.site-weight-tons[_ngcontent-%COMP%] {\n  font-size: 11px;\n  min-width: 42px;\n  text-align: right;\n  color: var(--color-neutral-700);\n}\n.history-row[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-3);\n  padding: var(--space-3) var(--space-4);\n  cursor: pointer;\n  border-bottom: 1px solid var(--color-neutral-300);\n  transition: background-color 80ms ease-out;\n  &:last-child {\n    border-bottom: none;\n  }\n  &:hover {\n    background-color: var(--color-primary-050);\n  }\n  &.history-selected {\n    background-color: var(--color-primary-100);\n  }\n  &:focus-visible {\n    outline: 2px solid var(--color-primary-600);\n    outline-offset: -2px;\n  }\n}\n.history-icon-wrap[_ngcontent-%COMP%] {\n  width: 32px;\n  height: 32px;\n  border-radius: var(--radius-full);\n  display: flex;\n  align-items: center;\n  justify-content: center;\n  flex-shrink: 0;\n}\n.history-info[_ngcontent-%COMP%] {\n  flex: 1;\n  min-width: 0;\n}\n.history-coord[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  font-weight: 600;\n}\n.history-meta[_ngcontent-%COMP%] {\n  font-size: 11px;\n  color: var(--color-neutral-500);\n  margin-top: 2px;\n  display: flex;\n  align-items: center;\n  gap: var(--space-2);\n}\n.result-primary[_ngcontent-%COMP%] {\n  display: grid;\n  grid-template-columns: auto 1fr;\n  gap: var(--space-6);\n  align-items: start;\n  margin-bottom: var(--space-5);\n}\n@media (max-width: 767px) {\n  .result-primary[_ngcontent-%COMP%] {\n    grid-template-columns: 1fr;\n  }\n}\n.result-coord-block[_ngcontent-%COMP%] {\n  background-color: var(--color-primary-050);\n  border: 1px solid var(--color-primary-100);\n  border-radius: var(--radius-3);\n  padding: var(--space-4) var(--space-6);\n  text-align: center;\n}\n.result-label[_ngcontent-%COMP%] {\n  font-size: var(--font-size-label);\n  font-weight: 600;\n  color: var(--color-neutral-500);\n  text-transform: uppercase;\n  letter-spacing: 0.04em;\n  margin-bottom: var(--space-2);\n}\n.result-coord[_ngcontent-%COMP%] {\n  font-size: var(--font-size-h2);\n  font-weight: 700;\n  color: var(--color-primary-700);\n  margin-bottom: var(--space-1);\n}\n.result-coord-detail[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-500);\n}\n.result-meta-grid[_ngcontent-%COMP%] {\n  display: grid;\n  grid-template-columns: repeat(2, 1fr);\n  gap: var(--space-4);\n}\n.result-meta-item[_ngcontent-%COMP%] {\n  display: flex;\n  flex-direction: column;\n  gap: 3px;\n}\n.result-meta-value[_ngcontent-%COMP%] {\n  font-size: var(--font-size-h3);\n  font-weight: 700;\n  color: var(--color-neutral-900);\n}\n.result-meta-label[_ngcontent-%COMP%] {\n  font-size: var(--font-size-label);\n  font-weight: 600;\n  color: var(--color-neutral-500);\n  text-transform: uppercase;\n}\n.result-summary-bar[_ngcontent-%COMP%] {\n  display: flex;\n  gap: var(--space-6);\n  margin-top: var(--space-5);\n  padding: var(--space-4) var(--space-5);\n  background-color: var(--color-neutral-100);\n  border-radius: var(--radius-2);\n}\n.result-summary-item[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-3);\n  .material-icons {\n    font-size: 28px;\n  }\n}\n.result-summary-value[_ngcontent-%COMP%] {\n  font-size: var(--font-size-h3);\n  font-weight: 700;\n}\n.result-summary-label[_ngcontent-%COMP%] {\n  font-size: var(--font-size-small);\n  color: var(--color-neutral-500);\n}\n.empty-panel[_ngcontent-%COMP%] {\n  display: flex;\n  flex-direction: column;\n  align-items: center;\n  justify-content: center;\n  padding: var(--space-12) var(--space-8);\n  text-align: center;\n  min-height: 320px;\n}\n.empty-icon[_ngcontent-%COMP%] {\n  font-size: 56px;\n  color: var(--color-neutral-300);\n  margin-bottom: var(--space-4);\n}\n.empty-panel[_ngcontent-%COMP%]   h2[_ngcontent-%COMP%] {\n  font-size: var(--font-size-h2);\n  margin-bottom: var(--space-3);\n}\n.empty-panel[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\n  color: var(--color-neutral-500);\n  max-width: 440px;\n  margin-bottom: var(--space-5);\n  line-height: 1.6;\n}\n.empty-actions[_ngcontent-%COMP%] {\n  display: flex;\n  gap: var(--space-3);\n}\n/*# sourceMappingURL=barycenter.component.css.map */"] });
  }
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && \u0275setClassDebugInfo(BarycenterComponent, { className: "BarycenterComponent", filePath: "src\\app\\features\\barycenter\\barycenter.component.ts", lineNumber: 579 });
})();
export {
  BarycenterComponent
};
//# sourceMappingURL=chunk-VHSBFWVB.js.map
