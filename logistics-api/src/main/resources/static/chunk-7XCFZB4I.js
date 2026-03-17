import {
  signal,
  ɵɵdefineInjectable
} from "./chunk-WAAQAFSM.js";

// src/app/core/services/toast.service.ts
var ToastService = class _ToastService {
  constructor() {
    this._toasts = signal([]);
    this.toasts = this._toasts.asReadonly();
  }
  show(type, title, message, duration = 4e3) {
    const id = `toast-${Date.now()}-${Math.random().toString(36).slice(2)}`;
    const toast = { id, type, title, message, duration };
    this._toasts.update((list) => [...list, toast]);
    if (duration > 0) {
      setTimeout(() => this.dismiss(id), duration);
    }
  }
  success(title, message) {
    this.show("success", title, message);
  }
  warning(title, message) {
    this.show("warning", title, message);
  }
  error(title, message) {
    this.show("danger", title, message);
  }
  info(title, message) {
    this.show("info", title, message);
  }
  dismiss(id) {
    this._toasts.update((list) => list.filter((t) => t.id !== id));
  }
  static {
    this.\u0275fac = function ToastService_Factory(t) {
      return new (t || _ToastService)();
    };
  }
  static {
    this.\u0275prov = /* @__PURE__ */ \u0275\u0275defineInjectable({ token: _ToastService, factory: _ToastService.\u0275fac, providedIn: "root" });
  }
};

export {
  ToastService
};
//# sourceMappingURL=chunk-7XCFZB4I.js.map
