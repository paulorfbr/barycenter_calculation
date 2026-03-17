package com.logistics.ui.viewmodel;

import com.logistics.domain.model.Company;
import com.logistics.ui.screens.CompanyListScreen;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps {@link Company} domain objects to {@link CompanyListScreen.CompanyRow}
 * view-model objects.
 *
 * This mapper lives in the UI package because it references JavaFX types
 * ({@code SimpleStringProperty} inside CompanyRow). The domain and application
 * layers remain free of JavaFX dependencies.
 *
 * Calling convention:
 *   Always invoke on the JavaFX Application Thread (or wrap in Platform.runLater)
 *   because CompanyRow internally creates JavaFX properties.
 */
public final class CompanyViewModelMapper {

    private CompanyViewModelMapper() {}

    /**
     * Converts a domain {@link Company} to a CompanyRow for the list table.
     *
     * @param company the domain object
     * @param locationCount number of associated locations (passed separately
     *                      because Company does not own Location in this model —
     *                      locations are queried from LocationRepository)
     * @return a populated CompanyRow
     */
    public static CompanyListScreen.CompanyRow toRow(Company company, int locationCount) {
        return new CompanyListScreen.CompanyRow(
                company.getId(),
                company.getName(),
                formatType(company.getType()),
                locationCount,
                formatStatus(company.getStatus())
        );
    }

    /**
     * Bulk-converts a list of companies.
     *
     * @param companies     the domain list
     * @param locationCount location count per company (keyed by company id;
     *                      pass an empty map if counts are unavailable)
     * @return list of CompanyRow instances
     */
    public static List<CompanyListScreen.CompanyRow> toRowList(
            List<Company> companies,
            java.util.Map<String, Integer> locationCount) {

        return companies.stream()
                .map(c -> toRow(c, locationCount.getOrDefault(c.getId(), 0)))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Formatting helpers
    // -------------------------------------------------------------------------

    private static String formatType(Company.Type type) {
        return switch (type) {
            case SHIPPER -> "Shipper";
            case CARRIER -> "Carrier";
            case BOTH    -> "Both";
        };
    }

    private static String formatStatus(Company.Status status) {
        return switch (status) {
            case ACTIVE   -> "Active";
            case INACTIVE -> "Inactive";
            case PENDING  -> "Pending";
        };
    }
}
