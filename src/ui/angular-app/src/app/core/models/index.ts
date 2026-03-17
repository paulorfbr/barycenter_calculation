// =============================================================================
// DOMAIN MODELS — mirrors the Java domain model exactly
// =============================================================================

// ---------------------------------------------------------------------------
// GeoCoordinate (mirrors GeoCoordinate.java record)
// ---------------------------------------------------------------------------
export interface GeoCoordinate {
  latitude: number;
  longitude: number;
}

// ---------------------------------------------------------------------------
// ConsumptionSite (mirrors ConsumptionSiteDto.java)
// ---------------------------------------------------------------------------
export type SiteStatus = 'ACTIVE' | 'INACTIVE';

export interface ConsumptionSite {
  id: string;
  companyId: string;
  name: string;
  description?: string;
  latitude: number;
  longitude: number;
  formattedCoordinate: string;
  weightTons: number;
  weightFormatted: string;
  address?: string;
  city?: string;
  country?: string;
  status: SiteStatus;
  createdAt: string;
}

export interface CreateSitePayload {
  companyId: string;
  name: string;
  description?: string;
  latitude: number;
  longitude: number;
  weightTons: number;
  address?: string;
  city?: string;
  country?: string;
}

export interface UpdateSitePayload {
  name?: string;
  description?: string;
  latitude?: number;
  longitude?: number;
  weightTons?: number;
  address?: string;
  city?: string;
  country?: string;
  status?: SiteStatus;
}

// ---------------------------------------------------------------------------
// Company (mirrors Company.java + CompanyDto)
// ---------------------------------------------------------------------------
export type CompanyStatus = 'ACTIVE' | 'INACTIVE' | 'PENDING';
export type CompanyType   = 'SHIPPER' | 'CARRIER' | 'BOTH';

export interface Company {
  id: string;
  name: string;
  type: CompanyType;
  status: CompanyStatus;
  taxId?: string;
  contactName?: string;
  contactEmail?: string;
  contactPhone?: string;
  notes?: string;
  consumptionSiteCount: number;
  totalTrafficTons: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCompanyPayload {
  name: string;
  type: CompanyType;
  taxId?: string;
  contactName?: string;
  contactEmail?: string;
  contactPhone?: string;
  notes?: string;
}

export interface UpdateCompanyPayload extends Partial<CreateCompanyPayload> {
  status?: CompanyStatus;
}

// ---------------------------------------------------------------------------
// BarycentreResult (mirrors BarycentreResultDto.java)
// ---------------------------------------------------------------------------
export type AlgorithmType = 'weighted-barycenter' | 'weiszfeld-iterative';
export type ResultStatus  = 'CANDIDATE' | 'APPROVED' | 'REJECTED' | 'CONFIRMED';

export interface BarycentreInputSite {
  siteId: string;
  siteName: string;
  latitude: number;
  longitude: number;
  weightTons: number;
  distanceToOptimalKm: number;
}

export interface BarycentreResult {
  logisticsCenterId: string;
  companyId: string;
  optimalLatitude: number;
  optimalLongitude: number;
  formattedCoordinate: string;
  algorithmDescription: AlgorithmType;
  iterationCount: number;
  convergenceErrorKm: number;
  totalWeightedTons: number;
  inputSiteCount: number;
  inputSites: BarycentreInputSite[];
  status: ResultStatus;
  calculatedAt?: string;
}

export interface CalculateRequest {
  companyId: string;
  algorithm: AlgorithmType;
  maxIterations?: number;
  toleranceKm?: number;
}

// ---------------------------------------------------------------------------
// DashboardSummary (mirrors DashboardSummaryDto.java)
// ---------------------------------------------------------------------------
export interface ActivityItem {
  timeLabel: string;
  message: string;
}

export interface OverdueShipment {
  shipmentId: string;
  companyName: string;
  origin: string;
  destination: string;
  daysOverdue: string;
}

export interface DashboardSummary {
  totalCompanies: number;
  activeShipments: number;
  totalLocations: number;
  onTimeRatePercent: number;
  companiesTrend: string;
  shipmentsTrend: string;
  locationsTrend: string;
  onTimeTrend: string;
  totalConsumptionSites: number;
  totalTrafficTons: number;
  logisticsCenterCandidates: number;
  recentActivity: ActivityItem[];
  overdueShipments: OverdueShipment[];
}

// ---------------------------------------------------------------------------
// Toast notification model
// ---------------------------------------------------------------------------
export type ToastType = 'success' | 'warning' | 'danger' | 'info';

export interface Toast {
  id: string;
  type: ToastType;
  title: string;
  message?: string;
  duration?: number;
}

// ---------------------------------------------------------------------------
// Table sort state
// ---------------------------------------------------------------------------
export type SortDirection = 'asc' | 'desc' | null;

export interface SortState {
  column: string;
  direction: SortDirection;
}

// ---------------------------------------------------------------------------
// Filter state for site table
// ---------------------------------------------------------------------------
export interface SiteFilter {
  search: string;
  status: SiteStatus | 'ALL';
  companyId: string | 'ALL';
}

export interface CompanyFilter {
  search: string;
  status: CompanyStatus | 'ALL';
  type: CompanyType | 'ALL';
}
