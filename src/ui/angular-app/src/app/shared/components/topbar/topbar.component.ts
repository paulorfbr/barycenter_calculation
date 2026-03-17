import { Component, Input, signal, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

interface Breadcrumb {
  label: string;
  route?: string;
}

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <header class="topbar" role="banner">
      <!-- Breadcrumb -->
      <nav class="breadcrumb" aria-label="Breadcrumb">
        @for (crumb of breadcrumbs(); track crumb.label; let last = $last) {
          @if (crumb.route && !last) {
            <a [routerLink]="crumb.route" class="crumb-link">{{ crumb.label }}</a>
            <span class="crumb-sep material-icons" aria-hidden="true">chevron_right</span>
          } @else {
            <span class="crumb-current" [attr.aria-current]="last ? 'page' : null">{{ crumb.label }}</span>
          }
        }
      </nav>

      <!-- Global search -->
      <div class="search-wrapper" role="search">
        <span class="search-icon material-icons" aria-hidden="true">search</span>
        <input
          class="search-input"
          type="search"
          placeholder="Search sites, companies…"
          [(ngModel)]="searchQuery"
          (keydown.enter)="onSearch()"
          aria-label="Global search"
        />
        @if (searchQuery.length > 0) {
          <button class="search-clear btn-icon" (click)="clearSearch()" aria-label="Clear search">
            <span class="material-icons">close</span>
          </button>
        }
      </div>

      <!-- Right actions -->
      <div class="topbar-actions">
        <button class="btn btn-icon" aria-label="Notifications" title="Notifications">
          <span class="material-icons">notifications_none</span>
          <span class="notif-dot" aria-hidden="true"></span>
        </button>

        <div class="divider-v" aria-hidden="true"></div>

        <!-- User avatar -->
        <button class="user-avatar" aria-label="User menu" aria-haspopup="true">
          <span class="avatar-initials">AD</span>
          <span class="user-name">Admin</span>
          <span class="material-icons avatar-chevron">expand_more</span>
        </button>
      </div>
    </header>
  `,
  styles: [`
    .topbar {
      height: var(--topbar-height);
      background-color: var(--color-neutral-000);
      border-bottom: 1px solid var(--color-neutral-300);
      display: flex;
      align-items: center;
      gap: var(--space-4);
      padding: 0 var(--space-6);
      flex-shrink: 0;
      box-shadow: var(--shadow-1);
      z-index: 5;
    }

    /* Breadcrumb */
    .breadcrumb {
      display: flex;
      align-items: center;
      gap: 2px;
      flex-shrink: 0;
    }

    .crumb-link {
      font-size: var(--font-size-small);
      color: var(--color-primary-600);
      text-decoration: none;

      &:hover { text-decoration: underline; }
    }

    .crumb-sep {
      font-size: 16px;
      color: var(--color-neutral-500);
    }

    .crumb-current {
      font-size: var(--font-size-small);
      font-weight: 600;
      color: var(--color-neutral-900);
    }

    /* Search */
    .search-wrapper {
      flex: 1;
      max-width: 420px;
      position: relative;
      display: flex;
      align-items: center;
    }

    .search-icon {
      position: absolute;
      left: 10px;
      font-size: 18px;
      color: var(--color-neutral-500);
      pointer-events: none;
    }

    .search-input {
      width: 100%;
      height: 36px;
      padding: 0 36px 0 36px;
      background-color: var(--color-neutral-100);
      border: 1.5px solid transparent;
      border-radius: var(--radius-full);
      font-family: var(--font-family-base);
      font-size: var(--font-size-small);
      color: var(--color-neutral-900);
      transition: border-color var(--anim-button-hover) ease-out,
                  background-color var(--anim-button-hover) ease-out;

      &::placeholder { color: var(--color-neutral-500); }

      &:focus {
        outline: none;
        border-color: var(--color-primary-600);
        background-color: var(--color-neutral-000);
      }
    }

    .search-clear {
      position: absolute;
      right: 4px;
      width: 28px;
      height: 28px;
      min-height: 28px;
      .material-icons { font-size: 16px; }
    }

    /* Actions */
    .topbar-actions {
      display: flex;
      align-items: center;
      gap: var(--space-2);
      margin-left: auto;
      flex-shrink: 0;
    }

    .btn.btn-icon {
      position: relative;
      color: var(--color-neutral-700);
    }

    .notif-dot {
      position: absolute;
      top: 6px;
      right: 6px;
      width: 8px;
      height: 8px;
      background-color: var(--color-danger-600);
      border-radius: 50%;
      border: 2px solid var(--color-neutral-000);
    }

    .divider-v {
      width: 1px;
      height: 24px;
      background-color: var(--color-neutral-300);
      margin: 0 var(--space-1);
    }

    .user-avatar {
      display: flex;
      align-items: center;
      gap: var(--space-2);
      padding: 4px 8px 4px 4px;
      background: transparent;
      border: none;
      border-radius: var(--radius-full);
      cursor: pointer;
      transition: background-color var(--anim-button-hover) ease-out;

      &:hover { background-color: var(--color-neutral-100); }
      &:focus-visible {
        outline: 2px solid var(--color-primary-600);
        outline-offset: 2px;
      }
    }

    .avatar-initials {
      width: 32px;
      height: 32px;
      border-radius: var(--radius-full);
      background-color: var(--color-primary-700);
      color: #fff;
      font-size: 12px;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .user-name {
      font-size: var(--font-size-small);
      font-weight: 600;
      color: var(--color-neutral-900);
    }

    .avatar-chevron {
      font-size: 18px;
      color: var(--color-neutral-500);
    }
  `]
})
export class TopbarComponent {
  private router = inject(Router);

  searchQuery = '';
  breadcrumbs = signal<Breadcrumb[]>([{ label: 'Dashboard', route: '/dashboard' }]);

  private readonly routeLabels: Record<string, string> = {
    '/dashboard':  'Dashboard',
    '/companies':  'Companies',
    '/sites':      'Sites',
    '/barycenter': 'Barycenter',
    '/map':        'Map View',
  };

  constructor() {
    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd)
    ).subscribe((e) => {
      const nav = e as NavigationEnd;
      const url = nav.urlAfterRedirects.split('?')[0];
      const segments = url.split('/').filter(Boolean);
      const crumbs: Breadcrumb[] = [{ label: 'Home', route: '/dashboard' }];
      let path = '';
      for (const seg of segments) {
        path += '/' + seg;
        crumbs.push({ label: this.routeLabels[path] ?? this.capitalize(seg), route: path });
      }
      this.breadcrumbs.set(crumbs);
    });
  }

  onSearch(): void {
    if (this.searchQuery.trim()) {
      this.router.navigate(['/sites'], { queryParams: { search: this.searchQuery.trim() } });
    }
  }

  clearSearch(): void {
    this.searchQuery = '';
  }

  private capitalize(s: string): string {
    return s.charAt(0).toUpperCase() + s.slice(1);
  }
}
