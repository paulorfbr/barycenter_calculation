import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import { ToastService } from '../../core/services/toast.service';
import {
  Company, CompanyFilter, CompanyStatus, CompanyType,
  CreateCompanyPayload, UpdateCompanyPayload
} from '../../core/models';

@Component({
  selector: 'app-companies',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, ReactiveFormsModule],
  template: `
    <div class="page-content page-enter">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1 class="page-title">Companies</h1>
          <p class="page-subtitle">{{ filteredCompanies().length }} of {{ dataService.companies().length }} companies</p>
        </div>
        <div class="page-actions">
          <button class="btn btn-primary" (click)="openCreateModal()">
            <span class="material-icons">add</span>
            New Company
          </button>
        </div>
      </div>

      <!-- Filters toolbar -->
      <div class="card filters-bar">
        <div class="card-body" style="padding: var(--space-4) var(--space-5);">
          <div class="filter-row">
            <div class="search-field">
              <span class="material-icons search-icon-sm" aria-hidden="true">search</span>
              <input
                class="form-control"
                type="search"
                placeholder="Search companies…"
                [(ngModel)]="filter.search"
                aria-label="Search companies"
              />
            </div>

            <label class="sr-only" for="statusFilter">Filter by status</label>
            <select id="statusFilter" class="form-control filter-select" [(ngModel)]="filter.status" aria-label="Filter by status">
              <option value="ALL">All Statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="PENDING">Pending</option>
            </select>

            <label class="sr-only" for="typeFilter">Filter by type</label>
            <select id="typeFilter" class="form-control filter-select" [(ngModel)]="filter.type" aria-label="Filter by type">
              <option value="ALL">All Types</option>
              <option value="SHIPPER">Shipper</option>
              <option value="CARRIER">Carrier</option>
              <option value="BOTH">Both</option>
            </select>

            @if (filter.search || filter.status !== 'ALL' || filter.type !== 'ALL') {
              <button class="btn btn-ghost btn-sm" (click)="clearFilters()">
                <span class="material-icons">filter_list_off</span>
                Clear
              </button>
            }
          </div>
        </div>
      </div>

      <!-- Table -->
      <div class="data-table-wrapper">
        <table class="data-table" aria-label="Companies list">
          <thead>
            <tr>
              <th scope="col" class="sortable" (click)="sort('name')" [class.sort-asc]="sortCol==='name'&&sortDir==='asc'" [class.sort-desc]="sortCol==='name'&&sortDir==='desc'">
                Company <span class="sort-indicator material-icons" aria-hidden="true">unfold_more</span>
              </th>
              <th scope="col">Type</th>
              <th scope="col">Status</th>
              <th scope="col" class="sortable" (click)="sort('consumptionSiteCount')" [class.sort-asc]="sortCol==='consumptionSiteCount'&&sortDir==='asc'" [class.sort-desc]="sortCol==='consumptionSiteCount'&&sortDir==='desc'">
                Sites <span class="sort-indicator material-icons" aria-hidden="true">unfold_more</span>
              </th>
              <th scope="col" class="sortable" (click)="sort('totalTrafficTons')" [class.sort-asc]="sortCol==='totalTrafficTons'&&sortDir==='asc'" [class.sort-desc]="sortCol==='totalTrafficTons'&&sortDir==='desc'">
                Traffic <span class="sort-indicator material-icons" aria-hidden="true">unfold_more</span>
              </th>
              <th scope="col">Contact</th>
              <th scope="col" class="actions">Actions</th>
            </tr>
          </thead>
          <tbody>
            @if (filteredCompanies().length === 0) {
              <tr>
                <td colspan="7">
                  <div class="table-empty-state">
                    <span class="material-icons">domain_disabled</span>
                    <p>No companies found matching your filters.</p>
                  </div>
                </td>
              </tr>
            }
            @for (company of pagedCompanies(); track company.id) {
              <tr [class.selected]="selectedId() === company.id" (click)="selectedId.set(company.id)">
                <td>
                  <div class="company-name-cell">
                    <span class="company-initial" [style.background-color]="companyColor(company.name)">
                      {{ company.name.charAt(0) }}
                    </span>
                    <div>
                      <p class="name-primary">{{ company.name }}</p>
                      @if (company.taxId) {
                        <p class="name-secondary mono">{{ company.taxId }}</p>
                      }
                    </div>
                  </div>
                </td>
                <td>
                  <span class="badge badge-neutral">{{ company.type }}</span>
                </td>
                <td>
                  <span class="badge" [class]="statusClass(company.status)">{{ company.status }}</span>
                </td>
                <td>
                  @if (company.consumptionSiteCount > 0) {
                    <a [routerLink]="['/sites']" [queryParams]="{companyId: company.id}" class="site-count-link">
                      {{ company.consumptionSiteCount }}
                    </a>
                  } @else {
                    <span class="muted">—</span>
                  }
                </td>
                <td class="mono">
                  @if (company.totalTrafficTons > 0) {
                    {{ formatTons(company.totalTrafficTons) }}
                  } @else {
                    <span class="muted">—</span>
                  }
                </td>
                <td>
                  @if (company.contactName) {
                    <div>
                      <p style="font-size: var(--font-size-small);">{{ company.contactName }}</p>
                      @if (company.contactEmail) {
                        <p style="font-size: 11px; color: var(--color-neutral-500);">{{ company.contactEmail }}</p>
                      }
                    </div>
                  } @else {
                    <span class="muted">—</span>
                  }
                </td>
                <td class="actions">
                  <button class="btn btn-icon" (click)="openEditModal(company); $event.stopPropagation()" [attr.aria-label]="'Edit ' + company.name" title="Edit">
                    <span class="material-icons">edit</span>
                  </button>
                  <a class="btn btn-icon" [routerLink]="['/sites']" [queryParams]="{companyId: company.id}" [attr.aria-label]="'View sites for ' + company.name" title="View sites" (click)="$event.stopPropagation()">
                    <span class="material-icons">location_on</span>
                  </a>
                  <a class="btn btn-icon" [routerLink]="['/barycenter']" [queryParams]="{companyId: company.id}" [attr.aria-label]="'Calculate barycenter for ' + company.name" title="Calculate barycenter" (click)="$event.stopPropagation()">
                    <span class="material-icons">my_location</span>
                  </a>
                  <button class="btn btn-icon" (click)="confirmDelete(company); $event.stopPropagation()" [attr.aria-label]="'Delete ' + company.name" title="Delete" style="color: var(--color-danger-600);">
                    <span class="material-icons">delete_outline</span>
                  </button>
                </td>
              </tr>
            }
          </tbody>
        </table>

        <!-- Pagination -->
        <div class="pagination-bar">
          <span>Showing {{ pageStart() }}–{{ pageEnd() }} of {{ filteredCompanies().length }} companies</span>
          <div class="pagination-controls">
            <button class="page-btn" (click)="prevPage()" [disabled]="page() === 1" aria-label="Previous page">
              <span class="material-icons" style="font-size:18px;">chevron_left</span>
            </button>
            @for (p of pageNumbers(); track p) {
              <button class="page-btn" [class.active]="p === page()" (click)="page.set(p)" [attr.aria-label]="'Page ' + p" [attr.aria-current]="p === page() ? 'page' : null">
                {{ p }}
              </button>
            }
            <button class="page-btn" (click)="nextPage()" [disabled]="page() === totalPages()" aria-label="Next page">
              <span class="material-icons" style="font-size:18px;">chevron_right</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create / Edit Modal -->
    @if (showModal()) {
      <div class="modal-overlay" role="dialog" aria-modal="true" [attr.aria-label]="editTarget() ? 'Edit company' : 'Create company'" (click)="closeModal()">
        <div class="modal" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>{{ editTarget() ? 'Edit Company' : 'New Company' }}</h2>
            <button class="btn btn-icon" (click)="closeModal()" aria-label="Close dialog">
              <span class="material-icons">close</span>
            </button>
          </div>

          <form [formGroup]="form" (ngSubmit)="submitForm()" novalidate>
            <div class="modal-body">
              <div class="grid-2">
                <div class="form-group">
                  <label class="form-label" for="cName">Company Name *</label>
                  <input id="cName" class="form-control" formControlName="name" [class.error]="fieldError('name')" autocomplete="organization" />
                  @if (fieldError('name')) {
                    <p class="form-error" role="alert"><span class="material-icons">error_outline</span> Name is required</p>
                  }
                </div>

                <div class="form-group">
                  <label class="form-label" for="cType">Type *</label>
                  <select id="cType" class="form-control" formControlName="type">
                    <option value="SHIPPER">Shipper</option>
                    <option value="CARRIER">Carrier</option>
                    <option value="BOTH">Both</option>
                  </select>
                </div>
              </div>

              <div class="grid-2" style="margin-top: var(--space-4);">
                <div class="form-group">
                  <label class="form-label" for="cTaxId">Tax ID</label>
                  <input id="cTaxId" class="form-control" formControlName="taxId" autocomplete="off" />
                </div>

                @if (editTarget()) {
                  <div class="form-group">
                    <label class="form-label" for="cStatus">Status</label>
                    <select id="cStatus" class="form-control" formControlName="status">
                      <option value="ACTIVE">Active</option>
                      <option value="INACTIVE">Inactive</option>
                      <option value="PENDING">Pending</option>
                    </select>
                  </div>
                }
              </div>

              <hr class="divider" />
              <p class="section-title">Contact Information</p>

              <div class="grid-2">
                <div class="form-group">
                  <label class="form-label" for="cContact">Contact Name</label>
                  <input id="cContact" class="form-control" formControlName="contactName" autocomplete="name" />
                </div>
                <div class="form-group">
                  <label class="form-label" for="cEmail">Contact Email</label>
                  <input id="cEmail" class="form-control" type="email" formControlName="contactEmail" [class.error]="fieldError('contactEmail')" autocomplete="email" />
                  @if (fieldError('contactEmail')) {
                    <p class="form-error" role="alert"><span class="material-icons">error_outline</span> Enter a valid email</p>
                  }
                </div>
              </div>

              <div class="form-group" style="margin-top: var(--space-4);">
                <label class="form-label" for="cPhone">Contact Phone</label>
                <input id="cPhone" class="form-control" formControlName="contactPhone" autocomplete="tel" style="max-width: 280px;" />
              </div>

              <div class="form-group" style="margin-top: var(--space-4);">
                <label class="form-label" for="cNotes">Notes</label>
                <textarea id="cNotes" class="form-control" formControlName="notes" rows="3" placeholder="Optional notes…"></textarea>
              </div>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="form.invalid">
                <span class="material-icons">{{ editTarget() ? 'save' : 'add' }}</span>
                {{ editTarget() ? 'Save Changes' : 'Create Company' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    }

    <!-- Delete confirmation modal -->
    @if (deleteTarget()) {
      <div class="modal-overlay" role="alertdialog" aria-modal="true" aria-label="Confirm deletion">
        <div class="modal modal-sm">
          <div class="modal-header">
            <h2>Delete Company</h2>
            <button class="btn btn-icon" (click)="deleteTarget.set(null)" aria-label="Cancel">
              <span class="material-icons">close</span>
            </button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete <strong>{{ deleteTarget()!.name }}</strong>?</p>
            <p style="margin-top: var(--space-3); color: var(--color-danger-600); font-size: var(--font-size-small);">
              <span class="material-icons" style="font-size:14px; vertical-align:middle;">warning</span>
              This will also delete all {{ deleteTarget()!.consumptionSiteCount }} associated consumption sites and cannot be undone.
            </p>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" (click)="deleteTarget.set(null)">Cancel</button>
            <button class="btn btn-danger" (click)="executeDelete()">
              <span class="material-icons">delete</span>
              Delete
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .filters-bar .card-body { padding: var(--space-3) var(--space-5) !important; }

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

    .filter-select { max-width: 160px; }

    .company-name-cell {
      display: flex;
      align-items: center;
      gap: var(--space-3);
    }

    .company-initial {
      width: 34px;
      height: 34px;
      border-radius: var(--radius-full);
      color: #fff;
      font-weight: 700;
      font-size: 15px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .name-primary { font-weight: 500; }
    .name-secondary { font-size: 11px; color: var(--color-neutral-500); margin-top: 1px; }

    .site-count-link {
      color: var(--color-primary-600);
      font-weight: 600;
      text-decoration: none;

      &:hover { text-decoration: underline; }
    }

    .muted { color: var(--color-neutral-400); }
  `]
})
export class CompaniesComponent {
  dataService = inject(DataService);
  private toast   = inject(ToastService);
  private fb      = inject(FormBuilder);

  // ── UI state ──────────────────────────────────────────────────────────────
  filter: CompanyFilter = { search: '', status: 'ALL', type: 'ALL' };
  sortCol = 'name';
  sortDir: 'asc' | 'desc' = 'asc';
  page       = signal(1);
  pageSize   = 10;
  selectedId = signal<string | null>(null);

  showModal   = signal(false);
  editTarget  = signal<Company | null>(null);
  deleteTarget = signal<Company | null>(null);

  form = this.fb.group({
    name:         ['', [Validators.required, Validators.minLength(2)]],
    type:         ['SHIPPER' as CompanyType, Validators.required],
    taxId:        [''],
    status:       ['ACTIVE' as CompanyStatus],
    contactName:  [''],
    contactEmail: ['', Validators.email],
    contactPhone: [''],
    notes:        [''],
  });

  // ── Computed data ──────────────────────────────────────────────────────────

  filteredCompanies = computed(() => {
    let list = [...this.dataService.companies()];
    const f  = this.filter;

    if (f.search) {
      const q = f.search.toLowerCase();
      list = list.filter(c =>
        c.name.toLowerCase().includes(q) ||
        (c.taxId ?? '').toLowerCase().includes(q) ||
        (c.contactName ?? '').toLowerCase().includes(q)
      );
    }
    if (f.status !== 'ALL') list = list.filter(c => c.status === f.status);
    if (f.type   !== 'ALL') list = list.filter(c => c.type   === f.type);

    list.sort((a, b) => {
      const av = (a as any)[this.sortCol];
      const bv = (b as any)[this.sortCol];
      const cmp = typeof av === 'number' ? av - bv : String(av).localeCompare(String(bv));
      return this.sortDir === 'asc' ? cmp : -cmp;
    });

    return list;
  });

  totalPages  = computed(() => Math.ceil(this.filteredCompanies().length / this.pageSize) || 1);
  pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i + 1));
  pageStart   = computed(() => (this.page() - 1) * this.pageSize + 1);
  pageEnd     = computed(() => Math.min(this.page() * this.pageSize, this.filteredCompanies().length));

  pagedCompanies = computed(() => {
    const start = (this.page() - 1) * this.pageSize;
    return this.filteredCompanies().slice(start, start + this.pageSize);
  });

  // ── Sorting ────────────────────────────────────────────────────────────────

  sort(col: string): void {
    if (this.sortCol === col) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortCol = col;
      this.sortDir = 'asc';
    }
    this.page.set(1);
  }

  // ── Pagination ─────────────────────────────────────────────────────────────

  prevPage(): void { if (this.page() > 1) this.page.update(p => p - 1); }
  nextPage(): void { if (this.page() < this.totalPages()) this.page.update(p => p + 1); }

  clearFilters(): void {
    this.filter = { search: '', status: 'ALL', type: 'ALL' };
    this.page.set(1);
  }

  // ── Modal lifecycle ────────────────────────────────────────────────────────

  openCreateModal(): void {
    this.editTarget.set(null);
    this.form.reset({ type: 'SHIPPER', status: 'ACTIVE' });
    this.showModal.set(true);
  }

  openEditModal(company: Company): void {
    this.editTarget.set(company);
    this.form.patchValue({
      name: company.name,
      type: company.type,
      taxId: company.taxId ?? '',
      status: company.status,
      contactName: company.contactName ?? '',
      contactEmail: company.contactEmail ?? '',
      contactPhone: company.contactPhone ?? '',
      notes: company.notes ?? '',
    });
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.editTarget.set(null);
  }

  submitForm(): void {
    if (this.form.invalid) return;
    const val = this.form.getRawValue();
    if (this.editTarget()) {
      const payload: UpdateCompanyPayload = {
        name: val.name!,
        type: val.type as CompanyType,
        taxId: val.taxId || undefined,
        status: val.status as CompanyStatus,
        contactName: val.contactName || undefined,
        contactEmail: val.contactEmail || undefined,
        contactPhone: val.contactPhone || undefined,
        notes: val.notes || undefined,
      };
      this.dataService.updateCompany(this.editTarget()!.id, payload);
      this.toast.success('Company updated', `${val.name} has been saved.`);
    } else {
      const payload: CreateCompanyPayload = {
        name: val.name!,
        type: val.type as CompanyType,
        taxId: val.taxId || undefined,
        contactName: val.contactName || undefined,
        contactEmail: val.contactEmail || undefined,
        contactPhone: val.contactPhone || undefined,
        notes: val.notes || undefined,
      };
      this.dataService.createCompany(payload);
      this.toast.success('Company created', `${val.name} has been added.`);
    }
    this.closeModal();
  }

  confirmDelete(company: Company): void {
    this.deleteTarget.set(company);
  }

  executeDelete(): void {
    const c = this.deleteTarget();
    if (!c) return;
    this.dataService.deleteCompany(c.id);
    this.toast.success('Company deleted', `${c.name} has been removed.`);
    this.deleteTarget.set(null);
  }

  // ── Helpers ────────────────────────────────────────────────────────────────

  fieldError(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && (ctrl.dirty || ctrl.touched));
  }

  statusClass(status: CompanyStatus): string {
    const map: Record<CompanyStatus, string> = {
      ACTIVE:   'badge badge-success',
      INACTIVE: 'badge badge-neutral',
      PENDING:  'badge badge-warning',
    };
    return map[status];
  }

  formatTons(t: number): string {
    return t >= 1000 ? `${(t / 1000).toFixed(1)}k t` : `${t.toFixed(0)} t`;
  }

  private COLORS = ['#1F4E79','#2E6DA4','#0891B2','#16A34A','#D97706','#7C3AED'];
  companyColor(name: string): string {
    return this.COLORS[name.charCodeAt(0) % this.COLORS.length];
  }
}