package com.logistics.shared.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

/**
 * Canonical, Jackson-serialisable value object for WGS-84 coordinates.
 *
 * Re-exported from the shared library so that every microservice uses identical
 * types when passing coordinates across service boundaries via REST or events.
 * Uses a classical class rather than a Java record to keep full Jackson
 * compatibility across all Jackson versions in the BOM.
 */
public final class GeoCoordinate {

    private static final double EARTH_RADIUS_KM = 6_371.0;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private final double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private final double longitude;

    @JsonCreator
    public GeoCoordinate(
            @JsonProperty("latitude")  double latitude,
            @JsonProperty("longitude") double longitude) {
        if (latitude  < -90.0  || latitude  > 90.0)  throw new IllegalArgumentException("Latitude out of range: "  + latitude);
        if (longitude < -180.0 || longitude > 180.0) throw new IllegalArgumentException("Longitude out of range: " + longitude);
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    public static GeoCoordinate of(double latitude, double longitude) {
        return new GeoCoordinate(latitude, longitude);
    }

    public double getLatitude()  { return latitude; }
    public double getLongitude() { return longitude; }

    /**
     * Haversine great-circle distance between this coordinate and {@code other}.
     *
     * @param other the target coordinate
     * @return distance in kilometres
     */
    public double distanceKmTo(GeoCoordinate other) {
        double lat1  = Math.toRadians(this.latitude);
        double lat2  = Math.toRadians(other.latitude);
        double dLat  = Math.toRadians(other.latitude  - this.latitude);
        double dLon  = Math.toRadians(other.longitude - this.longitude);
        double a     = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                     + Math.cos(lat1) * Math.cos(lat2)
                     * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public String toDisplayString() {
        return String.format("%.4f, %.4f", latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoCoordinate g)) return false;
        return Double.compare(latitude, g.latitude) == 0
            && Double.compare(longitude, g.longitude) == 0;
    }

    @Override public int    hashCode() { return Double.hashCode(latitude) * 31 + Double.hashCode(longitude); }
    @Override public String toString()  { return "GeoCoordinate{lat=" + latitude + ", lon=" + longitude + "}"; }
}
