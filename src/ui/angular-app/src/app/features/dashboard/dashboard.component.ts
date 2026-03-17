import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DataService } from '../../core/services/data.service';
import { KpiCardComponent } from '../../shared/components/kpi-card/kpi-card.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, KpiCardComponent],
  template: `
    <div class="page-content page-enter">
      <!-- Page header -->
      <div class="page-header">
        <div>
          <h1 class="page-title">Dashboard</h1>
          <p class="page-subtitle">Logistics barycenter overview — {{ today }}</p>
        </div>
        <div class="page-actions">
          <a routerLink="/barycenter" class="btn btn-primary">
            <span class="material-icons">my_location</span>
            Calculate Barycenter
          </a>
        </div>
      </div>

      <!-- KPI cards -->
      <div class="grid-4" role="list" aria-label="Key performance indicators">
        <div role="listitem">
          <app-kpi-card
            label="Total Companies"
            [value]="summary().totalCompanies"
            icon="domain"
            [trend]="summary().companiesTrend"
            [trendPositive]="true"
            iconBg="var(--color-primary-050)"
            iconColor="var(--color-primary-700)"
          />
        </div>
        <div role="listitem">
          <app-kpi-card
            label="Active Shipments"
            [value]="summary().activeShipments"
            icon="local_shipping"
            [trend]="summary().shipmentsTrend"
            [trendPositive]="true"
            iconBg="var(--color-info-100)"
            iconColor="var(--color-info-600)"
          />
        </div>
        <div role="listitem">
          <app-kpi-card
            label="Consumption Sites"
            [value]="summary().totalConsumptionSites"
            icon="location_on"
            [trend]="summary().locationsTrend"
            [trendPositive]="true"
            iconBg="var(--color-warning-100)"
            iconColor="var(--color-warning-600)"
          />
        </div>
        <div role="listitem">
          <app-kpi-card
            label="On-Time Rate"
            [value]="summary().onTimeRatePercent + '%'"
            icon="verified"
            [trend]="summary().onTimeTrend"
            [trendPositive]="true"
            iconBg="var(--color-success-100)"
            iconColor="var(--color-success-600)"
          />
        </div>
      </div>

      <!-- Barycenter summary + activity row -->
      <div class="dashboard-grid">
        <!-- Barycenter summary card -->
        <div class="card barycenter-summary">
          <div class="card-header">
            <div>
              <h2 class="card-title">Barycenter Analysis</h2>
              <p class="card-subtitle">Weighted logistics center optimization</p>
            </div>
            <a routerLink="/barycenter" class="btn btn-secondary btn-sm">
              <span class="material-icons">open_in_new</span>
              Open
            </a>
          </div>
          <div class="card-body">
            <div class="bary-stats">
              <div class="bary-stat">
                <span class="stat-value">{{ summary().totalConsumptionSites }}</span>
                <span class="stat-label">Input Sites</span>
              </div>
              <div class="bary-stat-divider" aria-hidden="true"></div>
              <div class="bary-stat">
                <span class="stat-value">{{ totalTonsFormatted() }}</span>
                <span class="stat-label">Total Traffic</span>
              </div>
              <div class="bary-stat-divider" aria-hidden="true"></div>
              <div class="bary-stat">
                <span class="stat-value">{{ summary().logisticsCenterCandidates }}</span>
                <span class="stat-label">Candidates</span>
              </div>
            </div>

            <div class="algorithm-info" style="margin-top: var(--space-5);">
              <strong>Weiszfeld Algorithm:</strong> minimises the weighted sum of geodesic (Haversine)
              distances to all consumption sites — the geometric median, optimal for logistics cost.
              <strong>Simple Barycenter:</strong> closed-form weighted centroid, instant convergence.
            </div>

            <div class="company-site-list" style="margin-top: var(--space-5);">
              <p class="section-title" style="margin-bottom: var(--space-3);">Companies with Sites</p>
              @for (company of companiesWithSites(); track company.id) {
                <div class="company-site-row">
                  <div class="company-site-info">
                    <span class="company-site-name">{{ company.name }}</span>
                    <span class="badge badge-primary">{{ company.consumptionSiteCount }} sites</span>
                  </div>
                  <div class="weight-bar-container" style="flex:1; max-width: 200px;">
                    <div class="weight-bar-track">
                      <div
                        class="weight-bar-fill"
                        [style.width.%]="(company.totalTrafficTons / maxTons()) * 100"
                        [attr.aria-label]="company.totalTrafficTons + ' tons'"
                      ></div>
                    </div>
                    <span class="weight-value mono">{{ formatTons(company.totalTrafficTons) }}</span>
                  </div>
                </div>
              }
              @if (companiesWithSites().length === 0) {
                <p class="empty-hint">No companies have consumption sites yet.
                  <a routerLink="/sites">Add sites</a> to begin.
                </p>
              }
            </div>
          </div>
        </div>

        <!-- Right column -->
        <div class="dashboard-right">
          <!-- Recent activity -->
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Recent Activity</h2>
            </div>
            <div class="card-body activity-feed" role="feed" aria-label="Recent activity">
              @for (item of summary().recentActivity; track item.timeLabel + item.message) {
                <div class="activity-item" role="article">
                  <span class="activity-dot" aria-hidden="true"></span>
                  <div class="activity-content">
                    <p class="activity-message">{{ item.message }}</p>
                    <time class="activity-time">{{ item.timeLabel }}</time>
                  </div>
                </div>
              }
            </div>
          </div>

          <!-- Latest results -->
          <div class="card">
            <div class="card-header">
              <h2 class="card-title">Latest Results</h2>
              <a routerLink="/barycenter" class="btn btn-ghost btn-sm">View all</a>
            </div>
            <div class="card-body" style="padding: 0;">
              @if (latestResults().length > 0) {
                <table class="data-table" aria-label="Latest barycenter results">
                  <thead>
                    <tr>
                      <th scope="col">Company</th>
                      <th scope="col">Coordinate</th>
                      <th scope="col">Algorithm</th>
                      <th scope="col">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (r of latestResults(); track r.logisticsCenterId) {
                      <tr>
                        <td>{{ getCompanyName(r.companyId) }}</td>
                        <td class="mono">{{ r.formattedCoordinate }}</td>
                        <td>
                          <span class="badge badge-neutral" style="font-size:10px;">
                            {{ r.algorithmDescription === 'weiszfeld-iterative' ? 'Weiszfeld' : 'Simple' }}
                          </span>
                        </td>
                        <td>
                          <span class="badge" [class]="statusBadgeClass(r.status)">{{ r.status }}</span>
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              } @else {
                <div class="table-empty-state">
                  <span class="material-icons">my_location</span>
                  <p>No barycenter results yet.</p>
                  <a routerLink="/barycenter" class="btn btn-primary btn-sm" style="margin-top: var(--space-3);">
                    Run Calculation
                  </a>
                </div>
              }
            </div>
          </div>
        </div>
      </div>

      <!-- Overdue shipments -->
      @if (summary().overdueShipments.length > 0) {
        <div class="card">
          <div class="card-header">
            <div>
              <h2 class="card-title">Overdue Shipments</h2>
              <p class="card-subtitle">Require immediate attention</p>
            </div>
          </div>
          <div style="overflow-x: auto;">
            <table class="data-table" aria-label="Overdue shipments">
              <thead>
                <tr>
                  <th scope="col">Shipment ID</th>
                  <th scope="col">Company</th>
                  <th scope="col">Origin</th>
                  <th scope="col">Destination</th>
                  <th scope="col">Overdue</th>
                </tr>
              </thead>
              <tbody>
                @for (s of summary().overdueShipments; track s.shipmentId) {
                  <tr>
                    <td class="mono">{{ s.shipmentId }}</td>
                    <td>{{ s.companyName }}</td>
                    <td>{{ s.origin }}</td>
                    <td>{{ s.destination }}</td>
                    <td><span class="badge badge-danger">{{ s.daysOverdue }}</span></td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .dashboard-grid {
      display: grid;
      grid-template-columns: 1fr 380px;
      gap: var(--space-6);
      align-items: start;
    }

    @media (max-width: 1279px) {
      .dashboard-grid { grid-template-columns: 1fr; }
    }

    .dashboard-right {
      display: flex;
      flex-direction: column;
      gap: var(--space-5);
    }

    /* Bary stats row */
    .bary-stats {
      display: flex;
      align-items: center;
      gap: var(--space-6);
      padding: var(--space-4) 0;
    }

    .bary-stat {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .bary-stat-divider {
      width: 1px;
      height: 40px;
      background-color: var(--color-neutral-300);
    }

    /* Company site list */
    .company-site-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: var(--space-4);
      padding: var(--space-2) 0;
      border-bottom: 1px solid var(--color-neutral-300);

      &:last-child { border-bottom: none; }
    }

    .company-site-info {
      display: flex;
      align-items: center;
      gap: var(--space-3);
      min-width: 160px;
    }

    .company-site-name {
      font-size: var(--font-size-body);
      font-weight: 500;
      color: var(--color-neutral-900);
    }

    .weight-value {
      font-size: var(--font-size-small);
      color: var(--color-neutral-700);
      min-width: 54px;
      text-align: right;
    }

    .empty-hint {
      font-size: var(--font-size-small);
      color: var(--color-neutral-500);
      a { color: var(--color-primary-600); }
    }

    /* Activity feed */
    .activity-feed {
      padding: var(--space-2) var(--space-6);
    }

    .activity-item {
      display: flex;
      align-items: flex-start;
      gap: var(--space-3);
      padding: var(--space-3) 0;
      border-bottom: 1px solid var(--color-neutral-300);

      &:last-child { border-bottom: none; }
    }

    .activity-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background-color: var(--color-primary-400);
      flex-shrink: 0;
      margin-top: 5px;
    }

    .activity-message {
      font-size: var(--font-size-small);
      color: var(--color-neutral-900);
      line-height: 1.4;
    }

    .activity-time {
      font-size: 11px;
      color: var(--color-neutral-500);
      display: block;
      margin-top: 2px;
    }
  `]
})
export class DashboardComponent {
  private dataService = inject(DataService);

  summary = this.dataService.dashboardSummary;

  today = new Date().toLocaleDateString('en-US', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });

  companiesWithSites = computed(() =>
    this.dataService.companies().filter(c => c.consumptionSiteCount > 0)
  );

  maxTons = computed(() =>
    Math.max(...this.companiesWithSites().map(c => c.totalTrafficTons), 1)
  );

  latestResults = computed(() =>
    this.dataService.results().slice(0, 5)
  );

  totalTonsFormatted = computed(() => {
    const t = this.summary().totalTrafficTons;
    return t >= 1000 ? `${(t / 1000).toFixed(1)}k t` : `${t.toFixed(0)} t`;
  });

  getCompanyName(id: string): string {
    return this.dataService.getCompanyById(id)?.name ?? id;
  }

  formatTons(t: number): string {
    return t >= 1000 ? `${(t / 1000).toFixed(1)}k t` : `${t.toFixed(0)} t`;
  }

  statusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      CANDIDATE: 'badge-info',
      APPROVED:  'badge-success',
      CONFIRMED: 'badge-success',
      REJECTED:  'badge-danger',
    };
    return `badge ${map[status] ?? 'badge-neutral'}`;
  }
}
