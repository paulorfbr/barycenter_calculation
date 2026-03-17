import {
  __spreadProps,
  __spreadValues,
  computed,
  signal,
  ɵɵdefineInjectable
} from "./chunk-WAAQAFSM.js";

// src/app/core/services/data.service.ts
var _idCounter = 1e3;
function uid(prefix = "id") {
  return `${prefix}-${(++_idCounter).toString().padStart(3, "0")}`;
}
function fmtCoord(lat, lon) {
  return `${lat.toFixed(4)}, ${lon.toFixed(4)}`;
}
function fmtTons(t) {
  return t >= 1e3 ? `${(t / 1e3).toFixed(1)}k t` : `${t.toFixed(0)} t`;
}
function now() {
  return (/* @__PURE__ */ new Date()).toISOString();
}
var DataService = class _DataService {
  // -------------------------------------------------------------------------
  // Constructor — seed sample data matching SampleDataSeeder.java
  // -------------------------------------------------------------------------
  constructor() {
    this._companies = signal([]);
    this._sites = signal([]);
    this._results = signal([]);
    this.companies = this._companies.asReadonly();
    this.sites = this._sites.asReadonly();
    this.results = this._results.asReadonly();
    this.activeCompanies = computed(() => this._companies().filter((c) => c.status === "ACTIVE"));
    this.activeSites = computed(() => this._sites().filter((s) => s.status === "ACTIVE"));
    this.dashboardSummary = computed(() => {
      const companies = this._companies();
      const sites = this._sites();
      const results = this._results();
      const totalTons = sites.filter((s) => s.status === "ACTIVE").reduce((sum, s) => sum + s.weightTons, 0);
      return {
        totalCompanies: companies.length,
        activeShipments: 142,
        totalLocations: sites.length,
        onTimeRatePercent: 94.2,
        companiesTrend: `+${companies.filter((c) => c.status === "ACTIVE").length} active`,
        shipmentsTrend: "+8 this week",
        locationsTrend: `${sites.filter((s) => s.status === "ACTIVE").length} active sites`,
        onTimeTrend: "+2.1% vs last month",
        totalConsumptionSites: sites.length,
        totalTrafficTons: totalTons,
        logisticsCenterCandidates: results.filter((r) => r.status === "CANDIDATE").length,
        recentActivity: [
          { timeLabel: "2 min ago", message: "Barycenter recalculated for Acme Corp" },
          { timeLabel: "14 min ago", message: 'New site "West Hub" added to Global Freight' },
          { timeLabel: "1 hr ago", message: "FastLog Inc site weights updated" },
          { timeLabel: "3 hr ago", message: "Barycenter result approved for FastLog Inc" },
          { timeLabel: "Yesterday", message: 'Company "Metro Moves" created' }
        ],
        overdueShipments: [
          { shipmentId: "SHP-8821", companyName: "Acme Corp", origin: "Los Angeles", destination: "New York", daysOverdue: "3 days" },
          { shipmentId: "SHP-8799", companyName: "Global Freight", origin: "Chicago", destination: "Seattle", daysOverdue: "5 days" }
        ]
      };
    });
    this.seedSampleData();
  }
  // -------------------------------------------------------------------------
  // Company CRUD
  // -------------------------------------------------------------------------
  createCompany(payload) {
    const company = {
      id: uid("CMP"),
      name: payload.name.trim(),
      type: payload.type,
      status: "ACTIVE",
      taxId: payload.taxId,
      contactName: payload.contactName,
      contactEmail: payload.contactEmail,
      contactPhone: payload.contactPhone,
      notes: payload.notes,
      consumptionSiteCount: 0,
      totalTrafficTons: 0,
      createdAt: now(),
      updatedAt: now()
    };
    this._companies.update((list) => [...list, company]);
    return company;
  }
  updateCompany(id, payload) {
    let updated = null;
    this._companies.update((list) => list.map((c) => {
      if (c.id !== id)
        return c;
      updated = __spreadProps(__spreadValues(__spreadValues({}, c), payload), { updatedAt: now() });
      return updated;
    }));
    this.recomputeCompanyStats(id);
    return updated;
  }
  deleteCompany(id) {
    this._companies.update((list) => list.filter((c) => c.id !== id));
    this._sites.update((list) => list.filter((s) => s.companyId !== id));
    this._results.update((list) => list.filter((r) => r.companyId !== id));
  }
  getCompanyById(id) {
    return this._companies().find((c) => c.id === id);
  }
  getSitesByCompany(companyId) {
    return this._sites().filter((s) => s.companyId === companyId);
  }
  getResultsByCompany(companyId) {
    return this._results().filter((r) => r.companyId === companyId);
  }
  // -------------------------------------------------------------------------
  // Site CRUD
  // -------------------------------------------------------------------------
  createSite(payload) {
    const site = {
      id: uid("STE"),
      companyId: payload.companyId,
      name: payload.name.trim(),
      description: payload.description,
      latitude: payload.latitude,
      longitude: payload.longitude,
      formattedCoordinate: fmtCoord(payload.latitude, payload.longitude),
      weightTons: payload.weightTons,
      weightFormatted: fmtTons(payload.weightTons),
      address: payload.address,
      city: payload.city,
      country: payload.country,
      status: "ACTIVE",
      createdAt: now()
    };
    this._sites.update((list) => [...list, site]);
    this.recomputeCompanyStats(payload.companyId);
    return site;
  }
  updateSite(id, payload) {
    let updated = null;
    this._sites.update((list) => list.map((s) => {
      if (s.id !== id)
        return s;
      const lat = payload.latitude ?? s.latitude;
      const lon = payload.longitude ?? s.longitude;
      const w = payload.weightTons ?? s.weightTons;
      updated = __spreadProps(__spreadValues(__spreadValues({}, s), payload), {
        latitude: lat,
        longitude: lon,
        weightTons: w,
        formattedCoordinate: fmtCoord(lat, lon),
        weightFormatted: fmtTons(w)
      });
      return updated;
    }));
    if (updated)
      this.recomputeCompanyStats(updated.companyId);
    return updated;
  }
  deleteSite(id) {
    const site = this._sites().find((s) => s.id === id);
    this._sites.update((list) => list.filter((s) => s.id !== id));
    if (site)
      this.recomputeCompanyStats(site.companyId);
  }
  getSiteById(id) {
    return this._sites().find((s) => s.id === id);
  }
  // -------------------------------------------------------------------------
  // Barycenter calculation (mirrors BarycentreCalculationEngine.java)
  // -------------------------------------------------------------------------
  calculate(request) {
    const sites = this._sites().filter((s) => s.companyId === request.companyId && s.status === "ACTIVE");
    if (sites.length < 2) {
      throw new Error("At least 2 active sites are required for barycenter calculation.");
    }
    const lats = sites.map((s) => s.latitude);
    const lons = sites.map((s) => s.longitude);
    const weights = sites.map((s) => s.weightTons);
    let optLat;
    let optLon;
    let iterCount;
    let convergenceErrorKm;
    if (request.algorithm === "weiszfeld-iterative") {
      const r = this.weiszfeldIterate(lats, lons, weights, request.maxIterations ?? 1e3, request.toleranceKm ?? 0.01);
      optLat = r.lat;
      optLon = r.lon;
      iterCount = r.iterations;
      convergenceErrorKm = r.error;
    } else {
      const r = this.weightedBarycentre(lats, lons, weights);
      optLat = r.lat;
      optLon = r.lon;
      iterCount = 1;
      convergenceErrorKm = 0;
    }
    const totalTons = weights.reduce((s, w) => s + w, 0);
    const inputSites = sites.map((s) => ({
      siteId: s.id,
      siteName: s.name,
      latitude: s.latitude,
      longitude: s.longitude,
      weightTons: s.weightTons,
      distanceToOptimalKm: haversineKm(s.latitude, s.longitude, optLat, optLon)
    }));
    const result = {
      logisticsCenterId: uid("LC"),
      companyId: request.companyId,
      optimalLatitude: optLat,
      optimalLongitude: optLon,
      formattedCoordinate: fmtCoord(optLat, optLon),
      algorithmDescription: request.algorithm,
      iterationCount: iterCount,
      convergenceErrorKm,
      totalWeightedTons: totalTons,
      inputSiteCount: sites.length,
      inputSites,
      status: "CANDIDATE",
      calculatedAt: now()
    };
    this._results.update((list) => [result, ...list]);
    return result;
  }
  updateResultStatus(id, status) {
    this._results.update((list) => list.map((r) => r.logisticsCenterId === id ? __spreadProps(__spreadValues({}, r), { status }) : r));
  }
  // -------------------------------------------------------------------------
  // Algorithm implementations (TypeScript mirrors of Java engine)
  // -------------------------------------------------------------------------
  weightedBarycentre(lats, lons, weights) {
    let sumW = 0, sumLat = 0, sumLon = 0;
    for (let i = 0; i < lats.length; i++) {
      sumW += weights[i];
      sumLat += weights[i] * lats[i];
      sumLon += weights[i] * lons[i];
    }
    if (sumW <= 0) {
      const ones = new Array(lats.length).fill(1);
      return this.weightedBarycentre(lats, lons, ones);
    }
    return { lat: sumLat / sumW, lon: sumLon / sumW };
  }
  weiszfeldIterate(lats, lons, weights, maxIter, toleranceKm) {
    let { lat, lon } = this.weightedBarycentre(lats, lons, weights);
    const EPSILON = 1e-6;
    let iter = 0;
    let error = Number.MAX_VALUE;
    while (iter < maxIter && error > toleranceKm) {
      let numLat = 0, numLon = 0, den = 0;
      for (let i = 0; i < lats.length; i++) {
        const d = Math.max(haversineKm(lat, lon, lats[i], lons[i]), EPSILON);
        const wd = weights[i] / d;
        numLat += wd * lats[i];
        numLon += wd * lons[i];
        den += wd;
      }
      if (den <= 0)
        break;
      const nextLat = numLat / den;
      const nextLon = numLon / den;
      error = haversineKm(lat, lon, nextLat, nextLon);
      lat = nextLat;
      lon = nextLon;
      iter++;
    }
    return { lat, lon, iterations: iter, error };
  }
  // -------------------------------------------------------------------------
  // Internal helpers
  // -------------------------------------------------------------------------
  recomputeCompanyStats(companyId) {
    const companySites = this._sites().filter((s) => s.companyId === companyId && s.status === "ACTIVE");
    const allSites = this._sites().filter((s) => s.companyId === companyId);
    this._companies.update((list) => list.map((c) => {
      if (c.id !== companyId)
        return c;
      return __spreadProps(__spreadValues({}, c), {
        consumptionSiteCount: allSites.length,
        totalTrafficTons: companySites.reduce((sum, s) => sum + s.weightTons, 0),
        updatedAt: now()
      });
    }));
  }
  // -------------------------------------------------------------------------
  // Sample data (mirrors SampleDataSeeder.java)
  // -------------------------------------------------------------------------
  seedSampleData() {
    const acmeId = "CMP-001";
    const freightId = "CMP-002";
    const fastLogId = "CMP-003";
    const seedCompanies = [
      {
        id: acmeId,
        name: "Acme Corp",
        type: "SHIPPER",
        status: "ACTIVE",
        taxId: "12-3456789",
        contactName: "Jane Smith",
        contactEmail: "j@acme.com",
        contactPhone: "+1-555-0100",
        consumptionSiteCount: 4,
        totalTrafficTons: 1205,
        createdAt: "2024-01-15T09:00:00Z",
        updatedAt: "2024-03-01T14:30:00Z"
      },
      {
        id: freightId,
        name: "Global Freight",
        type: "CARRIER",
        status: "ACTIVE",
        taxId: "98-7654321",
        contactName: "Bob Johnson",
        contactEmail: "b@globalfreight.com",
        contactPhone: "+1-555-0200",
        consumptionSiteCount: 3,
        totalTrafficTons: 1720,
        createdAt: "2024-01-20T10:00:00Z",
        updatedAt: "2024-02-28T11:15:00Z"
      },
      {
        id: fastLogId,
        name: "FastLog Inc",
        type: "BOTH",
        status: "ACTIVE",
        taxId: "55-1122334",
        contactName: "Maria Garcia",
        contactEmail: "m@fastlog.com",
        contactPhone: "+1-555-0300",
        consumptionSiteCount: 3,
        totalTrafficTons: 1240,
        createdAt: "2024-02-01T08:00:00Z",
        updatedAt: "2024-03-05T09:45:00Z"
      },
      {
        id: "CMP-004",
        name: "BayArea Transport",
        type: "CARRIER",
        status: "ACTIVE",
        consumptionSiteCount: 0,
        totalTrafficTons: 0,
        createdAt: "2024-02-10T11:00:00Z",
        updatedAt: "2024-02-10T11:00:00Z"
      },
      {
        id: "CMP-005",
        name: "Pacific Shipping",
        type: "SHIPPER",
        status: "INACTIVE",
        consumptionSiteCount: 0,
        totalTrafficTons: 0,
        createdAt: "2024-01-05T09:00:00Z",
        updatedAt: "2024-02-20T16:00:00Z"
      },
      {
        id: "CMP-006",
        name: "Metro Moves",
        type: "BOTH",
        status: "ACTIVE",
        consumptionSiteCount: 0,
        totalTrafficTons: 0,
        createdAt: "2024-03-01T10:00:00Z",
        updatedAt: "2024-03-01T10:00:00Z"
      },
      {
        id: "CMP-007",
        name: "Apex Logistics",
        type: "CARRIER",
        status: "PENDING",
        consumptionSiteCount: 0,
        totalTrafficTons: 0,
        createdAt: "2024-03-08T14:00:00Z",
        updatedAt: "2024-03-08T14:00:00Z"
      },
      {
        id: "CMP-008",
        name: "SkyBridge Co",
        type: "SHIPPER",
        status: "ACTIVE",
        consumptionSiteCount: 0,
        totalTrafficTons: 0,
        createdAt: "2024-03-10T09:30:00Z",
        updatedAt: "2024-03-10T09:30:00Z"
      }
    ];
    const seedSites = [
      // Acme Corp (CMP-001)
      { id: "STE-001", companyId: acmeId, name: "Main Warehouse", description: "Primary origin", latitude: 34.0522, longitude: -118.2437, formattedCoordinate: "34.0522, -118.2437", weightTons: 450, weightFormatted: "450 t", address: "3200 S Figueroa St", city: "Los Angeles", country: "USA", status: "ACTIVE", createdAt: "2024-01-16T09:00:00Z" },
      { id: "STE-002", companyId: acmeId, name: "East Hub", description: "East Coast distribution", latitude: 40.7128, longitude: -74.006, formattedCoordinate: "40.7128, -74.0060", weightTons: 380, weightFormatted: "380 t", address: "1 World Trade Center", city: "New York", country: "USA", status: "ACTIVE", createdAt: "2024-01-16T09:05:00Z" },
      { id: "STE-003", companyId: acmeId, name: "Midwest Depot", description: "Central US depot", latitude: 41.8781, longitude: -87.6298, formattedCoordinate: "41.8781, -87.6298", weightTons: 210, weightFormatted: "210 t", address: "600 W Chicago Ave", city: "Chicago", country: "USA", status: "ACTIVE", createdAt: "2024-01-16T09:10:00Z" },
      { id: "STE-004", companyId: acmeId, name: "South Station", description: "Southeast distribution", latitude: 25.7617, longitude: -80.1918, formattedCoordinate: "25.7617, -80.1918", weightTons: 165, weightFormatted: "165 t", address: "100 N Biscayne Blvd", city: "Miami", country: "USA", status: "ACTIVE", createdAt: "2024-01-16T09:15:00Z" },
      // Global Freight (CMP-002)
      { id: "STE-005", companyId: freightId, name: "JFK Cargo Terminal", description: "Airport cargo hub", latitude: 40.6413, longitude: -73.7781, formattedCoordinate: "40.6413, -73.7781", weightTons: 820, weightFormatted: "820 t", address: "JFK Airport", city: "New York", country: "USA", status: "ACTIVE", createdAt: "2024-01-21T10:00:00Z" },
      { id: "STE-006", companyId: freightId, name: "ORD Air Hub", description: "Midwest air cargo", latitude: 41.9742, longitude: -87.9073, formattedCoordinate: "41.9742, -87.9073", weightTons: 560, weightFormatted: "560 t", address: "O'Hare International Airport", city: "Chicago", country: "USA", status: "ACTIVE", createdAt: "2024-01-21T10:05:00Z" },
      { id: "STE-007", companyId: freightId, name: "SEA Terminal", description: "Pacific Northwest", latitude: 47.4502, longitude: -122.3088, formattedCoordinate: "47.4502, -122.3088", weightTons: 340, weightFormatted: "340 t", address: "Seattle-Tacoma Airport", city: "Seattle", country: "USA", status: "ACTIVE", createdAt: "2024-01-21T10:10:00Z" },
      // FastLog Inc (CMP-003)
      { id: "STE-008", companyId: fastLogId, name: "LAX Cargo", description: "West Coast gateway", latitude: 33.9425, longitude: -118.4081, formattedCoordinate: "33.9425, -118.4081", weightTons: 610, weightFormatted: "610 t", address: "LAX Airport", city: "Los Angeles", country: "USA", status: "ACTIVE", createdAt: "2024-02-02T08:00:00Z" },
      { id: "STE-009", companyId: fastLogId, name: "DFW Depot", description: "Texas hub", latitude: 32.8998, longitude: -97.0403, formattedCoordinate: "32.8998, -97.0403", weightTons: 290, weightFormatted: "290 t", address: "DFW Airport", city: "Dallas", country: "USA", status: "ACTIVE", createdAt: "2024-02-02T08:05:00Z" },
      { id: "STE-010", companyId: fastLogId, name: "ATL Warehouse", description: "Southeast gateway", latitude: 33.6407, longitude: -84.4277, formattedCoordinate: "33.6407, -84.4277", weightTons: 340, weightFormatted: "340 t", address: "Hartsfield-Jackson Airport", city: "Atlanta", country: "USA", status: "ACTIVE", createdAt: "2024-02-02T08:10:00Z" }
    ];
    this._companies.set(seedCompanies);
    this._sites.set(seedSites);
  }
  static {
    this.\u0275fac = function DataService_Factory(t) {
      return new (t || _DataService)();
    };
  }
  static {
    this.\u0275prov = /* @__PURE__ */ \u0275\u0275defineInjectable({ token: _DataService, factory: _DataService.\u0275fac, providedIn: "root" });
  }
};
function haversineKm(lat1, lon1, lat2, lon2) {
  const R = 6371;
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a = Math.sin(dLat / 2) ** 2 + Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}
function toRad(deg) {
  return deg * Math.PI / 180;
}

export {
  DataService
};
//# sourceMappingURL=chunk-E6436NOF.js.map
