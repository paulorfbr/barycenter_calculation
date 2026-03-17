import {
  Component, inject, signal, computed,
  AfterViewInit, OnDestroy, ElementRef, ViewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DataService } from '../../core/services/data.service';
import * as L from 'leaflet';

delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl:       'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl:     'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="map-view-layout page-enter">
      <!-- Overlay controls panel -->
      <div class="map-overlay-panel card">
        <div class="overlay-header">
          <span class="material-icons" style="color: var(--color-primary-700);">map</span>
          <h2 style="font-size: var(--font-size-h3); font-weight: 600;">Map View</h2>
        </div>

        <div class="overlay-section">
          <label class="form-label" for="mvCompany">Company Filter</label>
          <select id="mvCompany" class="form-control" [(ngModel)]="selectedCompanyId" (ngModelChange)="refreshMap()">
            <option value="ALL">All Companies</option>
            @for (c of dataService.companies(); track c.id) {
              <option [value]="c.id">{{ c.name }}</option>
            }
          </select>
        </div>

        <div class="overlay-section">
          <label class="form-label">Layers</label>
          <label class="toggle-row">
            <input type="checkbox" [(ngModel)]="showSites" (ngModelChange)="refreshMap()" />
            <span class="toggle-label">
              <span class="legend-dot" style="background: var(--color-warning-600);"></span>
              Consumption Sites
            </span>
          </label>
          <label class="toggle-row">
            <input type="checkbox" [(ngModel)]="showResults" (ngModelChange)="refreshMap()" />
            <span class="toggle-label">
              <span class="legend-dot" style="background: var(--color-primary-700); border: 2px solid white; box-shadow: 0 1px 4px rgba(0,0,0,0.3);"></span>
              Barycenter Results
            </span>
          </label>
          <label class="toggle-row">
            <input type="checkbox" [(ngModel)]="showConnections" (ngModelChange)="refreshMap()" />
            <span class="toggle-label">
              <span style="display:inline-block; width:20px; height:2px; background: var(--color-primary-400); border-radius: 1px; vertical-align: middle;"></span>
              Connection Lines
            </span>
          </label>
        </div>

        <div class="overlay-section">
          <label class="form-label">Stats</label>
          <div class="overlay-stat">
            <span>Sites visible</span>
            <strong>{{ visibleSiteCount() }}</strong>
          </div>
          <div class="overlay-stat">
            <span>Results shown</span>
            <strong>{{ visibleResultCount() }}</strong>
          </div>
        </div>

        <a routerLink="/barycenter" class="btn btn-primary btn-sm" style="margin-top: var(--space-2);">
          <span class="material-icons">my_location</span>
          Go to Calculator
        </a>
      </div>

      <!-- Full-screen map -->
      <div class="full-map" #mapEl aria-label="Full map view of all consumption sites and barycenter results" role="img"></div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      height: 100%;
    }

    .map-view-layout {
      position: relative;
      height: calc(100vh - var(--topbar-height) - 2px);
      overflow: hidden;
    }

    .full-map {
      width: 100%;
      height: 100%;
      z-index: 0;
    }

    .map-overlay-panel {
      position: absolute;
      top: var(--space-4);
      left: var(--space-4);
      z-index: 500;
      width: 240px;
      padding: var(--space-4);
      display: flex;
      flex-direction: column;
      gap: var(--space-4);
      max-height: calc(100% - var(--space-8));
      overflow-y: auto;
    }

    .overlay-header {
      display: flex;
      align-items: center;
      gap: var(--space-2);
    }

    .overlay-section {
      display: flex;
      flex-direction: column;
      gap: var(--space-2);
    }

    .toggle-row {
      display: flex;
      align-items: center;
      gap: var(--space-2);
      cursor: pointer;
      font-size: var(--font-size-small);
      color: var(--color-neutral-700);
      padding: var(--space-1) 0;

      input[type="checkbox"] {
        width: 16px;
        height: 16px;
        accent-color: var(--color-primary-600);
        flex-shrink: 0;
      }
    }

    .toggle-label {
      display: flex;
      align-items: center;
      gap: var(--space-2);
    }

    .legend-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      display: inline-block;
      flex-shrink: 0;
    }

    .overlay-stat {
      display: flex;
      justify-content: space-between;
      font-size: var(--font-size-small);
      color: var(--color-neutral-700);
      padding: 2px 0;

      strong { color: var(--color-neutral-900); }
    }
  `]
})
export class MapViewComponent implements AfterViewInit, OnDestroy {
  @ViewChild('mapEl', { static: false }) mapEl!: ElementRef<HTMLDivElement>;

  dataService = inject(DataService);

  selectedCompanyId = 'ALL';
  showSites       = true;
  showResults     = true;
  showConnections = true;

  private map?: L.Map;
  private layers: L.Layer[] = [];

  visibleSiteCount = computed(() => {
    const sites = this.dataService.activeSites();
    return this.selectedCompanyId === 'ALL'
      ? sites.length
      : sites.filter(s => s.companyId === this.selectedCompanyId).length;
  });

  visibleResultCount = computed(() => {
    const results = this.dataService.results();
    return this.selectedCompanyId === 'ALL'
      ? results.length
      : results.filter(r => r.companyId === this.selectedCompanyId).length;
  });

  ngAfterViewInit(): void {
    setTimeout(() => this.initMap(), 100);
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private initMap(): void {
    if (!this.mapEl?.nativeElement || this.map) return;

    this.map = L.map(this.mapEl.nativeElement, {
      center: [39.5, -98.35],
      zoom: 4,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 18,
    }).addTo(this.map);

    this.refreshMap();
  }

  refreshMap(): void {
    if (!this.map) return;
    this.layers.forEach(l => l.remove());
    this.layers = [];

    const allSites   = this.dataService.sites();
    const allResults = this.dataService.results();

    const sites = this.selectedCompanyId === 'ALL'
      ? allSites
      : allSites.filter(s => s.companyId === this.selectedCompanyId);

    const results = this.selectedCompanyId === 'ALL'
      ? allResults
      : allResults.filter(r => r.companyId === this.selectedCompanyId);

    const COMPANY_COLORS = ['#1F4E79','#2E6DA4','#0891B2','#16A34A','#D97706','#7C3AED','#be185d','#b45309'];
    const companyIds = [...new Set(allSites.map(s => s.companyId))];
    const colorMap: Record<string, string> = {};
    companyIds.forEach((id, i) => { colorMap[id] = COMPANY_COLORS[i % COMPANY_COLORS.length]; });

    if (this.showSites) {
      for (const site of sites) {
        const color = colorMap[site.companyId] ?? '#D97706';
        const m = L.circleMarker([site.latitude, site.longitude], {
          radius: 7 + Math.sqrt(site.weightTons / 200),
          fillColor: site.status === 'ACTIVE' ? color : '#D1D5DB',
          color: '#fff', weight: 2, opacity: 1,
          fillOpacity: site.status === 'ACTIVE' ? 0.85 : 0.4,
        }).bindPopup(`
          <strong>${site.name}</strong><br/>
          <small style="color:#6B7280;">${this.dataService.getCompanyById(site.companyId)?.name ?? ''}</small><br/>
          <code style="font-size:11px;">${site.formattedCoordinate}</code><br/>
          <b>${site.weightFormatted}</b>
        `).addTo(this.map!);
        this.layers.push(m);
      }
    }

    if (this.showResults) {
      for (const r of results) {
        const icon = L.divIcon({
          html: `<div style="
            width:18px; height:18px;
            background:#1F4E79;
            border:3px solid white;
            border-radius:50%;
            box-shadow: 0 2px 6px rgba(0,0,0,0.35);
          "></div>`,
          className: '',
          iconSize: [18, 18],
          iconAnchor: [9, 9],
        });
        const m = L.marker([r.optimalLatitude, r.optimalLongitude], { icon })
          .bindPopup(`
            <strong style="color:#1F4E79;">Optimal Center</strong><br/>
            <small>${this.dataService.getCompanyById(r.companyId)?.name ?? ''}</small><br/>
            <code style="font-size:11px;">${r.formattedCoordinate}</code><br/>
            Algorithm: ${r.algorithmDescription === 'weiszfeld-iterative' ? 'Weiszfeld' : 'Simple'}<br/>
            Status: <b>${r.status}</b>
          `).addTo(this.map!);
        this.layers.push(m);

        if (this.showConnections) {
          for (const s of r.inputSites) {
            const line = L.polyline([
              [s.latitude, s.longitude],
              [r.optimalLatitude, r.optimalLongitude]
            ], {
              color: '#5B9BD5', weight: 1, opacity: 0.25, dashArray: '4 6',
            }).addTo(this.map!);
            this.layers.push(line);
          }
        }
      }
    }
  }
}
