import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';
import { TopbarComponent } from './shared/components/topbar/topbar.component';
import { ToastContainerComponent } from './shared/components/toast/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, TopbarComponent, ToastContainerComponent],
  template: `
    <div class="app-shell">
      <app-sidebar />
      <div class="app-main">
        <app-topbar />
        <div class="app-content" id="main-content" role="main">
          <router-outlet />
        </div>
      </div>
    </div>
    <app-toast-container />

    <!-- Skip-to-content for keyboard users -->
    <a href="#main-content" class="skip-link">Skip to main content</a>
  `,
  styles: [`
    .app-shell {
      display: flex;
      height: 100vh;
      overflow: hidden;
      background-color: var(--color-neutral-100);
    }

    .app-main {
      flex: 1;
      display: flex;
      flex-direction: column;
      min-width: 0;
      overflow: hidden;
    }

    .app-content {
      flex: 1;
      overflow-y: auto;
      overflow-x: hidden;
      min-height: 0;
    }

    /* Skip link (screen readers + keyboard) */
    .skip-link {
      position: fixed;
      top: -100px;
      left: var(--space-4);
      z-index: 9999;
      background-color: var(--color-primary-700);
      color: #fff;
      padding: var(--space-3) var(--space-5);
      border-radius: 0 0 var(--radius-2) var(--radius-2);
      font-weight: 600;
      text-decoration: none;
      transition: top 100ms ease-out;

      &:focus { top: 0; }
    }
  `]
})
export class AppComponent {
  title = 'logistics-barycenter-spa';
}
