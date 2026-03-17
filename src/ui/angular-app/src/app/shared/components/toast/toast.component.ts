import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast.service';

const TOAST_ICONS: Record<string, string> = {
  success: 'check_circle',
  warning: 'warning',
  danger:  'error',
  info:    'info',
};

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container" role="region" aria-label="Notifications" aria-live="polite">
      @for (toast of toastService.toasts(); track toast.id) {
        <div
          class="toast"
          [class]="'toast toast-' + toast.type"
          role="alert"
          [attr.aria-live]="toast.type === 'danger' ? 'assertive' : 'polite'"
        >
          <span class="material-icons toast-icon" aria-hidden="true">{{ getIcon(toast.type) }}</span>
          <div class="toast-content">
            <p class="toast-title">{{ toast.title }}</p>
            @if (toast.message) {
              <p class="toast-message">{{ toast.message }}</p>
            }
          </div>
          <button
            class="toast-close"
            (click)="toastService.dismiss(toast.id)"
            [attr.aria-label]="'Dismiss: ' + toast.title"
          >
            <span class="material-icons">close</span>
          </button>
        </div>
      }
    </div>
  `
})
export class ToastContainerComponent {
  toastService = inject(ToastService);

  getIcon(type: string): string {
    return TOAST_ICONS[type] ?? 'info';
  }
}