import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import { ToastService } from '../../core/services/toast.service';
import {
  ConsumptionSite, SiteFilter, SiteStatus,
  CreateSitePayload, UpdateSitePayload
} from '../../core/models';

@Component({
  selector: 'app-sites',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, ReactiveFormsModule],
  template: `
    <div class="page-content page-enter">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1 class="page-title">Consumption Sites</h1>
          <p class="page-subtitle">{{ filteredSites().length }} sites · {{ totalTonsFormatted() }} total traffic</p>
        </div>
        <div class="page-actions">
          <button class="btn btn-primary" (click)="openCreateModal()">
            <span class="material-icons">add_location</span>
            Add Site
          </button>
        </div>
      </div>

      <!-- Filters toolbar -->
      <div class="card">
        <div class="card-body" style="padding: var(--space-3) var(--space-5);">
          <div class="filter-row">
            <div class="search-field">
              <span class="material-icons search-icon-sm" aria-hidden="true">search</span>
              <input
                class="form-control"
                type="search"
                placeholder="Search sites, cities…"
                [(ngModel)]="filter.search"
                aria-label="Search sites"
              />
            </div>

            <select class="form-control filter-select" [(ngModel)]="filter.companyId" aria-label="Filter by company">
              <option value="ALL">All Companies</option>
              @for (c of dataService.companies(); track c.id) {
                <option [value]="c.id">{{ c.name }}</option>
              }
            </select>

            <select class="form-control filter-select" [(ngModel)]="filter.status" aria-label="Filter by status">
              <option value="ALL">All Statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>

            @if (filter.search || filter.status !== 'ALL' || filter.companyId !== 'ALL') {
              <button class="btn btn-ghost btn-sm" (click)="clearFilters()">
                <span class="material-icons">filter_list_off</span>
                Clear
              </button>
            }

            <div style="margin-left: auto;">
              <button class="btn btn-icon" (click)="viewMode.set('table')" [class.active-mode]="viewMode()==='table'" title="Table view" aria-label="Table view">
                <span class="material-icons">table_rows</span>
              </button>
              <button class="btn btn-icon" (click)="viewMode.set('cards')" [class.active-mode]="viewMode()==='cards'" title="Card view" aria-label="Card view">
                <span class="material-icons">grid_view</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Table view -->
      @if (viewMode() === 'table') {
        <div class="data-table-wrapper">
          <table class="data-table" aria-label="Consumption sites list">
            <thead>
              <tr>
                <th scope="col" class="sortable" (click)="sort('name')" [class.sort-asc]="sortCol==='name'&&sortDir==='asc'" [class.sort-desc]="sortCol==='name'&&sortDir==='desc'">
                  Site <span class="sort-indicator material-icons" aria-hidden="true">unfold_more</span>
                </th>
                <th scope="col">Company</th>
                <th scope="col">Location</th>
                <th scope="col">Coordinates</th>
                <th scope="col" class="sortable" (click)="sort('weightTons')" [class.sort-asc]="sortCol==='weightTons'&&sortDir==='asc'" [class.sort-desc]="sortCol==='weightTons'&&sortDir==='desc'">
                  Weight <span class="sort-indicator material-icons" aria-hidden="true">unfold_more</span>
                </th>
                <th scope="col">Weight %</th>
                <th scope="col">Status</th>
                <th scope="col" class="actions">Actions</th>
              </tr>
            </thead>
            <tbody>
              @if (filteredSites().length === 0) {
                <tr>
                  <td colspan="8">
                    <div class="table-empty-state">
                      <span class="material-icons">location_off</span>
                      <p>No sites found.</p>
                    </div>
                  </td>
                </tr>
              }
              @for (site of pagedSites(); track site.id) {
                <tr>
                  <td>
                    <div class="site-name-cell">
                      <span class="site-pin" aria-hidden="true">
                        <span class="material-icons" [style.color]="site.status === 'ACTIVE' ? 'var(--color-warning-600)' : 'var(--color-neutral-400)'">location_on</span>
                      </span>
                      <div>
                        <p class="name-primary">{{ site.name }}</p>
                        @if (site.description) {
                          <p class="name-secondary">{{ site.description }}</p>
                        }
                      </div>
                    </div>
                  </td>
                  <td>
                    <a [routerLink]="['/companies']" class="company-link">{{ getCompanyName(site.companyId) }}</a>
                  </td>
                  <td>
                    @if (site.city) {
                      <span>{{ site.city }}{{ site.country ? ', ' + site.country : '' }}</span>
                    } @else {
                      <span class="muted">—</span>
                    }
                  </td>
                  <td>
                    <span class="coord-display" title="Latitude: {{ site.latitude }}, Longitude: {{ site.longitude }}">
                      {{ site.formattedCoordinate }}
                    </span>
                  </td>
                  <td class="mono">{{ site.weightFormatted }}</td>
                  <td>
                    <div class="weight-bar-container" style="min-width: 100px;">
                      <div class="weight-bar-track">
                        <div class="weight-bar-fill" [style.width.%]="weightPercent(site)" [attr.aria-label]="weightPercent(site).toFixed(1) + '%'"></div>
                      </div>
                      <span style="font-size:11px; color: var(--color-neutral-500); min-width: 34px; text-align:right;">{{ weightPercent(site).toFixed(0) }}%</span>
                    </div>
                  </td>
                  <td>
                    <span class="badge" [class]="site.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'">{{ site.status }}</span>
                  </td>
                  <td class="actions">
                    <button class="btn btn-icon" (click)="openEditModal(site)" [attr.aria-label]="'Edit ' + site.name" title="Edit">
                      <span class="material-icons">edit</span>
                    </button>
                    <button class="btn btn-icon" (click)="toggleStatus(site)" [attr.aria-label]="(site.status === 'ACTIVE' ? 'Deactivate' : 'Activate') + ' ' + site.name" [title]="site.status === 'ACTIVE' ? 'Deactivate' : 'Activate'">
                      <span class="material-icons">{{ site.status === 'ACTIVE' ? 'pause_circle' : 'play_circle' }}</span>
                    </button>
                    <button class="btn btn-icon" (click)="confirmDelete(site)" [attr.aria-label]="'Delete ' + site.name" title="Delete" style="color: var(--color-danger-600);">
                      <span class="material-icons">delete_outline</span>
                    </button>
                  </td>
                </tr>
              }
            </tbody>
          </table>
          <!-- Pagination -->
          <div class="pagination-bar">
            <span>Showing {{ pageStart() }}–{{ pageEnd() }} of {{ filteredSites().length }} sites</span>
            <div class="pagination-controls">
              <button class="page-btn" (click)="prevPage()" [disabled]="page() === 1" aria-label="Previous page">
                <span class="material-icons" style="font-size:18px;">chevron_left</span>
              </button>
              @for (p of pageNumbers(); track p) {
                <button class="page-btn" [class.active]="p === page()" (click)="page.set(p)" [attr.aria-label]="'Page ' + p">{{ p }}</button>
              }
              <button class="page-btn" (click)="nextPage()" [disabled]="page() === totalPages()" aria-label="Next page">
                <span class="material-icons" style="font-size:18px;">chevron_right</span>
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Card view -->
      @if (viewMode() === 'cards') {
        <div class="site-cards-grid">
          @if (filteredSites().length === 0) {
            <div class="card" style="padding: var(--space-8); text-align: center; grid-column: 1 / -1;">
              <span class="material-icons" style="font-size:48px; color: var(--color-neutral-300);">location_off</span>
              <p style="color: var(--color-neutral-500);">No sites found.</p>
            </div>
          }
          @for (site of pagedSites(); track site.id) {
            <div class="site-card card">
              <div class="site-card-header">
                <span class="material-icons site-card-pin" [style.color]="site.status === 'ACTIVE' ? 'var(--color-warning-600)' : 'var(--color-neutral-300)'">location_on</span>
                <div class="site-card-title-area">
                  <h3 class="site-card-name">{{ site.name }}</h3>
                  <p class="site-card-company">{{ getCompanyName(site.companyId) }}</p>
                </div>
                <span class="badge" [class]="site.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'" style="margin-left: auto;">{{ site.status }}</span>
              </div>
              <div class="site-card-body">
                @if (site.city) {
                  <p class="site-card-location">
                    <span class="material-icons" style="font-size: 14px;">place</span>
                    {{ site.city }}{{ site.country ? ', ' + site.country : '' }}
                  </p>
                }
                <p class="coord-display site-card-coord">{{ site.formattedCoordinate }}</p>
                <div class="site-card-weight">
                  <span class="site-weight-value">{{ site.weightFormatted }}</span>
                  <div class="weight-bar-container" style="flex: 1;">
                    <div class="weight-bar-track">
                      <div class="weight-bar-fill" [style.width.%]="weightPercent(site)"></div>
                    </div>
                  </div>
                  <span class="site-weight-pct">{{ weightPercent(site).toFixed(0) }}%</span>
                </div>
              </div>
              <div class="site-card-actions">
                <button class="btn btn-ghost btn-sm" (click)="openEditModal(site)" [attr.aria-label]="'Edit ' + site.name">
                  <span class="material-icons">edit</span> Edit
                </button>
                <button class="btn btn-ghost btn-sm" (click)="toggleStatus(site)" [attr.aria-label]="(site.status === 'ACTIVE' ? 'Deactivate' : 'Activate') + ' ' + site.name">
                  <span class="material-icons">{{ site.status === 'ACTIVE' ? 'pause_circle' : 'play_circle' }}</span>
                  {{ site.status === 'ACTIVE' ? 'Deactivate' : 'Activate' }}
                </button>
              </div>
            </div>
          }
        </div>
      }
    </div>

    <!-- Create / Edit modal -->
    @if (showModal()) {
      <div class="modal-overlay" role="dialog" aria-modal="true" [attr.aria-label]="editTarget() ? 'Edit site' : 'Add site'" (click)="closeModal()">
        <div class="modal modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>{{ editTarget() ? 'Edit Site' : 'Add Consumption Site' }}</h2>
            <button class="btn btn-icon" (click)="closeModal()" aria-label="Close">
              <span class="material-icons">close</span>
            </button>
          </div>
          <form [formGroup]="form" (ngSubmit)="submitForm()" novalidate>
            <div class="modal-body">
              <!-- Identity -->
              <div class="grid-2">
                <div class="form-group" style="grid-column: span 2;">
                  <label class="form-label" for="sSiteName">Site Name *</label>
                  <input id="sSiteName" class="form-control" formControlName="name" [class.error]="fieldError('name')" />
                  @if (fieldError('name')) {
                    <p class="form-error" role="alert"><span class="material-icons">error_outline</span> Name is required</p>
                  }
                </div>
              </div>

              <div class="form-group" style="margin-top: var(--space-4);">
                <label class="form-label" for="sCompany">Company *</label>
                <select id="sCompany" class="form-control" formControlName="companyId" [class.error]="fieldError('companyId')">
                  <option value="">Select a company…</option>
                  @for (c of dataService.companies(); track c.id) {
                    <option [value]="c.id">{{ c.name }}</option>
                  }
                </select>
                @if (fieldError('companyId')) {
                  <p class="form-error" role="alert"><span class="material-icons">error_outline</span> Company is required</p>
                }
              </div>

              <div class="form-group" style="margin-top: var(--space-4);">
                <label class="form-label" for="sDesc">Description</label>
                <input id="sDesc" class="form-control" formControlName="description" placeholder="Optional description" />
              </div>

              <hr class="divider" />
              <p class="section-title">Geographic Coordinates</p>
              <p class="form-hint" style="margin-bottom: var(--space-3);">
                Enter WGS-84 decimal-degree coordinates. Latitude: −90 to 90, Longitude: −180 to 180.
              </p>

              <div class="grid-2">
                <div class="form-group">
                  <label class="form-label" for="sLat">Latitude *</label>
                  <input id="sLat" class="form-control" type="number" step="0.0001" formControlName="latitude" [class.error]="fieldError('latitude')" placeholder="e.g. 40.7128" />
                  @if (fieldError('latitude')) {
                    <p class="form-error" role="alert"><span class="material-icons">error_outline</span> Valid latitude (−90 to 90) required</p>
                  }
                </div>
                <div class="form-group">
                  <label class="form-label" for="sLon">Longitude *</label>
                  <input id="sLon" class="form-control" type="number" step="0.0001" formControlName="longitude" [class.error]="fieldError('longitude')" placeholder="e.g. −74.0060" />
                  @if (fieldError('longitude')) {
                    <p class="form-error" role="alert"><span class="material-icons">error_outline</span> Valid longitude (−180 to 180) required</p>
                  }
                </div>
              </div>

              <hr class="divider" />
              <p class="section-title">Traffic Weight</p>

              <div class="form-group">
                <label class="form-label" for="sWeight">Annual Traffic Volume (tons) *</label>
                <input id="sWeight" class="form-control" type="number" min="0" step="1" formControlName="weightTons" [class.error]="fieldError('weightTons')" placeholder="e.g. 450" style="max-width: 240px;" />
                @if (fieldError('weightTons')) {
                  <p class="form-error" role="alert"><span class="material-icons">error_outline</span> Weight must be a positive number</p>
                }
                <p class="form-hint">Tonnage used as the weight in barycenter calculation.</p>
              </div>

              <hr class="divider" />
              <p class="section-title">Address (Optional)</p>

              <div class="grid-3">
                <div class="form-group">
                  <label class="form-label" for="sAddr">Street Address</label>
                  <input id="sAddr" class="form-control" formControlName="address" autocomplete="street-address" />
                </div>
                <div class="form-group">
                  <label class="form-label" for="sCity">City</label>
                  <input id="sCity" class="form-control" formControlName="city" autocomplete="address-level2" />
                </div>
                <div class="form-group">
                  <label class="form-label" for="sCountry">Country</label>
                  <input id="sCountry" class="form-control" formControlName="country" autocomplete="country-name" />
                </div>
              </div>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="form.invalid">
                <span class="material-icons">{{ editTarget() ? 'save' : 'add_location' }}</span>
                {{ editTarget() ? 'Save Changes' : 'Add Site' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    }

    <!-- Delete confirmation -->
    @if (deleteTarget()) {
      <div class="modal-overlay" role="alertdialog" aria-modal="true" aria-label="Confirm site deletion">
        <div class="modal modal-sm">
          <div class="modal-header">
            <h2>Delete Site</h2>
            <button class="btn btn-icon" (click)="deleteTarget.set(null)" aria-label="Cancel">
              <span class="material-icons">close</span>
            </button>
          </div>
          <div class="modal-body">
            <p>Delete <strong>{{ deleteTarget()!.name }}</strong>?</p>
            <p style="margin-top: var(--space-2); font-size: var(--font-size-small); color: var(--color-neutral-500);">
              This will remove it from all barycenter calculations.
            </p>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" (click)="deleteTarget.set(null)">Cancel</button>
            <button class="btn btn-danger" (click)="executeDelete()">
              <span class="material-icons">delete</span> Delete
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .filter-row {
      display: flex;
      align-items: center;
      gap: var(--space-3);
      flex-wrap: wrap;
    }

    .search-field {
      position: relative;
      flex: 1;
      min-width: 200px;
      .form-control { padding-left: 36px; }
    }

    .search-icon-sm {
      position: absolute;
      left: 10px;
      top: 50%;
      transform: translateY(-50%);
      font-size: 18px;
      color: var(--color-neutral-500);
      pointer-events: none;
      z-index: 1;
    }

    .filter-select { max-width: 180px; }

    .active-mode {
      background-color: var(--color-primary-050);
      color: var(--color-primary-700);
    }

    .site-name-cell {
      display: flex;
      align-items: center;
      gap: var(--space-2);
    }

    .site-pin .material-icons { font-size: 22px; }

    .name-primary { font-weight: 500; }
    .name-secondary { font-size: 11px; color: var(--color-neutral-500); }

    .company-link {
      color: var(--color-primary-600);
      text-decoration: none;
      font-size: var(--font-size-small);
      &:hover { text-decoration: underline; }
    }

    .muted { color: var(--color-neutral-400); }

    /* Card view grid */
    .site-cards-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: var(--space-5);
    }

    .site-card {
      display: flex;
      flex-direction: column;
    }

    .site-card-header {
      display: flex;
      align-items: flex-start;
      gap: var(--space-3);
      padding: var(--space-4) var(--space-5) var(--space-3);
    }

    .site-card-pin { font-size: 28px; flex-shrink: 0; margin-top: -2px; }

    .site-card-title-area { flex: 1; }
    .site-card-name { font-size: var(--font-size-h3); font-weight: 600; }
    .site-card-company { font-size: var(--font-size-small); color: var(--color-primary-600); margin-top: 2px; }

    .site-card-body {
      padding: var(--space-2) var(--space-5) var(--space-4);
      flex: 1;
    }

    .site-card-location {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: var(--font-size-small);
      color: var(--color-neutral-700);
      margin-bottom: var(--space-2);
    }

    .site-card-coord {
      font-size: 12px;
      margin-bottom: var(--space-3);
    }

    .site-card-weight {
      display: flex;
      align-items: center;
      gap: var(--space-2);
    }

    .site-weight-value { font-size: var(--font-size-small); font-weight: 600; min-width: 50px; }
    .site-weight-pct { font-size: 11px; color: var(--color-neutral-500); min-width: 28px; }

    .site-card-actions {
      display: flex;
      gap: var(--space-2);
      padding: var(--space-3) var(--space-5);
      border-top: 1px solid var(--color-neutral-300);
    }
  `]
})
export class SitesComponent implements OnInit {
  dataService = inject(DataService);
  private toast  = inject(ToastService);
  private fb     = inject(FormBuilder);
  private route  = inject(ActivatedRoute);

  filter: SiteFilter = { search: '', status: 'ALL', companyId: 'ALL' };
  sortCol = 'name';
  sortDir: 'asc' | 'desc' = 'asc';
  page    = signal(1);
  pageSize = 10;
  viewMode = signal<'table' | 'cards'>('table');

  showModal    = signal(false);
  editTarget   = signal<ConsumptionSite | null>(null);
  deleteTarget = signal<ConsumptionSite | null>(null);

  form = this.fb.group({
    companyId:   ['', Validators.required],
    name:        ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    latitude:    [null as number | null, [Validators.required, Validators.min(-90), Validators.max(90)]],
    longitude:   [null as number | null, [Validators.required, Validators.min(-180), Validators.max(180)]],
    weightTons:  [null as number | null, [Validators.required, Validators.min(0)]],
    address:     [''],
    city:        [''],
    country:     [''],
  });

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['companyId']) this.filter.companyId = params['companyId'];
      if (params['search'])    this.filter.search    = params['search'];
    });
  }

  filteredSites = computed(() => {
    let list = [...this.dataService.sites()];
    const f = this.filter;
    if (f.search) {
      const q = f.search.toLowerCase();
      list = list.filter(s =>
        s.name.toLowerCase().includes(q) ||
        (s.city ?? '').toLowerCase().includes(q) ||
        (s.address ?? '').toLowerCase().includes(q)
      );
    }
    if (f.status    !== 'ALL') list = list.filter(s => s.status    === f.status);
    if (f.companyId !== 'ALL') list = list.filter(s => s.companyId === f.companyId);

    list.sort((a, b) => {
      const av = (a as any)[this.sortCol];
      const bv = (b as any)[this.sortCol];
      const cmp = typeof av === 'number' ? av - bv : String(av).localeCompare(String(bv));
      return this.sortDir === 'asc' ? cmp : -cmp;
    });
    return list;
  });

  totalTonsFormatted = computed(() => {
    const t = this.filteredSites()
      .filter(s => s.status === 'ACTIVE')
      .reduce((sum, s) => sum + s.weightTons, 0);
    return t >= 1000 ? `${(t / 1000).toFixed(1)}k t` : `${t.toFixed(0)} t`;
  });

  maxTons = computed(() =>
    Math.max(...this.dataService.activeSites().map(s => s.weightTons), 1)
  );

  totalPages  = computed(() => Math.ceil(this.filteredSites().length / this.pageSize) || 1);
  pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i + 1));
  pageStart   = computed(() => (this.page() - 1) * this.pageSize + 1);
  pageEnd     = computed(() => Math.min(this.page() * this.pageSize, this.filteredSites().length));

  pagedSites = computed(() => {
    const start = (this.page() - 1) * this.pageSize;
    return this.filteredSites().slice(start, start + this.pageSize);
  });

  sort(col: string): void {
    if (this.sortCol === col) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortCol = col;
      this.sortDir = 'desc';
    }
    this.page.set(1);
  }

  prevPage(): void { if (this.page() > 1) this.page.update(p => p - 1); }
  nextPage(): void { if (this.page() < this.totalPages()) this.page.update(p => p + 1); }

  clearFilters(): void {
    this.filter = { search: '', status: 'ALL', companyId: 'ALL' };
    this.page.set(1);
  }

  openCreateModal(): void {
    this.editTarget.set(null);
    this.form.reset();
    this.showModal.set(true);
  }

  openEditModal(site: ConsumptionSite): void {
    this.editTarget.set(site);
    this.form.patchValue({
      companyId: site.companyId,
      name: site.name,
      description: site.description ?? '',
      latitude: site.latitude,
      longitude: site.longitude,
      weightTons: site.weightTons,
      address: site.address ?? '',
      city: site.city ?? '',
      country: site.country ?? '',
    });
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.editTarget.set(null);
  }

  submitForm(): void {
    if (this.form.invalid) return;
    const v = this.form.getRawValue();
    if (this.editTarget()) {
      const payload: UpdateSitePayload = {
        name: v.name!,
        description: v.description || undefined,
        latitude: v.latitude!,
        longitude: v.longitude!,
        weightTons: v.weightTons!,
        address: v.address || undefined,
        city: v.city || undefined,
        country: v.country || undefined,
      };
      this.dataService.updateSite(this.editTarget()!.id, payload);
      this.toast.success('Site updated', `${v.name} has been saved.`);
    } else {
      const payload: CreateSitePayload = {
        companyId: v.companyId!,
        name: v.name!,
        description: v.description || undefined,
        latitude: v.latitude!,
        longitude: v.longitude!,
        weightTons: v.weightTons!,
        address: v.address || undefined,
        city: v.city || undefined,
        country: v.country || undefined,
      };
      this.dataService.createSite(payload);
      this.toast.success('Site added', `${v.name} has been created.`);
    }
    this.closeModal();
  }

  toggleStatus(site: ConsumptionSite): void {
    const newStatus: SiteStatus = site.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    this.dataService.updateSite(site.id, { status: newStatus });
    this.toast.info(
      `Site ${newStatus === 'ACTIVE' ? 'activated' : 'deactivated'}`,
      site.name
    );
  }

  confirmDelete(site: ConsumptionSite): void {
    this.deleteTarget.set(site);
  }

  executeDelete(): void {
    const s = this.deleteTarget();
    if (!s) return;
    this.dataService.deleteSite(s.id);
    this.toast.success('Site deleted', `${s.name} has been removed.`);
    this.deleteTarget.set(null);
  }

  fieldError(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && (ctrl.dirty || ctrl.touched));
  }

  getCompanyName(id: string): string {
    return this.dataService.getCompanyById(id)?.name ?? id;
  }

  weightPercent(site: ConsumptionSite): number {
    const max = this.maxTons();
    return max > 0 ? (site.weightTons / max) * 100 : 0;
  }
}