import { Component, signal, computed, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  badge?: number;
}

interface NavSection {
  label: string;
  items: NavItem[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar" [class.collapsed]="collapsed()" role="navigation" aria-label="Main navigation">
      <!-- Logo area -->
      <div class="sidebar-logo">
        <div class="logo-mark" aria-hidden="true">
          <span class="material-icons">hub</span>
        </div>
        @if (!collapsed()) {
          <div class="logo-text">
            <span class="logo-name">LogistiX</span>
            <span class="logo-tagline">Barycenter Suite</span>
          </div>
        }
      </div>

      <!-- Navigation sections -->
      <nav class="sidebar-nav">
        @for (section of navSections; track section.label) {
          @if (!collapsed()) {
            <p class="nav-section-label">{{ section.label }}</p>
          }
          @for (item of section.items; track item.route) {
            <a
              class="nav-item"
              [routerLink]="item.route"
              routerLinkActive="active"
              [title]="collapsed() ? item.label : ''"
              [attr.aria-label]="item.label"
            >
              <span class="material-icons nav-icon" aria-hidden="true">{{ item.icon }}</span>
              @if (!collapsed()) {
                <span class="nav-label">{{ item.label }}</span>
                @if (item.badge) {
                  <span class="nav-badge" [attr.aria-label]="item.badge + ' items'">{{ item.badge }}</span>
                }
              }
            </a>
          }
        }
      </nav>

      <!-- Collapse toggle -->
      <button
        class="sidebar-toggle btn-icon"
        (click)="toggleCollapse()"
        [attr.aria-label]="collapsed() ? 'Expand sidebar' : 'Collapse sidebar'"
        [title]="collapsed() ? 'Expand sidebar' : 'Collapse sidebar'"
      >
        <span class="material-icons">
          {{ collapsed() ? 'chevron_right' : 'chevron_left' }}
        </span>
      </button>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: var(--sidebar-width-expanded);
      min-width: var(--sidebar-width-expanded);
      height: 100%;
      background-color: var(--color-primary-900);
      display: flex;
      flex-direction: column;
      transition: width var(--anim-sidebar-toggle) ease-in-out,
                  min-width var(--anim-sidebar-toggle) ease-in-out;
      overflow: hidden;
      position: relative;
      z-index: 10;
    }

    .sidebar.collapsed {
      width: var(--sidebar-width-collapsed);
      min-width: var(--sidebar-width-collapsed);
    }

    /* Logo */
    .sidebar-logo {
      height: 64px;
      display: flex;
      align-items: center;
      gap: var(--space-3);
      padding: 0 var(--space-4);
      border-bottom: 1px solid rgba(255,255,255,0.08);
      flex-shrink: 0;
    }

    .logo-mark {
      width: 36px;
      height: 36px;
      border-radius: var(--radius-2);
      background-color: var(--color-primary-600);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      .material-icons {
        color: #fff;
        font-size: 20px;
      }
    }

    .logo-text {
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    .logo-name {
      font-size: var(--font-size-h3);
      font-weight: 700;
      color: #fff;
      white-space: nowrap;
    }

    .logo-tagline {
      font-size: 10px;
      color: var(--color-primary-400);
      white-space: nowrap;
      letter-spacing: 0.05em;
    }

    /* Navigation */
    .sidebar-nav {
      flex: 1;
      overflow-y: auto;
      overflow-x: hidden;
      padding: var(--space-4) 0;

      &::-webkit-scrollbar { width: 4px; }
      &::-webkit-scrollbar-track { background: transparent; }
      &::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.15); border-radius: 2px; }
    }

    .nav-section-label {
      font-size: var(--font-size-label);
      font-weight: 600;
      color: var(--color-neutral-500);
      text-transform: uppercase;
      letter-spacing: 0.06em;
      padding: var(--space-2) var(--space-4) var(--space-1);
      margin-top: var(--space-3);
      white-space: nowrap;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: var(--space-3);
      height: 48px;
      padding: 0 var(--space-4);
      color: rgba(255,255,255,0.72);
      text-decoration: none;
      transition: background-color 100ms ease-out, color 100ms ease-out;
      cursor: pointer;
      position: relative;
      white-space: nowrap;

      &:hover {
        background-color: rgba(255,255,255,0.08);
        color: #fff;
      }

      &.active {
        background-color: var(--color-primary-800);
        color: #fff;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 8px;
          bottom: 8px;
          width: 3px;
          background-color: var(--color-primary-400);
          border-radius: 0 2px 2px 0;
        }
      }

      &:focus-visible {
        outline: 2px solid var(--color-primary-400);
        outline-offset: -2px;
      }
    }

    .nav-icon {
      font-size: 22px;
      flex-shrink: 0;
    }

    .nav-label {
      font-size: var(--font-size-body);
      font-weight: 500;
      flex: 1;
    }

    .nav-badge {
      background-color: var(--color-primary-600);
      color: #fff;
      font-size: 10px;
      font-weight: 700;
      padding: 2px 6px;
      border-radius: var(--radius-full);
      min-width: 18px;
      text-align: center;
    }

    /* Toggle button */
    .sidebar-toggle {
      height: 48px;
      width: 100%;
      border-radius: 0;
      color: rgba(255,255,255,0.5);
      border-top: 1px solid rgba(255,255,255,0.08);
      flex-shrink: 0;

      &:hover { background-color: rgba(255,255,255,0.08); color: #fff; }
      &:focus-visible { outline: 2px solid var(--color-primary-400); outline-offset: -2px; }

      .material-icons { font-size: 22px; }
    }
  `]
})
export class SidebarComponent {
  collapsed = signal(false);

  navSections: NavSection[] = [
    {
      label: 'Overview',
      items: [
        { label: 'Dashboard',  icon: 'dashboard',        route: '/dashboard' },
      ]
    },
    {
      label: 'Management',
      items: [
        { label: 'Companies',  icon: 'domain',           route: '/companies' },
        { label: 'Sites',      icon: 'location_on',      route: '/sites' },
      ]
    },
    {
      label: 'Analysis',
      items: [
        { label: 'Barycenter', icon: 'my_location',      route: '/barycenter' },
        { label: 'Map View',   icon: 'map',              route: '/map' },
      ]
    },
  ];

  toggleCollapse(): void {
    this.collapsed.update(v => !v);
  }

  @HostListener('window:keydown', ['$event'])
  onKeydown(event: KeyboardEvent): void {
    if (event.ctrlKey && event.key === 'b') {
      event.preventDefault();
      this.toggleCollapse();
    }
  }
}
