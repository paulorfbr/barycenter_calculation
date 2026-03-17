import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
    title: 'Dashboard — LogistiX',
  },
  {
    path: 'companies',
    loadComponent: () =>
      import('./features/companies/companies.component').then(m => m.CompaniesComponent),
    title: 'Companies — LogistiX',
  },
  {
    path: 'sites',
    loadComponent: () =>
      import('./features/sites/sites.component').then(m => m.SitesComponent),
    title: 'Consumption Sites — LogistiX',
  },
  {
    path: 'barycenter',
    loadComponent: () =>
      import('./features/barycenter/barycenter.component').then(m => m.BarycenterComponent),
    title: 'Barycenter Calculator — LogistiX',
  },
  {
    path: 'map',
    loadComponent: () =>
      import('./features/barycenter/map-view.component').then(m => m.MapViewComponent),
    title: 'Map View — LogistiX',
  },
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];
