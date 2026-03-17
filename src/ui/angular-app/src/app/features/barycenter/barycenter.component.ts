import {
  Component, inject, signal, computed, OnInit,
  AfterViewInit, OnDestroy, ElementRef, ViewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import { ToastService } from '../../core/services/toast.service';
import { BarycentreResult, AlgorithmType, Company, ConsumptionSite } from '../../core/models';
import * as L from 'leaflet';

// ---------------------------------------------------------------------------
// Fix Leaflet's default icon path issue in Angular builds
// ---------------------------------------------------------------------------
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl:       'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl:     'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

@Component({
  selector: 'app-barycenter',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, ReactiveFormsModule],
  template: `
    <div class="page-content page-enter bary-layout">
      <!-- ═══════════════ LEFT PANEL ═══════════════ -->
      <aside class="bary-panel">
        <!-- Panel header -->
        <div class="panel-header">
          <h1 class="page-title" style="font-size: var(--font-size-h2);">Barycenter Calculator</h1>
          <p class="page-subtitle">Optimal logistics center from weighted sites</p>
        </div>

        <!-- Company selector -->
        <div class="card panel-card">
          <div class="card-body">
            <div class="form-group">
              <label class="form-label" for="bCompany">Select Company</label>
              <select id="bCompany" class="form-control" [(ngModel)]="selectedCompanyId" (ngModelChange)="onCompanyChange()">
                <option value="">— Choose a company —</option>
                @for (c of dataService.companies(); track c.id) {
                  <option [value]="c.id">{{ c.name }} ({{ getSiteCount(c.id) }} sites)</option>
                }
              </select>
            </div>

            @if (selectedCompanyId) {
              <div style="margin-top: var(--space-4);">
                <div class="form-group">
                  <label class="form-label" for="bAlgo">Algorithm</label>
                  <select id="bAlgo" class="form-control" [(ngModel)]="algorithm">
                    <option value="weighted-barycenter">Simple Weighted Barycenter</option>
                    <option value="weiszfeld-iterative">Weiszfeld Iterative Refinement</option>
                  </select>
                </div>

                @if (algorithm === 'weiszfeld-iterative') {
                  <div class="grid-2" style="margin-top: var(--space-3);">
                    <div class="form-group">
                      <label class="form-label" for="bMaxIter">Max Iterations</label>
                      <input id="bMaxIter" class="form-control" type="number" min="1" max="10000" [(ngModel)]="maxIterations" />
                    </div>
                    <div class="form-group">
                      <label class="form-label" for="bTol">Tolerance (km)</label>
                      <input id="bTol" class="form-control" type="number" min="0.0001" step="0.001" [(ngModel)]="toleranceKm" />
                    </div>
                  </div>
                }

                <div class="algorithm-info" style="margin-top: var(--space-3);">
                  @if (algorithm === 'weiszfeld-iterative') {
                    <strong>Weiszfeld:</strong> Minimises sum of weighted geodesic distances (geometric median). Best for logistics cost optimisation.
                  } @else {
                    <strong>Simple Barycenter:</strong> Weighted arithmetic mean of coordinates. Instant convergence, minimises squared distances.
                  }
                </div>

                <button
                  class="btn btn-primary w-full"
                  style="margin-top: var(--space-4);"
                  (click)="runCalculation()"
                  [disabled]="activeSiteCount() < 2 || calculating()"
                >
                  @if (calculating()) {
                    <div class="spinner" style="width:18px; height:18px; border-width:2px;"></div>
                    Calculating…
                  } @else {
                    <span class="material-icons">my_location</span>
                    Calculate Barycenter
                  }
                </button>

                @if (activeSiteCount() < 2) {
                  <p class="form-error" style="margin-top: var(--space-2);" role="alert">
                    <span class="material-icons">warning</span>
                    At least 2 active sites required (currently {{ activeSiteCount() }}).
                    <a routerLink="/sites">Add sites</a>
                  </p>
                }
              </div>
            }
          </div>
        </div>

        <!-- Site weights table -->
        @if (selectedCompanySites().length > 0) {
          <div class="card panel-card">
            <div class="card-header">
              <div>
                <h2 class="card-title" style="font-size: var(--font-size-h3);">Input Sites</h2>
                <p class="card-subtitle">{{ activeSiteCount() }} active · {{ totalTons() | number:'1.0-0' }} t total</p>
              </div>
              <a [routerLink]="['/sites']" [queryParams]="{companyId: selectedCompanyId}" class="btn btn-ghost btn-sm">
                <span class="material-icons">edit</span>
              </a>
            </div>
            <div class="card-body" style="padding: var(--space-2) var(--space-4);">
              @for (site of selectedCompanySites(); track site.id) {
                <div class="site-weight-row" [class.inactive-site]="site.status === 'INACTIVE'">
                  <span class="material-icons site-weight-icon" [style.color]="site.status === 'ACTIVE' ? 'var(--color-warning-600)' : 'var(--color-neutral-300)'">location_on</span>
                  <div class="site-weight-info">
                    <p class="site-weight-name">{{ site.name }}</p>
                    <p class="site-weight-city">{{ site.city || site.formattedCoordinate }}</p>
                  </div>
                  <div class="site-weight-bar-col">
                    <div class="weight-bar-track">
                      <div class="weight-bar-fill" [style.width.%]="siteWeightPercent(site)" [style.background-color]="site.status === 'ACTIVE' ? 'var(--color-primary-600)' : 'var(--color-neutral-300)'"></div>
                    </div>
                  </div>
                  <span class="site-weight-tons mono">{{ site.weightFormatted }}</span>
                </div>
              }
            </div>
          </div>
        }

        <!-- Calculation history -->
        @if (companyResults().length > 0) {
          <div class="card panel-card">
            <div class="card-header">
              <h2 class="card-title" style="font-size: var(--font-size-h3);">Calculation History</h2>
            </div>
            <div class="card-body" style="padding: 0;">
              @for (r of companyResults(); track r.logisticsCenterId; let first = $first) {
                <div
                  class="history-row"
                  [class.history-selected]="activeResult()?.logisticsCenterId === r.logisticsCenterId"
                  (click)="selectResult(r)"
                  role="button"
                  tabindex="0"
                  (keydown.enter)="selectResult(r)"
                  [attr.aria-label]="'Select result ' + r.formattedCoordinate"
                >
                  <div class="history-icon-wrap" [style.background-color]="resultColor(r.status)">
                    <span class="material-icons" style="font-size:16px; color:#fff;">my_location</span>
                  </div>
                  <div class="history-info">
                    <p class="history-coord mono">{{ r.formattedCoordinate }}</p>
                    <p class="history-meta">
                      {{ r.algorithmDescription === 'weiszfeld-iterative' ? 'Weiszfeld' : 'Simple' }}
                      · {{ r.inputSiteCount }} sites
                      @if (first) { <span class="badge badge-primary" style="font-size:9px; padding: 1px 6px;">Latest</span> }
                    </p>
                  </div>
                  <span class="badge" [class]="statusBadgeClass(r.status)">{{ r.status }}</span>
                </div>
              }
            </div>
          </div>
        }
      </aside>

      <!-- ═══════════════ MAIN AREA ═══════════════ -->
      <main class="bary-main">
        <!-- Map -->
        <div class="card map-card">
          <div class="card-header">
            <div>
              <h2 class="card-title">Geographic Visualization</h2>
              <p class="card-subtitle">
                @if (activeResult()) {
                  Optimal logistics center: <strong class="mono">{{ activeResult()!.formattedCoordinate }}</strong>
                } @else {
                  Select a company and run calculation to see the optimal location
                }
              </p>
            </div>
            <div class="map-controls">
              <button class="btn btn-icon" (click)="resetMapView()" title="Reset view" aria-label="Reset map view">
                <span class="material-icons">center_focus_strong</span>
              </button>
              <button class="btn btn-icon" (click)="toggleSatellite()" [title]="satelliteMode ? 'Street map' : 'Satellite'" [attr.aria-label]="satelliteMode ? 'Switch to street map' : 'Switch to satellite'">
                <span class="material-icons">{{ satelliteMode ? 'map' : 'satellite' }}</span>
              </button>
            </div>
          </div>
          <div class="map-container" #mapEl aria-label="Interactive map showing consumption sites and optimal logistics center location" role="img"></div>
          <div class="map-legend" aria-label="Map legend">
            <div class="legend-item">
              <span class="legend-dot" style="background: var(--color-warning-600);"></span>
              Consumption Site
            </div>
            <div class="legend-item" style="opacity: 0.4;">
              <span class="legend-dot" style="background: var(--color-neutral-300);"></span>
              Inactive Site
            </div>
            <div class="legend-item">
              <span class="legend-dot" style="background: var(--color-primary-700); width: 16px; height: 16px; border: 2px solid white; box-shadow: 0 2px 6px rgba(0,0,0,0.3);"></span>
              Optimal Center
            </div>
          </div>
        </div>

        <!-- Result detail panel -->
        @if (activeResult()) {
          <div class="result-detail card page-enter">
            <div class="card-header">
              <div>
                <h2 class="card-title">Calculation Result</h2>
                <p class="card-subtitle">{{ getCompanyName(activeResult()!.companyId) }}</p>
              </div>
              <div style="display: flex; gap: var(--space-2);">
                <span class="badge" [class]="statusBadgeClass(activeResult()!.status)">{{ activeResult()!.status }}</span>
                @if (activeResult()!.status === 'CANDIDATE') {
                  <button class="btn btn-primary btn-sm" (click)="approveResult()" aria-label="Approve this result">
                    <span class="material-icons">check</span> Approve
                  </button>
                  <button class="btn btn-secondary btn-sm" (click)="rejectResult()" aria-label="Reject this result">
                    <span class="material-icons">close</span> Reject
                  </button>
                }
              </div>
            </div>

            <div class="card-body">
              <!-- Primary result -->
              <div class="result-primary">
                <div class="result-coord-block">
                  <p class="result-label">Optimal Location</p>
                  <p class="result-coord mono">{{ activeResult()!.formattedCoordinate }}</p>
                  <p class="result-coord-detail">
                    Lat {{ activeResult()!.optimalLatitude.toFixed(6) }},
                    Lon {{ activeResult()!.optimalLongitude.toFixed(6) }}
                  </p>
                </div>

                <div class="result-meta-grid">
                  <div class="result-meta-item">
                    <span class="result-meta-value">{{ activeResult()!.algorithmDescription === 'weiszfeld-iterative' ? 'Weiszfeld' : 'Simple' }}</span>
                    <span class="result-meta-label">Algorithm</span>
                  </div>
                  <div class="result-meta-item">
                    <span class="result-meta-value">{{ activeResult()!.iterationCount }}</span>
                    <span class="result-meta-label">Iterations</span>
                  </div>
                  <div class="result-meta-item">
                    <span class="result-meta-value">{{ activeResult()!.convergenceErrorKm.toFixed(4) }} km</span>
                    <span class="result-meta-label">Convergence Error</span>
                  </div>
                  <div class="result-meta-item">
                    <span class="result-meta-value">{{ (activeResult()!.totalWeightedTons / 1000).toFixed(1) }}k t</span>
                    <span class="result-meta-label">Total Weight</span>
                  </div>
                </div>
              </div>

              <hr class="divider" />

              <!-- Input sites breakdown -->
              <h3 style="font-size: var(--font-size-h3); margin-bottom: var(--space-4);">Site Contribution Analysis</h3>
              <div style="overflow-x: auto;">
                <table class="data-table" style="min-width: 560px;" aria-label="Site contribution analysis">
                  <thead>
                    <tr>
                      <th scope="col">Site</th>
                      <th scope="col">Coordinates</th>
                      <th scope="col">Weight</th>
                      <th scope="col">Weight %</th>
                      <th scope="col">Dist. to Optimal</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (s of sortedInputSites(); track s.siteId) {
                      <tr>
                        <td>
                          <div style="display: flex; align-items: center; gap: var(--space-2);">
                            <span class="material-icons" style="font-size:18px; color: var(--color-warning-600);">location_on</span>
                            {{ s.siteName }}
                          </div>
                        </td>
                        <td class="mono" style="font-size:12px;">{{ s.latitude.toFixed(4) }}, {{ s.longitude.toFixed(4) }}</td>
                        <td class="mono">{{ s.weightTons >= 1000 ? (s.weightTons/1000).toFixed(1)+'k' : s.weightTons.toFixed(0) }} t</td>
                        <td>
                          <div class="weight-bar-container" style="min-width: 120px;">
                            <div class="weight-bar-track">
                              <div class="weight-bar-fill" [style.width.%]="(s.weightTons / activeResult()!.totalWeightedTons) * 100"></div>
                            </div>
                            <span style="font-size:11px; min-width:32px; text-align:right; color: var(--color-neutral-500);">
                              {{ ((s.weightTons / activeResult()!.totalWeightedTons) * 100).toFixed(1) }}%
                            </span>
                          </div>
                        </td>
                        <td class="mono">{{ s.distanceToOptimalKm.toFixed(1) }} km</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>

              <!-- Weighted distance summary -->
              <div class="result-summary-bar">
                <div class="result-summary-item">
                  <span class="material-icons" style="color: var(--color-primary-600);">route</span>
                  <div>
                    <p class="result-summary-value">{{ totalWeightedDistance() | number:'1.0-0' }} km</p>
                    <p class="result-summary-label">Avg weighted distance to all sites</p>
                  </div>
                </div>
                <div class="result-summary-item">
                  <span class="material-icons" style="color: var(--color-success-600);">check_circle</span>
                  <div>
                    <p class="result-summary-value">{{ activeResult()!.inputSiteCount }}</p>
                    <p class="result-summary-label">Sites in calculation</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        }

        <!-- Empty state when no company selected -->
        @if (!selectedCompanyId) {
          <div class="card empty-panel">
            <span class="material-icons empty-icon">my_location</span>
            <h2>Start a Calculation</h2>
            <p>Select a company from the left panel, choose an algorithm, and run the barycenter calculation to find the optimal logistics center location.</p>
            <div class="empty-actions">
              <a routerLink="/companies" class="btn btn-secondary">
                <span class="material-icons">domain</span>
                Manage Companies
              </a>
              <a routerLink="/sites" class="btn btn-primary">
                <span class="material-icons">add_location</span>
                Add Sites
              </a>
            </div>
          </div>
        }
      </main>
    </div>
  `,
  styles: [`
    /* Two-column sticky layout */
    .bary-layout {
      display: grid;
      grid-template-columns: 360px 1fr;
      gap: var(--space-5);
      align-items: start;
      padding-bottom: var(--space-10);
    }

    @media (max-width: 1023px) {
      .bary-layout { grid-template-columns: 1fr; }
    }

    .bary-panel {
      display: flex;
      flex-direction: column;
      gap: var(--space-4);
      position: sticky;
      top: var(--space-6);
      max-height: calc(100vh - var(--topbar-height) - var(--space-10));
      overflow-y: auto;
      padding-right: 4px;

      &::-webkit-scrollbar { width: 4px; }
      &::-webkit-scrollbar-track { background: transparent; }
      &::-webkit-scrollbar-thumb { background: var(--color-neutral-300); border-radius: 2px; }
    }

    .bary-main {
      display: flex;
      flex-direction: column;
      gap: var(--space-5);
    }

    .panel-header {
      padding: 0 var(--space-1);
    }

    .panel-card { /* already .card */ }

    /* Map */
    .map-card { overflow: visible; }

    .map-container {
      height: 440px;
      width: 100%;
    }

    .map-controls {
      display: flex;
      gap: var(--space-1);
      margin-left: auto;
    }

    .map-legend {
      display: flex;
      align-items: center;
      gap: var(--space-5);
      padding: var(--space-3) var(--space-5);
      border-top: 1px solid var(--color-neutral-300);
      font-size: var(--font-size-small);
      color: var(--color-neutral-700);
    }

    .legend-item {
      display: flex;
      align-items: center;
      gap: var(--space-2);
    }

    .legend-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      display: inline-block;
    }

    /* Site weight list */
    .site-weight-row {
      display: flex;
      align-items: center;
      gap: var(--space-2);
      padding: var(--space-2) 0;
      border-bottom: 1px solid var(--color-neutral-300);

      &:last-child { border-bottom: none; }
    }

    .inactive-site { opacity: 0.45; }

    .site-weight-icon { font-size: 20px; flex-shrink: 0; }

    .site-weight-info { flex: 1; min-width: 0; }
    .site-weight-name { font-size: var(--font-size-small); font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .site-weight-city { font-size: 11px; color: var(--color-neutral-500); }

    .site-weight-bar-col {
      width: 60px;
      flex-shrink: 0;
    }

    .site-weight-tons {
      font-size: 11px;
      min-width: 42px;
      text-align: right;
      color: var(--color-neutral-700);
    }

    /* History list */
    .history-row {
      display: flex;
      align-items: center;
      gap: var(--space-3);
      padding: var(--space-3) var(--space-4);
      cursor: pointer;
      border-bottom: 1px solid var(--color-neutral-300);
      transition: background-color 80ms ease-out;

      &:last-child { border-bottom: none; }
      &:hover { background-color: var(--color-primary-050); }
      &.history-selected { background-color: var(--color-primary-100); }
      &:focus-visible { outline: 2px solid var(--color-primary-600); outline-offset: -2px; }
    }

    .history-icon-wrap {
      width: 32px;
      height: 32px;
      border-radius: var(--radius-full);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .history-info { flex: 1; min-width: 0; }
    .history-coord { font-size: var(--font-size-small); font-weight: 600; }
    .history-meta { font-size: 11px; color: var(--color-neutral-500); margin-top: 2px; display: flex; align-items: center; gap: var(--space-2); }

    /* Result detail */
    .result-primary {
      display: grid;
      grid-template-columns: auto 1fr;
      gap: var(--space-6);
      align-items: start;
      margin-bottom: var(--space-5);
    }

    @media (max-width: 767px) {
      .result-primary { grid-template-columns: 1fr; }
    }

    .result-coord-block {
      background-color: var(--color-primary-050);
      border: 1px solid var(--color-primary-100);
      border-radius: var(--radius-3);
      padding: var(--space-4) var(--space-6);
      text-align: center;
    }

    .result-label { font-size: var(--font-size-label); font-weight: 600; color: var(--color-neutral-500); text-transform: uppercase; letter-spacing: 0.04em; margin-bottom: var(--space-2); }
    .result-coord { font-size: var(--font-size-h2); font-weight: 700; color: var(--color-primary-700); margin-bottom: var(--space-1); }
    .result-coord-detail { font-size: var(--font-size-small); color: var(--color-neutral-500); }

    .result-meta-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: var(--space-4);
    }

    .result-meta-item {
      display: flex;
      flex-direction: column;
      gap: 3px;
    }

    .result-meta-value { font-size: var(--font-size-h3); font-weight: 700; color: var(--color-neutral-900); }
    .result-meta-label { font-size: var(--font-size-label); font-weight: 600; color: var(--color-neutral-500); text-transform: uppercase; }

    .result-summary-bar {
      display: flex;
      gap: var(--space-6);
      margin-top: var(--space-5);
      padding: var(--space-4) var(--space-5);
      background-color: var(--color-neutral-100);
      border-radius: var(--radius-2);
    }

    .result-summary-item {
      display: flex;
      align-items: center;
      gap: var(--space-3);

      .material-icons { font-size: 28px; }
    }

    .result-summary-value { font-size: var(--font-size-h3); font-weight: 700; }
    .result-summary-label { font-size: var(--font-size-small); color: var(--color-neutral-500); }

    /* Empty state */
    .empty-panel {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: var(--space-12) var(--space-8);
      text-align: center;
      min-height: 320px;
    }

    .empty-icon { font-size: 56px; color: var(--color-neutral-300); margin-bottom: var(--space-4); }

    .empty-panel h2 { font-size: var(--font-size-h2); margin-bottom: var(--space-3); }

    .empty-panel p {
      color: var(--color-neutral-500);
      max-width: 440px;
      margin-bottom: var(--space-5);
      line-height: 1.6;
    }

    .empty-actions { display: flex; gap: var(--space-3); }
  `]
})
export class BarycenterComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('mapEl', { static: false }) mapEl!: ElementRef<HTMLDivElement>;

  dataService = inject(DataService);
  private toast  = inject(ToastService);
  private route  = inject(ActivatedRoute);

  // ── Calculator state ──────────────────────────────────────────────────────
  selectedCompanyId = '';
  algorithm: AlgorithmType = 'weiszfeld-iterative';
  maxIterations = 1000;
  toleranceKm   = 0.01;
  calculating   = signal(false);
  activeResult  = signal<BarycentreResult | null>(null);
  satelliteMode = false;

  // ── Leaflet state ──────────────────────────────────────────────────────────
  private map?: L.Map;
  private siteMarkers: L.CircleMarker[] = [];
  private baryMarker?: L.Marker;
  private radiusLines: L.Polyline[] = [];
  private streetLayer!: L.TileLayer;
  private satelliteLayer!: L.TileLayer;

  // ── Computed ───────────────────────────────────────────────────────────────

  selectedCompanySites = computed(() =>
    this.selectedCompanyId
      ? this.dataService.getSitesByCompany(this.selectedCompanyId)
      : []
  );

  activeSiteCount = computed(() =>
    this.selectedCompanySites().filter(s => s.status === 'ACTIVE').length
  );

  totalTons = computed(() =>
    this.selectedCompanySites()
      .filter(s => s.status === 'ACTIVE')
      .reduce((sum, s) => sum + s.weightTons, 0)
  );

  companyResults = computed(() =>
    this.selectedCompanyId
      ? this.dataService.getResultsByCompany(this.selectedCompanyId)
      : []
  );

  sortedInputSites = computed(() =>
    this.activeResult()
      ? [...this.activeResult()!.inputSites].sort((a, b) => b.weightTons - a.weightTons)
      : []
  );

  totalWeightedDistance = computed(() => {
    if (!this.activeResult()) return 0;
    return this.activeResult()!.inputSites.reduce(
      (sum, s) => sum + (s.distanceToOptimalKm * s.weightTons) / this.activeResult()!.totalWeightedTons,
      0
    );
  });

  // ── Lifecycle ──────────────────────────────────────────────────────────────

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['companyId']) {
        this.selectedCompanyId = params['companyId'];
        this.onCompanyChange();
      }
    });
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.initMap(), 80);
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  // ── Map ────────────────────────────────────────────────────────────────────

  private initMap(): void {
    if (!this.mapEl?.nativeElement || this.map) return;

    this.streetLayer = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      { attribution: '© OpenStreetMap contributors', maxZoom: 18 }
    );

    this.satelliteLayer = L.tileLayer(
      'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
      { attribution: 'Tiles © Esri', maxZoom: 18 }
    );

    this.map = L.map(this.mapEl.nativeElement, {
      center: [39.5, -98.35],
      zoom: 4,
      layers: [this.streetLayer],
      zoomControl: true,
    });

    this.refreshMapMarkers();
  }

  private refreshMapMarkers(): void {
    if (!this.map) return;

    // Clear existing
    this.siteMarkers.forEach(m => m.remove());
    this.siteMarkers = [];
    this.radiusLines.forEach(l => l.remove());
    this.radiusLines = [];

    const sites = this.selectedCompanySites();
    const result = this.activeResult();

    // Draw site markers
    for (const site of sites) {
      const isActive = site.status === 'ACTIVE';
      const marker = L.circleMarker([site.latitude, site.longitude], {
        radius: 8 + Math.sqrt(site.weightTons / 100),
        fillColor:   isActive ? '#D97706' : '#D1D5DB',
        color:       '#fff',
        weight:      2,
        opacity:     1,
        fillOpacity: isActive ? 0.85 : 0.4,
      }).addTo(this.map!);

      marker.bindPopup(`
        <div style="min-width:180px;">
          <strong style="font-size:13px;">${site.name}</strong>
          ${site.city ? `<br><span style="color:#6B7280; font-size:11px;">${site.city}, ${site.country ?? ''}</span>` : ''}
          <hr style="margin:6px 0; border-color:#E5E7EB;"/>
          <span style="font-size:11px; color:#374151;">
            <b>Weight:</b> ${site.weightFormatted}<br/>
            <b>Coord:</b> <code>${site.formattedCoordinate}</code><br/>
            <b>Status:</b> ${site.status}
          </span>
        </div>
      `);
      this.siteMarkers.push(marker);

      // Draw line from site to optimal result
      if (result && isActive) {
        const line = L.polyline([
          [site.latitude, site.longitude],
          [result.optimalLatitude, result.optimalLongitude]
        ], {
          color: '#5B9BD5',
          weight: 1.5,
          opacity: 0.35,
          dashArray: '5 5',
        }).addTo(this.map!);
        this.radiusLines.push(line);
      }
    }

    // Draw barycenter marker
    if (this.baryMarker) {
      this.baryMarker.remove();
      this.baryMarker = undefined;
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
        className: '',
        iconSize: [22, 22],
        iconAnchor: [11, 11],
      });

      this.baryMarker = L.marker([result.optimalLatitude, result.optimalLongitude], { icon })
        .bindPopup(`
          <div style="min-width:200px;">
            <strong style="font-size:13px; color:#1F4E79;">Optimal Logistics Center</strong>
            <hr style="margin:6px 0; border-color:#E5E7EB;"/>
            <code style="font-size:12px;">${result.formattedCoordinate}</code><br/>
            <span style="font-size:11px; color:#374151;">
              Algorithm: ${result.algorithmDescription === 'weiszfeld-iterative' ? 'Weiszfeld' : 'Simple'}<br/>
              Iterations: ${result.iterationCount}<br/>
              Sites: ${result.inputSiteCount}<br/>
              Total weight: ${(result.totalWeightedTons / 1000).toFixed(1)}k t
            </span>
          </div>
        `)
        .addTo(this.map!);
    }

    // Fit map to all markers
    const allLatLngs: L.LatLngExpression[] = sites.map(s => [s.latitude, s.longitude] as [number, number]);
    if (result) allLatLngs.push([result.optimalLatitude, result.optimalLongitude]);

    if (allLatLngs.length > 0) {
      this.map!.fitBounds(L.latLngBounds(allLatLngs as any), { padding: [40, 40] });
    } else {
      this.map!.setView([39.5, -98.35], 4);
    }
  }

  resetMapView(): void {
    if (!this.map) return;
    const sites = this.selectedCompanySites();
    const result = this.activeResult();
    const allLatLngs: L.LatLngExpression[] = sites.map(s => [s.latitude, s.longitude] as [number, number]);
    if (result) allLatLngs.push([result.optimalLatitude, result.optimalLongitude]);
    if (allLatLngs.length > 0) {
      this.map.fitBounds(L.latLngBounds(allLatLngs as any), { padding: [40, 40] });
    } else {
      this.map.setView([39.5, -98.35], 4);
    }
  }

  toggleSatellite(): void {
    if (!this.map) return;
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

  onCompanyChange(): void {
    this.activeResult.set(null);
    const results = this.dataService.getResultsByCompany(this.selectedCompanyId);
    if (results.length > 0) this.activeResult.set(results[0]);
    setTimeout(() => {
      this.refreshMapMarkers();
      this.map?.invalidateSize();
    }, 100);
  }

  runCalculation(): void {
    if (this.activeSiteCount() < 2) return;
    this.calculating.set(true);

    setTimeout(() => {
      try {
        const result = this.dataService.calculate({
          companyId:     this.selectedCompanyId,
          algorithm:     this.algorithm,
          maxIterations: this.maxIterations,
          toleranceKm:   this.toleranceKm,
        });
        this.activeResult.set(result);
        this.refreshMapMarkers();
        this.toast.success(
          'Barycenter calculated',
          `Optimal location: ${result.formattedCoordinate} · ${result.iterationCount} iteration(s)`
        );
      } catch (err: any) {
        this.toast.error('Calculation failed', err.message);
      } finally {
        this.calculating.set(false);
      }
    }, 600); // simulate async processing
  }

  selectResult(result: BarycentreResult): void {
    this.activeResult.set(result);
    this.refreshMapMarkers();
  }

  approveResult(): void {
    if (!this.activeResult()) return;
    this.dataService.updateResultStatus(this.activeResult()!.logisticsCenterId, 'APPROVED');
    this.activeResult.update(r => r ? { ...r, status: 'APPROVED' } : r);
    this.toast.success('Result approved', 'Logistics center candidate has been approved.');
  }

  rejectResult(): void {
    if (!this.activeResult()) return;
    this.dataService.updateResultStatus(this.activeResult()!.logisticsCenterId, 'REJECTED');
    this.activeResult.update(r => r ? { ...r, status: 'REJECTED' } : r);
    this.toast.warning('Result rejected', 'Logistics center candidate has been rejected.');
  }

  // ── Helpers ────────────────────────────────────────────────────────────────

  getSiteCount(companyId: string): number {
    return this.dataService.getSitesByCompany(companyId).filter(s => s.status === 'ACTIVE').length;
  }

  getCompanyName(id: string): string {
    return this.dataService.getCompanyById(id)?.name ?? id;
  }

  siteWeightPercent(site: ConsumptionSite): number {
    const max = this.totalTons();
    return max > 0 ? (site.weightTons / max) * 100 : 0;
  }

  statusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      CANDIDATE: 'badge badge-info',
      APPROVED:  'badge badge-success',
      CONFIRMED: 'badge badge-success',
      REJECTED:  'badge badge-danger',
    };
    return map[status] ?? 'badge badge-neutral';
  }

  resultColor(status: string): string {
    const map: Record<string, string> = {
      CANDIDATE: 'var(--color-info-600)',
      APPROVED:  'var(--color-success-600)',
      CONFIRMED: 'var(--color-success-600)',
      REJECTED:  'var(--color-danger-600)',
    };
    return map[status] ?? 'var(--color-neutral-500)';
  }
}