package com.logistics.ui.viewmodel;

import com.logistics.domain.model.ConsumptionSite;
import com.logistics.domain.model.Location;
import com.logistics.domain.model.LogisticsCenter;
import com.logistics.ui.screens.LocationListScreen;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps {@link Location}, {@link ConsumptionSite}, and {@link LogisticsCenter}
 * domain objects to {@link LocationListScreen.LocationRow} view-model objects.
 *
 * Three source types map to the same view-model row because all three are
 * displayed in the unified Locations screen table and map view:
 *   - Location      : regular logistics locations (warehouses, hubs, airports...)
 *   - ConsumptionSite: sites with coordinates and traffic weight
 *   - LogisticsCenter: barycenter results shown as a special "Logistics Center" type
 */
public final class LocationViewModelMapper {

    private LocationViewModelMapper() {}

    // -------------------------------------------------------------------------
    // From Location
    // -------------------------------------------------------------------------

    public static LocationListScreen.LocationRow fromLocation(Location location,
                                                               String companyName) {
        return new LocationListScreen.LocationRow(
                location.getId(),
                location.getName(),
                formatLocationType(location.getType()),
                companyName,
                String.valueOf(location.getCoordinate().latitude()),
                String.valueOf(location.getCoordinate().longitude()),
                formatLocationStatus(location.getStatus())
        );
    }

    // -------------------------------------------------------------------------
    // From ConsumptionSite
    // -------------------------------------------------------------------------

    public static LocationListScreen.LocationRow fromConsumptionSite(ConsumptionSite site,
                                                                      String companyName) {
        return new LocationListScreen.LocationRow(
                site.getId(),
                site.getName() + " (" + site.getTrafficVolume().toDisplayString() + ")",
                "Consumption Site",
                companyName,
                String.valueOf(site.getCoordinate().latitude()),
                String.valueOf(site.getCoordinate().longitude()),
                formatSiteStatus(site.getStatus())
        );
    }

    // -------------------------------------------------------------------------
    // From LogisticsCenter (barycenter result)
    // -------------------------------------------------------------------------

    public static LocationListScreen.LocationRow fromLogisticsCenter(LogisticsCenter center,
                                                                      String companyName) {
        return new LocationListScreen.LocationRow(
                center.getId(),
                center.getName() + " [Optimal]",
                "Logistics Center",
                companyName,
                String.format("%.4f", center.getOptimalPosition().latitude()),
                String.format("%.4f", center.getOptimalPosition().longitude()),
                formatCenterStatus(center.getStatus())
        );
    }

    // -------------------------------------------------------------------------
    // Bulk helpers
    // -------------------------------------------------------------------------

    public static List<LocationListScreen.LocationRow> fromLocations(
            List<Location> locations,
            java.util.Map<String, String> companyNames) {
        return locations.stream()
                .map(l -> fromLocation(l, companyNames.getOrDefault(l.getCompanyId(), "Unknown")))
                .collect(Collectors.toList());
    }

    public static List<LocationListScreen.LocationRow> fromConsumptionSites(
            List<ConsumptionSite> sites,
            java.util.Map<String, String> companyNames) {
        return sites.stream()
                .map(s -> fromConsumptionSite(s, companyNames.getOrDefault(s.getCompanyId(), "Unknown")))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Formatting
    // -------------------------------------------------------------------------

    private static String formatLocationType(Location.Type type) {
        return switch (type) {
            case WAREHOUSE        -> "Warehouse";
            case HUB              -> "Hub";
            case AIRPORT          -> "Airport";
            case PORT             -> "Port";
            case DEPOT            -> "Depot";
            case LOGISTICS_CENTER -> "Logistics Center";
        };
    }

    private static String formatLocationStatus(Location.Status status) {
        return switch (status) {
            case ACTIVE      -> "Active";
            case INACTIVE    -> "Inactive";
            case MAINTENANCE -> "Maintenance";
        };
    }

    private static String formatSiteStatus(ConsumptionSite.Status status) {
        return switch (status) {
            case ACTIVE   -> "Active";
            case INACTIVE -> "Inactive";
        };
    }

    private static String formatCenterStatus(LogisticsCenter.Status status) {
        return switch (status) {
            case CANDIDATE -> "Pending";
            case APPROVED  -> "Active";
            case REJECTED  -> "Inactive";
            case CONFIRMED -> "Active";
        };
    }
}
