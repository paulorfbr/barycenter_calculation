import {
  ToastService
} from "./chunk-7XCFZB4I.js";
import {
  DefaultValueAccessor,
  FormBuilder,
  FormControlName,
  FormGroupDirective,
  FormsModule,
  NgControlStatus,
  NgControlStatusGroup,
  NgModel,
  NgSelectOption,
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
  ɵɵpureFunction0,
  ɵɵpureFunction1,
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

// src/app/features/companies/companies.component.ts
var _forTrack0 = ($index, $item) => $item.id;
var _c0 = () => ["/sites"];
var _c1 = (a0) => ({ companyId: a0 });
var _c2 = () => ["/barycenter"];
function CompaniesComponent_Conditional_41_Template(rf, ctx) {
  if (rf & 1) {
    const _r1 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "button", 40);
    \u0275\u0275listener("click", function CompaniesComponent_Conditional_41_Template_button_click_0_listener() {
      \u0275\u0275restoreView(_r1);
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.clearFilters());
    });
    \u0275\u0275elementStart(1, "span", 6);
    \u0275\u0275text(2, "filter_list_off");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Clear ");
    \u0275\u0275elementEnd();
  }
}
function CompaniesComponent_Conditional_67_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "tr")(1, "td", 41)(2, "div", 42)(3, "span", 6);
    \u0275\u0275text(4, "domain_disabled");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "p");
    \u0275\u0275text(6, "No companies found matching your filters.");
    \u0275\u0275elementEnd()()()();
  }
}
function CompaniesComponent_For_69_Conditional_8_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 47);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const company_r4 = \u0275\u0275nextContext().$implicit;
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(company_r4.taxId);
  }
}
function CompaniesComponent_For_69_Conditional_16_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "a", 50);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const company_r4 = \u0275\u0275nextContext().$implicit;
    \u0275\u0275property("routerLink", \u0275\u0275pureFunction0(3, _c0))("queryParams", \u0275\u0275pureFunction1(4, _c1, company_r4.id));
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", company_r4.consumptionSiteCount, " ");
  }
}
function CompaniesComponent_For_69_Conditional_17_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "span", 57);
    \u0275\u0275text(1, "\u2014");
    \u0275\u0275elementEnd();
  }
}
function CompaniesComponent_For_69_Conditional_19_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275text(0);
  }
  if (rf & 2) {
    const company_r4 = \u0275\u0275nextContext().$implicit;
    const ctx_r1 = \u0275\u0275nextContext();
    \u0275\u0275textInterpolate1(" ", ctx_r1.formatTons(company_r4.totalTrafficTons), " ");
  }
}
function CompaniesComponent_For_69_Conditional_20_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "span", 57);
    \u0275\u0275text(1, "\u2014");
    \u0275\u0275elementEnd();
  }
}
function CompaniesComponent_For_69_Conditional_22_Conditional_3_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 59);
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const company_r4 = \u0275\u0275nextContext(2).$implicit;
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(company_r4.contactEmail);
  }
}
function CompaniesComponent_For_69_Conditional_22_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div")(1, "p", 58);
    \u0275\u0275text(2);
    \u0275\u0275elementEnd();
    \u0275\u0275template(3, CompaniesComponent_For_69_Conditional_22_Conditional_3_Template, 2, 1, "p", 59);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const company_r4 = \u0275\u0275nextContext().$implicit;
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(company_r4.contactName);
    \u0275\u0275advance();
    \u0275\u0275conditional(3, company_r4.contactEmail ? 3 : -1);
  }
}
function CompaniesComponent_For_69_Conditional_23_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "span", 57);
    \u0275\u0275text(1, "\u2014");
    \u0275\u0275elementEnd();
  }
}
function CompaniesComponent_For_69_Template(rf, ctx) {
  if (rf & 1) {
    const _r3 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "tr", 43);
    \u0275\u0275listener("click", function CompaniesComponent_For_69_Template_tr_click_0_listener() {
      const company_r4 = \u0275\u0275restoreView(_r3).$implicit;
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.selectedId.set(company_r4.id));
    });
    \u0275\u0275elementStart(1, "td")(2, "div", 44)(3, "span", 45);
    \u0275\u0275text(4);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "div")(6, "p", 46);
    \u0275\u0275text(7);
    \u0275\u0275elementEnd();
    \u0275\u0275template(8, CompaniesComponent_For_69_Conditional_8_Template, 2, 1, "p", 47);
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(9, "td")(10, "span", 48);
    \u0275\u0275text(11);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(12, "td")(13, "span", 49);
    \u0275\u0275text(14);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(15, "td");
    \u0275\u0275template(16, CompaniesComponent_For_69_Conditional_16_Template, 2, 6, "a", 50)(17, CompaniesComponent_For_69_Conditional_17_Template, 2, 0);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(18, "td", 51);
    \u0275\u0275template(19, CompaniesComponent_For_69_Conditional_19_Template, 1, 1)(20, CompaniesComponent_For_69_Conditional_20_Template, 2, 0);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(21, "td");
    \u0275\u0275template(22, CompaniesComponent_For_69_Conditional_22_Template, 4, 2, "div")(23, CompaniesComponent_For_69_Conditional_23_Template, 2, 0);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(24, "td", 52)(25, "button", 53);
    \u0275\u0275listener("click", function CompaniesComponent_For_69_Template_button_click_25_listener($event) {
      const company_r4 = \u0275\u0275restoreView(_r3).$implicit;
      const ctx_r1 = \u0275\u0275nextContext();
      ctx_r1.openEditModal(company_r4);
      return \u0275\u0275resetView($event.stopPropagation());
    });
    \u0275\u0275elementStart(26, "span", 6);
    \u0275\u0275text(27, "edit");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(28, "a", 54);
    \u0275\u0275listener("click", function CompaniesComponent_For_69_Template_a_click_28_listener($event) {
      \u0275\u0275restoreView(_r3);
      return \u0275\u0275resetView($event.stopPropagation());
    });
    \u0275\u0275elementStart(29, "span", 6);
    \u0275\u0275text(30, "location_on");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(31, "a", 55);
    \u0275\u0275listener("click", function CompaniesComponent_For_69_Template_a_click_31_listener($event) {
      \u0275\u0275restoreView(_r3);
      return \u0275\u0275resetView($event.stopPropagation());
    });
    \u0275\u0275elementStart(32, "span", 6);
    \u0275\u0275text(33, "my_location");
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(34, "button", 56);
    \u0275\u0275listener("click", function CompaniesComponent_For_69_Template_button_click_34_listener($event) {
      const company_r4 = \u0275\u0275restoreView(_r3).$implicit;
      const ctx_r1 = \u0275\u0275nextContext();
      ctx_r1.confirmDelete(company_r4);
      return \u0275\u0275resetView($event.stopPropagation());
    });
    \u0275\u0275elementStart(35, "span", 6);
    \u0275\u0275text(36, "delete_outline");
    \u0275\u0275elementEnd()()()();
  }
  if (rf & 2) {
    const company_r4 = ctx.$implicit;
    const ctx_r1 = \u0275\u0275nextContext();
    \u0275\u0275classProp("selected", ctx_r1.selectedId() === company_r4.id);
    \u0275\u0275advance(3);
    \u0275\u0275styleProp("background-color", ctx_r1.companyColor(company_r4.name));
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", company_r4.name.charAt(0), " ");
    \u0275\u0275advance(3);
    \u0275\u0275textInterpolate(company_r4.name);
    \u0275\u0275advance();
    \u0275\u0275conditional(8, company_r4.taxId ? 8 : -1);
    \u0275\u0275advance(3);
    \u0275\u0275textInterpolate(company_r4.type);
    \u0275\u0275advance(2);
    \u0275\u0275classMap(ctx_r1.statusClass(company_r4.status));
    \u0275\u0275advance();
    \u0275\u0275textInterpolate(company_r4.status);
    \u0275\u0275advance(2);
    \u0275\u0275conditional(16, company_r4.consumptionSiteCount > 0 ? 16 : 17);
    \u0275\u0275advance(3);
    \u0275\u0275conditional(19, company_r4.totalTrafficTons > 0 ? 19 : 20);
    \u0275\u0275advance(3);
    \u0275\u0275conditional(22, company_r4.contactName ? 22 : 23);
    \u0275\u0275advance(3);
    \u0275\u0275attribute("aria-label", "Edit " + company_r4.name);
    \u0275\u0275advance(3);
    \u0275\u0275property("routerLink", \u0275\u0275pureFunction0(22, _c0))("queryParams", \u0275\u0275pureFunction1(23, _c1, company_r4.id));
    \u0275\u0275attribute("aria-label", "View sites for " + company_r4.name);
    \u0275\u0275advance(3);
    \u0275\u0275property("routerLink", \u0275\u0275pureFunction0(25, _c2))("queryParams", \u0275\u0275pureFunction1(26, _c1, company_r4.id));
    \u0275\u0275attribute("aria-label", "Calculate barycenter for " + company_r4.name);
    \u0275\u0275advance(3);
    \u0275\u0275attribute("aria-label", "Delete " + company_r4.name);
  }
}
function CompaniesComponent_For_78_Template(rf, ctx) {
  if (rf & 1) {
    const _r5 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "button", 60);
    \u0275\u0275listener("click", function CompaniesComponent_For_78_Template_button_click_0_listener() {
      const p_r6 = \u0275\u0275restoreView(_r5).$implicit;
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.page.set(p_r6));
    });
    \u0275\u0275text(1);
    \u0275\u0275elementEnd();
  }
  if (rf & 2) {
    const p_r6 = ctx.$implicit;
    const ctx_r1 = \u0275\u0275nextContext();
    \u0275\u0275classProp("active", p_r6 === ctx_r1.page());
    \u0275\u0275attribute("aria-label", "Page " + p_r6)("aria-current", p_r6 === ctx_r1.page() ? "page" : null);
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", p_r6, " ");
  }
}
function CompaniesComponent_Conditional_82_Conditional_15_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 71)(1, "span", 6);
    \u0275\u0275text(2, "error_outline");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Name is required");
    \u0275\u0275elementEnd();
  }
}
function CompaniesComponent_Conditional_82_Conditional_31_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "div", 68)(1, "label", 91);
    \u0275\u0275text(2, "Status");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(3, "select", 92)(4, "option", 16);
    \u0275\u0275text(5, "Active");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(6, "option", 17);
    \u0275\u0275text(7, "Inactive");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(8, "option", 18);
    \u0275\u0275text(9, "Pending");
    \u0275\u0275elementEnd()()();
  }
}
function CompaniesComponent_Conditional_82_Conditional_44_Template(rf, ctx) {
  if (rf & 1) {
    \u0275\u0275elementStart(0, "p", 71)(1, "span", 6);
    \u0275\u0275text(2, "error_outline");
    \u0275\u0275elementEnd();
    \u0275\u0275text(3, " Enter a valid email");
    \u0275\u0275elementEnd();
  }
}
function CompaniesComponent_Conditional_82_Template(rf, ctx) {
  if (rf & 1) {
    const _r7 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 61);
    \u0275\u0275listener("click", function CompaniesComponent_Conditional_82_Template_div_click_0_listener() {
      \u0275\u0275restoreView(_r7);
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.closeModal());
    });
    \u0275\u0275elementStart(1, "div", 62);
    \u0275\u0275listener("click", function CompaniesComponent_Conditional_82_Template_div_click_1_listener($event) {
      \u0275\u0275restoreView(_r7);
      return \u0275\u0275resetView($event.stopPropagation());
    });
    \u0275\u0275elementStart(2, "div", 63)(3, "h2");
    \u0275\u0275text(4);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "button", 64);
    \u0275\u0275listener("click", function CompaniesComponent_Conditional_82_Template_button_click_5_listener() {
      \u0275\u0275restoreView(_r7);
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.closeModal());
    });
    \u0275\u0275elementStart(6, "span", 6);
    \u0275\u0275text(7, "close");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(8, "form", 65);
    \u0275\u0275listener("ngSubmit", function CompaniesComponent_Conditional_82_Template_form_ngSubmit_8_listener() {
      \u0275\u0275restoreView(_r7);
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.submitForm());
    });
    \u0275\u0275elementStart(9, "div", 66)(10, "div", 67)(11, "div", 68)(12, "label", 69);
    \u0275\u0275text(13, "Company Name *");
    \u0275\u0275elementEnd();
    \u0275\u0275element(14, "input", 70);
    \u0275\u0275template(15, CompaniesComponent_Conditional_82_Conditional_15_Template, 4, 0, "p", 71);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(16, "div", 68)(17, "label", 72);
    \u0275\u0275text(18, "Type *");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(19, "select", 73)(20, "option", 21);
    \u0275\u0275text(21, "Shipper");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(22, "option", 22);
    \u0275\u0275text(23, "Carrier");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(24, "option", 23);
    \u0275\u0275text(25, "Both");
    \u0275\u0275elementEnd()()()();
    \u0275\u0275elementStart(26, "div", 74)(27, "div", 68)(28, "label", 75);
    \u0275\u0275text(29, "Tax ID");
    \u0275\u0275elementEnd();
    \u0275\u0275element(30, "input", 76);
    \u0275\u0275elementEnd();
    \u0275\u0275template(31, CompaniesComponent_Conditional_82_Conditional_31_Template, 10, 0, "div", 68);
    \u0275\u0275elementEnd();
    \u0275\u0275element(32, "hr", 77);
    \u0275\u0275elementStart(33, "p", 78);
    \u0275\u0275text(34, "Contact Information");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(35, "div", 67)(36, "div", 68)(37, "label", 79);
    \u0275\u0275text(38, "Contact Name");
    \u0275\u0275elementEnd();
    \u0275\u0275element(39, "input", 80);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(40, "div", 68)(41, "label", 81);
    \u0275\u0275text(42, "Contact Email");
    \u0275\u0275elementEnd();
    \u0275\u0275element(43, "input", 82);
    \u0275\u0275template(44, CompaniesComponent_Conditional_82_Conditional_44_Template, 4, 0, "p", 71);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(45, "div", 83)(46, "label", 84);
    \u0275\u0275text(47, "Contact Phone");
    \u0275\u0275elementEnd();
    \u0275\u0275element(48, "input", 85);
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(49, "div", 83)(50, "label", 86);
    \u0275\u0275text(51, "Notes");
    \u0275\u0275elementEnd();
    \u0275\u0275element(52, "textarea", 87);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(53, "div", 88)(54, "button", 89);
    \u0275\u0275listener("click", function CompaniesComponent_Conditional_82_Template_button_click_54_listener() {
      \u0275\u0275restoreView(_r7);
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.closeModal());
    });
    \u0275\u0275text(55, "Cancel");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(56, "button", 90)(57, "span", 6);
    \u0275\u0275text(58);
    \u0275\u0275elementEnd();
    \u0275\u0275text(59);
    \u0275\u0275elementEnd()()()()();
  }
  if (rf & 2) {
    const ctx_r1 = \u0275\u0275nextContext();
    \u0275\u0275attribute("aria-label", ctx_r1.editTarget() ? "Edit company" : "Create company");
    \u0275\u0275advance(4);
    \u0275\u0275textInterpolate(ctx_r1.editTarget() ? "Edit Company" : "New Company");
    \u0275\u0275advance(4);
    \u0275\u0275property("formGroup", ctx_r1.form);
    \u0275\u0275advance(6);
    \u0275\u0275classProp("error", ctx_r1.fieldError("name"));
    \u0275\u0275advance();
    \u0275\u0275conditional(15, ctx_r1.fieldError("name") ? 15 : -1);
    \u0275\u0275advance(16);
    \u0275\u0275conditional(31, ctx_r1.editTarget() ? 31 : -1);
    \u0275\u0275advance(12);
    \u0275\u0275classProp("error", ctx_r1.fieldError("contactEmail"));
    \u0275\u0275advance();
    \u0275\u0275conditional(44, ctx_r1.fieldError("contactEmail") ? 44 : -1);
    \u0275\u0275advance(12);
    \u0275\u0275property("disabled", ctx_r1.form.invalid);
    \u0275\u0275advance(2);
    \u0275\u0275textInterpolate(ctx_r1.editTarget() ? "save" : "add");
    \u0275\u0275advance();
    \u0275\u0275textInterpolate1(" ", ctx_r1.editTarget() ? "Save Changes" : "Create Company", " ");
  }
}
function CompaniesComponent_Conditional_83_Template(rf, ctx) {
  if (rf & 1) {
    const _r8 = \u0275\u0275getCurrentView();
    \u0275\u0275elementStart(0, "div", 39)(1, "div", 93)(2, "div", 63)(3, "h2");
    \u0275\u0275text(4, "Delete Company");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(5, "button", 94);
    \u0275\u0275listener("click", function CompaniesComponent_Conditional_83_Template_button_click_5_listener() {
      \u0275\u0275restoreView(_r8);
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.deleteTarget.set(null));
    });
    \u0275\u0275elementStart(6, "span", 6);
    \u0275\u0275text(7, "close");
    \u0275\u0275elementEnd()()();
    \u0275\u0275elementStart(8, "div", 66)(9, "p");
    \u0275\u0275text(10, "Are you sure you want to delete ");
    \u0275\u0275elementStart(11, "strong");
    \u0275\u0275text(12);
    \u0275\u0275elementEnd();
    \u0275\u0275text(13, "?");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(14, "p", 95)(15, "span", 96);
    \u0275\u0275text(16, "warning");
    \u0275\u0275elementEnd();
    \u0275\u0275text(17);
    \u0275\u0275elementEnd()();
    \u0275\u0275elementStart(18, "div", 88)(19, "button", 97);
    \u0275\u0275listener("click", function CompaniesComponent_Conditional_83_Template_button_click_19_listener() {
      \u0275\u0275restoreView(_r8);
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.deleteTarget.set(null));
    });
    \u0275\u0275text(20, "Cancel");
    \u0275\u0275elementEnd();
    \u0275\u0275elementStart(21, "button", 98);
    \u0275\u0275listener("click", function CompaniesComponent_Conditional_83_Template_button_click_21_listener() {
      \u0275\u0275restoreView(_r8);
      const ctx_r1 = \u0275\u0275nextContext();
      return \u0275\u0275resetView(ctx_r1.executeDelete());
    });
    \u0275\u0275elementStart(22, "span", 6);
    \u0275\u0275text(23, "delete");
    \u0275\u0275elementEnd();
    \u0275\u0275text(24, " Delete ");
    \u0275\u0275elementEnd()()()();
  }
  if (rf & 2) {
    const ctx_r1 = \u0275\u0275nextContext();
    \u0275\u0275advance(12);
    \u0275\u0275textInterpolate(ctx_r1.deleteTarget().name);
    \u0275\u0275advance(5);
    \u0275\u0275textInterpolate1(" This will also delete all ", ctx_r1.deleteTarget().consumptionSiteCount, " associated consumption sites and cannot be undone. ");
  }
}
var CompaniesComponent = class _CompaniesComponent {
  constructor() {
    this.dataService = inject(DataService);
    this.toast = inject(ToastService);
    this.fb = inject(FormBuilder);
    this.filter = { search: "", status: "ALL", type: "ALL" };
    this.sortCol = "name";
    this.sortDir = "asc";
    this.page = signal(1);
    this.pageSize = 10;
    this.selectedId = signal(null);
    this.showModal = signal(false);
    this.editTarget = signal(null);
    this.deleteTarget = signal(null);
    this.form = this.fb.group({
      name: ["", [Validators.required, Validators.minLength(2)]],
      type: ["SHIPPER", Validators.required],
      taxId: [""],
      status: ["ACTIVE"],
      contactName: [""],
      contactEmail: ["", Validators.email],
      contactPhone: [""],
      notes: [""]
    });
    this.filteredCompanies = computed(() => {
      let list = [...this.dataService.companies()];
      const f = this.filter;
      if (f.search) {
        const q = f.search.toLowerCase();
        list = list.filter((c) => c.name.toLowerCase().includes(q) || (c.taxId ?? "").toLowerCase().includes(q) || (c.contactName ?? "").toLowerCase().includes(q));
      }
      if (f.status !== "ALL")
        list = list.filter((c) => c.status === f.status);
      if (f.type !== "ALL")
        list = list.filter((c) => c.type === f.type);
      list.sort((a, b) => {
        const av = a[this.sortCol];
        const bv = b[this.sortCol];
        const cmp = typeof av === "number" ? av - bv : String(av).localeCompare(String(bv));
        return this.sortDir === "asc" ? cmp : -cmp;
      });
      return list;
    });
    this.totalPages = computed(() => Math.ceil(this.filteredCompanies().length / this.pageSize) || 1);
    this.pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i + 1));
    this.pageStart = computed(() => (this.page() - 1) * this.pageSize + 1);
    this.pageEnd = computed(() => Math.min(this.page() * this.pageSize, this.filteredCompanies().length));
    this.pagedCompanies = computed(() => {
      const start = (this.page() - 1) * this.pageSize;
      return this.filteredCompanies().slice(start, start + this.pageSize);
    });
    this.COLORS = ["#1F4E79", "#2E6DA4", "#0891B2", "#16A34A", "#D97706", "#7C3AED"];
  }
  // ── Sorting ────────────────────────────────────────────────────────────────
  sort(col) {
    if (this.sortCol === col) {
      this.sortDir = this.sortDir === "asc" ? "desc" : "asc";
    } else {
      this.sortCol = col;
      this.sortDir = "asc";
    }
    this.page.set(1);
  }
  // ── Pagination ─────────────────────────────────────────────────────────────
  prevPage() {
    if (this.page() > 1)
      this.page.update((p) => p - 1);
  }
  nextPage() {
    if (this.page() < this.totalPages())
      this.page.update((p) => p + 1);
  }
  clearFilters() {
    this.filter = { search: "", status: "ALL", type: "ALL" };
    this.page.set(1);
  }
  // ── Modal lifecycle ────────────────────────────────────────────────────────
  openCreateModal() {
    this.editTarget.set(null);
    this.form.reset({ type: "SHIPPER", status: "ACTIVE" });
    this.showModal.set(true);
  }
  openEditModal(company) {
    this.editTarget.set(company);
    this.form.patchValue({
      name: company.name,
      type: company.type,
      taxId: company.taxId ?? "",
      status: company.status,
      contactName: company.contactName ?? "",
      contactEmail: company.contactEmail ?? "",
      contactPhone: company.contactPhone ?? "",
      notes: company.notes ?? ""
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
    const val = this.form.getRawValue();
    if (this.editTarget()) {
      const payload = {
        name: val.name,
        type: val.type,
        taxId: val.taxId || void 0,
        status: val.status,
        contactName: val.contactName || void 0,
        contactEmail: val.contactEmail || void 0,
        contactPhone: val.contactPhone || void 0,
        notes: val.notes || void 0
      };
      this.dataService.updateCompany(this.editTarget().id, payload);
      this.toast.success("Company updated", `${val.name} has been saved.`);
    } else {
      const payload = {
        name: val.name,
        type: val.type,
        taxId: val.taxId || void 0,
        contactName: val.contactName || void 0,
        contactEmail: val.contactEmail || void 0,
        contactPhone: val.contactPhone || void 0,
        notes: val.notes || void 0
      };
      this.dataService.createCompany(payload);
      this.toast.success("Company created", `${val.name} has been added.`);
    }
    this.closeModal();
  }
  confirmDelete(company) {
    this.deleteTarget.set(company);
  }
  executeDelete() {
    const c = this.deleteTarget();
    if (!c)
      return;
    this.dataService.deleteCompany(c.id);
    this.toast.success("Company deleted", `${c.name} has been removed.`);
    this.deleteTarget.set(null);
  }
  // ── Helpers ────────────────────────────────────────────────────────────────
  fieldError(field) {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && (ctrl.dirty || ctrl.touched));
  }
  statusClass(status) {
    const map = {
      ACTIVE: "badge badge-success",
      INACTIVE: "badge badge-neutral",
      PENDING: "badge badge-warning"
    };
    return map[status];
  }
  formatTons(t) {
    return t >= 1e3 ? `${(t / 1e3).toFixed(1)}k t` : `${t.toFixed(0)} t`;
  }
  companyColor(name) {
    return this.COLORS[name.charCodeAt(0) % this.COLORS.length];
  }
  static {
    this.\u0275fac = function CompaniesComponent_Factory(t) {
      return new (t || _CompaniesComponent)();
    };
  }
  static {
    this.\u0275cmp = /* @__PURE__ */ \u0275\u0275defineComponent({ type: _CompaniesComponent, selectors: [["app-companies"]], standalone: true, features: [\u0275\u0275StandaloneFeature], decls: 84, vars: 26, consts: [[1, "page-content", "page-enter"], [1, "page-header"], [1, "page-title"], [1, "page-subtitle"], [1, "page-actions"], [1, "btn", "btn-primary", 3, "click"], [1, "material-icons"], [1, "card", "filters-bar"], [1, "card-body", 2, "padding", "var(--space-4) var(--space-5)"], [1, "filter-row"], [1, "search-field"], ["aria-hidden", "true", 1, "material-icons", "search-icon-sm"], ["type", "search", "placeholder", "Search companies\u2026", "aria-label", "Search companies", 1, "form-control", 3, "ngModelChange", "ngModel"], ["for", "statusFilter", 1, "sr-only"], ["id", "statusFilter", "aria-label", "Filter by status", 1, "form-control", "filter-select", 3, "ngModelChange", "ngModel"], ["value", "ALL"], ["value", "ACTIVE"], ["value", "INACTIVE"], ["value", "PENDING"], ["for", "typeFilter", 1, "sr-only"], ["id", "typeFilter", "aria-label", "Filter by type", 1, "form-control", "filter-select", 3, "ngModelChange", "ngModel"], ["value", "SHIPPER"], ["value", "CARRIER"], ["value", "BOTH"], [1, "btn", "btn-ghost", "btn-sm"], [1, "data-table-wrapper"], ["aria-label", "Companies list", 1, "data-table"], ["scope", "col", 1, "sortable", 3, "click"], ["aria-hidden", "true", 1, "sort-indicator", "material-icons"], ["scope", "col"], ["scope", "col", 1, "actions"], [3, "selected"], [1, "pagination-bar"], [1, "pagination-controls"], ["aria-label", "Previous page", 1, "page-btn", 3, "click", "disabled"], [1, "material-icons", 2, "font-size", "18px"], [1, "page-btn", 3, "active"], ["aria-label", "Next page", 1, "page-btn", 3, "click", "disabled"], ["role", "dialog", "aria-modal", "true", 1, "modal-overlay"], ["role", "alertdialog", "aria-modal", "true", "aria-label", "Confirm deletion", 1, "modal-overlay"], [1, "btn", "btn-ghost", "btn-sm", 3, "click"], ["colspan", "7"], [1, "table-empty-state"], [3, "click"], [1, "company-name-cell"], [1, "company-initial"], [1, "name-primary"], [1, "name-secondary", "mono"], [1, "badge", "badge-neutral"], [1, "badge"], [1, "site-count-link", 3, "routerLink", "queryParams"], [1, "mono"], [1, "actions"], ["title", "Edit", 1, "btn", "btn-icon", 3, "click"], ["title", "View sites", 1, "btn", "btn-icon", 3, "click", "routerLink", "queryParams"], ["title", "Calculate barycenter", 1, "btn", "btn-icon", 3, "click", "routerLink", "queryParams"], ["title", "Delete", 1, "btn", "btn-icon", 2, "color", "var(--color-danger-600)", 3, "click"], [1, "muted"], [2, "font-size", "var(--font-size-small)"], [2, "font-size", "11px", "color", "var(--color-neutral-500)"], [1, "page-btn", 3, "click"], ["role", "dialog", "aria-modal", "true", 1, "modal-overlay", 3, "click"], [1, "modal", 3, "click"], [1, "modal-header"], ["aria-label", "Close dialog", 1, "btn", "btn-icon", 3, "click"], ["novalidate", "", 3, "ngSubmit", "formGroup"], [1, "modal-body"], [1, "grid-2"], [1, "form-group"], ["for", "cName", 1, "form-label"], ["id", "cName", "formControlName", "name", "autocomplete", "organization", 1, "form-control"], ["role", "alert", 1, "form-error"], ["for", "cType", 1, "form-label"], ["id", "cType", "formControlName", "type", 1, "form-control"], [1, "grid-2", 2, "margin-top", "var(--space-4)"], ["for", "cTaxId", 1, "form-label"], ["id", "cTaxId", "formControlName", "taxId", "autocomplete", "off", 1, "form-control"], [1, "divider"], [1, "section-title"], ["for", "cContact", 1, "form-label"], ["id", "cContact", "formControlName", "contactName", "autocomplete", "name", 1, "form-control"], ["for", "cEmail", 1, "form-label"], ["id", "cEmail", "type", "email", "formControlName", "contactEmail", "autocomplete", "email", 1, "form-control"], [1, "form-group", 2, "margin-top", "var(--space-4)"], ["for", "cPhone", 1, "form-label"], ["id", "cPhone", "formControlName", "contactPhone", "autocomplete", "tel", 1, "form-control", 2, "max-width", "280px"], ["for", "cNotes", 1, "form-label"], ["id", "cNotes", "formControlName", "notes", "rows", "3", "placeholder", "Optional notes\u2026", 1, "form-control"], [1, "modal-footer"], ["type", "button", 1, "btn", "btn-secondary", 3, "click"], ["type", "submit", 1, "btn", "btn-primary", 3, "disabled"], ["for", "cStatus", 1, "form-label"], ["id", "cStatus", "formControlName", "status", 1, "form-control"], [1, "modal", "modal-sm"], ["aria-label", "Cancel", 1, "btn", "btn-icon", 3, "click"], [2, "margin-top", "var(--space-3)", "color", "var(--color-danger-600)", "font-size", "var(--font-size-small)"], [1, "material-icons", 2, "font-size", "14px", "vertical-align", "middle"], [1, "btn", "btn-secondary", 3, "click"], [1, "btn", "btn-danger", 3, "click"]], template: function CompaniesComponent_Template(rf, ctx) {
      if (rf & 1) {
        \u0275\u0275elementStart(0, "div", 0)(1, "div", 1)(2, "div")(3, "h1", 2);
        \u0275\u0275text(4, "Companies");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(5, "p", 3);
        \u0275\u0275text(6);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(7, "div", 4)(8, "button", 5);
        \u0275\u0275listener("click", function CompaniesComponent_Template_button_click_8_listener() {
          return ctx.openCreateModal();
        });
        \u0275\u0275elementStart(9, "span", 6);
        \u0275\u0275text(10, "add");
        \u0275\u0275elementEnd();
        \u0275\u0275text(11, " New Company ");
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(12, "div", 7)(13, "div", 8)(14, "div", 9)(15, "div", 10)(16, "span", 11);
        \u0275\u0275text(17, "search");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(18, "input", 12);
        \u0275\u0275twoWayListener("ngModelChange", function CompaniesComponent_Template_input_ngModelChange_18_listener($event) {
          \u0275\u0275twoWayBindingSet(ctx.filter.search, $event) || (ctx.filter.search = $event);
          return $event;
        });
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(19, "label", 13);
        \u0275\u0275text(20, "Filter by status");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(21, "select", 14);
        \u0275\u0275twoWayListener("ngModelChange", function CompaniesComponent_Template_select_ngModelChange_21_listener($event) {
          \u0275\u0275twoWayBindingSet(ctx.filter.status, $event) || (ctx.filter.status = $event);
          return $event;
        });
        \u0275\u0275elementStart(22, "option", 15);
        \u0275\u0275text(23, "All Statuses");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(24, "option", 16);
        \u0275\u0275text(25, "Active");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(26, "option", 17);
        \u0275\u0275text(27, "Inactive");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(28, "option", 18);
        \u0275\u0275text(29, "Pending");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(30, "label", 19);
        \u0275\u0275text(31, "Filter by type");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(32, "select", 20);
        \u0275\u0275twoWayListener("ngModelChange", function CompaniesComponent_Template_select_ngModelChange_32_listener($event) {
          \u0275\u0275twoWayBindingSet(ctx.filter.type, $event) || (ctx.filter.type = $event);
          return $event;
        });
        \u0275\u0275elementStart(33, "option", 15);
        \u0275\u0275text(34, "All Types");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(35, "option", 21);
        \u0275\u0275text(36, "Shipper");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(37, "option", 22);
        \u0275\u0275text(38, "Carrier");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(39, "option", 23);
        \u0275\u0275text(40, "Both");
        \u0275\u0275elementEnd()();
        \u0275\u0275template(41, CompaniesComponent_Conditional_41_Template, 4, 0, "button", 24);
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(42, "div", 25)(43, "table", 26)(44, "thead")(45, "tr")(46, "th", 27);
        \u0275\u0275listener("click", function CompaniesComponent_Template_th_click_46_listener() {
          return ctx.sort("name");
        });
        \u0275\u0275text(47, " Company ");
        \u0275\u0275elementStart(48, "span", 28);
        \u0275\u0275text(49, "unfold_more");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(50, "th", 29);
        \u0275\u0275text(51, "Type");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(52, "th", 29);
        \u0275\u0275text(53, "Status");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(54, "th", 27);
        \u0275\u0275listener("click", function CompaniesComponent_Template_th_click_54_listener() {
          return ctx.sort("consumptionSiteCount");
        });
        \u0275\u0275text(55, " Sites ");
        \u0275\u0275elementStart(56, "span", 28);
        \u0275\u0275text(57, "unfold_more");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(58, "th", 27);
        \u0275\u0275listener("click", function CompaniesComponent_Template_th_click_58_listener() {
          return ctx.sort("totalTrafficTons");
        });
        \u0275\u0275text(59, " Traffic ");
        \u0275\u0275elementStart(60, "span", 28);
        \u0275\u0275text(61, "unfold_more");
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(62, "th", 29);
        \u0275\u0275text(63, "Contact");
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(64, "th", 30);
        \u0275\u0275text(65, "Actions");
        \u0275\u0275elementEnd()()();
        \u0275\u0275elementStart(66, "tbody");
        \u0275\u0275template(67, CompaniesComponent_Conditional_67_Template, 7, 0, "tr");
        \u0275\u0275repeaterCreate(68, CompaniesComponent_For_69_Template, 37, 28, "tr", 31, _forTrack0);
        \u0275\u0275elementEnd()();
        \u0275\u0275elementStart(70, "div", 32)(71, "span");
        \u0275\u0275text(72);
        \u0275\u0275elementEnd();
        \u0275\u0275elementStart(73, "div", 33)(74, "button", 34);
        \u0275\u0275listener("click", function CompaniesComponent_Template_button_click_74_listener() {
          return ctx.prevPage();
        });
        \u0275\u0275elementStart(75, "span", 35);
        \u0275\u0275text(76, "chevron_left");
        \u0275\u0275elementEnd()();
        \u0275\u0275repeaterCreate(77, CompaniesComponent_For_78_Template, 2, 5, "button", 36, \u0275\u0275repeaterTrackByIdentity);
        \u0275\u0275elementStart(79, "button", 37);
        \u0275\u0275listener("click", function CompaniesComponent_Template_button_click_79_listener() {
          return ctx.nextPage();
        });
        \u0275\u0275elementStart(80, "span", 35);
        \u0275\u0275text(81, "chevron_right");
        \u0275\u0275elementEnd()()()()()();
        \u0275\u0275template(82, CompaniesComponent_Conditional_82_Template, 60, 13, "div", 38)(83, CompaniesComponent_Conditional_83_Template, 25, 2, "div", 39);
      }
      if (rf & 2) {
        \u0275\u0275advance(6);
        \u0275\u0275textInterpolate2("", ctx.filteredCompanies().length, " of ", ctx.dataService.companies().length, " companies");
        \u0275\u0275advance(12);
        \u0275\u0275twoWayProperty("ngModel", ctx.filter.search);
        \u0275\u0275advance(3);
        \u0275\u0275twoWayProperty("ngModel", ctx.filter.status);
        \u0275\u0275advance(11);
        \u0275\u0275twoWayProperty("ngModel", ctx.filter.type);
        \u0275\u0275advance(9);
        \u0275\u0275conditional(41, ctx.filter.search || ctx.filter.status !== "ALL" || ctx.filter.type !== "ALL" ? 41 : -1);
        \u0275\u0275advance(5);
        \u0275\u0275classProp("sort-asc", ctx.sortCol === "name" && ctx.sortDir === "asc")("sort-desc", ctx.sortCol === "name" && ctx.sortDir === "desc");
        \u0275\u0275advance(8);
        \u0275\u0275classProp("sort-asc", ctx.sortCol === "consumptionSiteCount" && ctx.sortDir === "asc")("sort-desc", ctx.sortCol === "consumptionSiteCount" && ctx.sortDir === "desc");
        \u0275\u0275advance(4);
        \u0275\u0275classProp("sort-asc", ctx.sortCol === "totalTrafficTons" && ctx.sortDir === "asc")("sort-desc", ctx.sortCol === "totalTrafficTons" && ctx.sortDir === "desc");
        \u0275\u0275advance(9);
        \u0275\u0275conditional(67, ctx.filteredCompanies().length === 0 ? 67 : -1);
        \u0275\u0275advance();
        \u0275\u0275repeater(ctx.pagedCompanies());
        \u0275\u0275advance(4);
        \u0275\u0275textInterpolate3("Showing ", ctx.pageStart(), "\u2013", ctx.pageEnd(), " of ", ctx.filteredCompanies().length, " companies");
        \u0275\u0275advance(2);
        \u0275\u0275property("disabled", ctx.page() === 1);
        \u0275\u0275advance(3);
        \u0275\u0275repeater(ctx.pageNumbers());
        \u0275\u0275advance(2);
        \u0275\u0275property("disabled", ctx.page() === ctx.totalPages());
        \u0275\u0275advance(3);
        \u0275\u0275conditional(82, ctx.showModal() ? 82 : -1);
        \u0275\u0275advance();
        \u0275\u0275conditional(83, ctx.deleteTarget() ? 83 : -1);
      }
    }, dependencies: [CommonModule, RouterLink, FormsModule, \u0275NgNoValidate, NgSelectOption, \u0275NgSelectMultipleOption, DefaultValueAccessor, SelectControlValueAccessor, NgControlStatus, NgControlStatusGroup, NgModel, ReactiveFormsModule, FormGroupDirective, FormControlName], styles: ["\n\n.filters-bar[_ngcontent-%COMP%]   .card-body[_ngcontent-%COMP%] {\n  padding: var(--space-3) var(--space-5) !important;\n}\n.filter-row[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-3);\n  flex-wrap: wrap;\n}\n.search-field[_ngcontent-%COMP%] {\n  position: relative;\n  flex: 1;\n  min-width: 200px;\n  .form-control {\n    padding-left: 36px;\n  }\n}\n.search-icon-sm[_ngcontent-%COMP%] {\n  position: absolute;\n  left: 10px;\n  top: 50%;\n  transform: translateY(-50%);\n  font-size: 18px;\n  color: var(--color-neutral-500);\n  pointer-events: none;\n  z-index: 1;\n}\n.filter-select[_ngcontent-%COMP%] {\n  max-width: 160px;\n}\n.company-name-cell[_ngcontent-%COMP%] {\n  display: flex;\n  align-items: center;\n  gap: var(--space-3);\n}\n.company-initial[_ngcontent-%COMP%] {\n  width: 34px;\n  height: 34px;\n  border-radius: var(--radius-full);\n  color: #fff;\n  font-weight: 700;\n  font-size: 15px;\n  display: flex;\n  align-items: center;\n  justify-content: center;\n  flex-shrink: 0;\n}\n.name-primary[_ngcontent-%COMP%] {\n  font-weight: 500;\n}\n.name-secondary[_ngcontent-%COMP%] {\n  font-size: 11px;\n  color: var(--color-neutral-500);\n  margin-top: 1px;\n}\n.site-count-link[_ngcontent-%COMP%] {\n  color: var(--color-primary-600);\n  font-weight: 600;\n  text-decoration: none;\n  &:hover {\n    text-decoration: underline;\n  }\n}\n.muted[_ngcontent-%COMP%] {\n  color: var(--color-neutral-400);\n}\n/*# sourceMappingURL=companies.component.css.map */"] });
  }
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && \u0275setClassDebugInfo(CompaniesComponent, { className: "CompaniesComponent", filePath: "src\\app\\features\\companies\\companies.component.ts", lineNumber: 374 });
})();
export {
  CompaniesComponent
};
//# sourceMappingURL=chunk-TUMOS65T.js.map
