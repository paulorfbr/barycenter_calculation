package com.logistics.shared.openapi;

/**
 * Constants for consistent API documentation across microservices.
 * Per constitution requirement: Standardized API documentation.
 */
public final class ApiDocumentationConstants {

    // Common response descriptions
    public static final String SUCCESS_200 = "Operation successful";
    public static final String CREATED_201 = "Resource created successfully";
    public static final String NO_CONTENT_204 = "Operation successful, no content returned";
    public static final String BAD_REQUEST_400 = "Invalid request data";
    public static final String UNAUTHORIZED_401 = "Authentication required";
    public static final String FORBIDDEN_403 = "Insufficient permissions";
    public static final String NOT_FOUND_404 = "Resource not found";
    public static final String CONFLICT_409 = "Resource conflict";
    public static final String INTERNAL_ERROR_500 = "Internal server error";

    // Security descriptions
    public static final String JWT_SECURITY = "JWT token required in Authorization header";
    public static final String ADMIN_ROLE = "Admin role required";
    public static final String MANAGER_ROLE = "Manager role or higher required";

    // Tag descriptions
    public static final String TAG_COMPANIES = "Company Management";
    public static final String TAG_SITES = "Consumption Site Management";
    public static final String TAG_CALCULATIONS = "Barycenter Calculations";
    public static final String TAG_DASHBOARD = "Analytics and Reporting";
    public static final String TAG_AUTH = "Authentication and Authorization";
    public static final String TAG_ADMIN = "Administrative Operations";

    // Operation descriptions
    public static final String COMPANY_CREATE = "Create a new partner company";
    public static final String COMPANY_LIST = "Retrieve paginated list of companies";
    public static final String COMPANY_GET = "Retrieve company details by ID";
    public static final String COMPANY_UPDATE = "Update existing company information";
    public static final String COMPANY_DELETE = "Delete company and associated data";

    public static final String SITE_CREATE = "Create a new consumption site";
    public static final String SITE_LIST = "Retrieve paginated list of sites";
    public static final String SITE_GET = "Retrieve site details by ID";
    public static final String SITE_UPDATE = "Update existing site information";
    public static final String SITE_DELETE = "Delete site and associated data";
    public static final String SITE_BULK_IMPORT = "Import multiple sites from CSV/Excel";

    public static final String CALC_BARYCENTER = "Calculate optimal barycenter location";
    public static final String CALC_HISTORY = "Retrieve calculation history";
    public static final String CALC_RESULT = "Retrieve specific calculation result";

    public static final String DASHBOARD_METRICS = "Retrieve key performance indicators";
    public static final String DASHBOARD_REPORTS = "Generate analytical reports";

    private ApiDocumentationConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}