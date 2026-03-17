package com.logistics.domain.vo;

/**
 * Immutable value object representing a geographic position as a
 * WGS-84 (latitude / longitude) decimal-degree coordinate pair.
 *
 * Design decisions:
 *   - Record chosen for automatic equals/hashCode/toString and immutability.
 *   - Validation is performed at construction time so that any domain object
 *     that holds a GeoCoordinate is guaranteed to carry valid data.
 *   - Haversine distance is kept here because it is a pure geometric operation
 *     on two coordinates and carries no external dependencies.
 *
 * Coordinate convention:
 *   latitude  — north/south, range [-90, +90]
 *   longitude — east/west,   range [-180, +180]
 */
public record GeoCoordinate(double latitude, double longitude) {

    // -------------------------------------------------------------------------
    // Earth radius used for Haversine calculation (mean spherical radius, km)
    // -------------------------------------------------------------------------

    private static final double EARTH_RADIUS_KM = 6_371.0;

    // -------------------------------------------------------------------------
    // Compact canonical constructor — validation
    // -------------------------------------------------------------------------

    public GeoCoordinate {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException(
                    "Latitude must be in [-90, 90] but was: " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException(
                    "Longitude must be in [-180, 180] but was: " + longitude);
        }
    }

    // -------------------------------------------------------------------------
    // Factory helpers
    // -------------------------------------------------------------------------

    /**
     * Parses a coordinate from decimal-degree strings as stored by the
     * existing LocationRow view-model (e.g. "34.0522", "-118.2437").
     *
     * @param latitude  latitude string
     * @param longitude longitude string
     * @return validated GeoCoordinate
     * @throws IllegalArgumentException if either string is non-numeric or out of range
     */
    public static GeoCoordinate of(String latitude, String longitude) {
        try {
            return new GeoCoordinate(
                    Double.parseDouble(latitude.trim()),
                    Double.parseDouble(longitude.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Cannot parse coordinate from '" + latitude + "', '" + longitude + "'", e);
        }
    }

    // -------------------------------------------------------------------------
    // Geometry
    // -------------------------------------------------------------------------

    /**
     * Calculates the great-circle distance to another coordinate using the
     * Haversine formula.
     *
     * @param other the other point
     * @return distance in kilometres
     */
    public double distanceKmTo(GeoCoordinate other) {
        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Formats the coordinate in the monospace style used by LocationRow:
     * {@code "34.0522, -118.2437"}.
     *
     * @return display string
     */
    public String toDisplayString() {
        return String.format("%.4f, %.4f", latitude, longitude);
    }

    /**
     * Formats as a Leaflet.js {@code L.latLng()} array literal.
     * Matches the {@code toLatLng()} output expected by LocationListScreen.
     *
     * @return string in the form {@code "[34.0522,-118.2437]"}
     */
    public String toLeafletLatLng() {
        return "[" + latitude + "," + longitude + "]";
    }
}
