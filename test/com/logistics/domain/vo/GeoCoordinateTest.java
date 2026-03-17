package com.logistics.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GeoCoordinate}.
 *
 * Covers:
 *   - Validation at construction time
 *   - String-based factory method
 *   - Haversine distance accuracy (reference values from online calculators)
 *   - Display formatting
 *   - Record equality and hashCode
 */
@DisplayName("GeoCoordinate")
class GeoCoordinateTest {

    // -------------------------------------------------------------------------
    // Construction validation
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("constructor validation")
    class Validation {

        @Test
        @DisplayName("accepts valid coordinates")
        void acceptsValidCoordinates() {
            assertDoesNotThrow(() -> new GeoCoordinate(34.0522, -118.2437));
        }

        @ParameterizedTest(name = "latitude {0} should be rejected")
        @CsvSource({"-90.1", "90.1", "180.0", "-180.0"})
        @DisplayName("rejects out-of-range latitudes")
        void rejectsOutOfRangeLatitude(double latitude) {
            assertThrows(IllegalArgumentException.class,
                    () -> new GeoCoordinate(latitude, 0.0));
        }

        @ParameterizedTest(name = "longitude {0} should be rejected")
        @CsvSource({"-180.1", "180.1", "360.0"})
        @DisplayName("rejects out-of-range longitudes")
        void rejectsOutOfRangeLongitude(double longitude) {
            assertThrows(IllegalArgumentException.class,
                    () -> new GeoCoordinate(0.0, longitude));
        }

        @Test
        @DisplayName("accepts boundary latitude values")
        void acceptsBoundaryLatitudes() {
            assertDoesNotThrow(() -> new GeoCoordinate(-90.0, 0.0));
            assertDoesNotThrow(() -> new GeoCoordinate(90.0, 0.0));
        }

        @Test
        @DisplayName("accepts boundary longitude values")
        void acceptsBoundaryLongitudes() {
            assertDoesNotThrow(() -> new GeoCoordinate(0.0, -180.0));
            assertDoesNotThrow(() -> new GeoCoordinate(0.0, 180.0));
        }
    }

    // -------------------------------------------------------------------------
    // Factory: GeoCoordinate.of(String, String)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("of(String, String)")
    class StringFactory {

        @Test
        @DisplayName("parses valid strings")
        void parsesValidStrings() {
            GeoCoordinate coord = GeoCoordinate.of("34.0522", "-118.2437");
            assertEquals(34.0522, coord.latitude(), 1e-6);
            assertEquals(-118.2437, coord.longitude(), 1e-6);
        }

        @Test
        @DisplayName("trims whitespace before parsing")
        void trimsWhitespace() {
            GeoCoordinate coord = GeoCoordinate.of(" 40.7128 ", " -74.0060 ");
            assertEquals(40.7128, coord.latitude(), 1e-4);
        }

        @Test
        @DisplayName("throws for non-numeric input")
        void throwsForNonNumericInput() {
            assertThrows(IllegalArgumentException.class,
                    () -> GeoCoordinate.of("abc", "0.0"));
        }
    }

    // -------------------------------------------------------------------------
    // Haversine distance
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("distanceKmTo()")
    class Distance {

        /**
         * Reference: LAX to JFK is approximately 3,974 km by great-circle.
         * Various online calculators agree to within ±5 km.
         */
        @Test
        @DisplayName("LAX to JFK approximately 3974 km")
        void laxToJfk() {
            GeoCoordinate lax = new GeoCoordinate(33.9425, -118.4081);
            GeoCoordinate jfk = new GeoCoordinate(40.6413, -73.7781);
            double distKm = lax.distanceKmTo(jfk);
            // Accept within ±10 km of the well-known reference value
            assertEquals(3974.0, distKm, 10.0,
                    "LAX to JFK distance should be ~3974 km, got: " + distKm);
        }

        @Test
        @DisplayName("distance to self is zero")
        void distanceToSelfIsZero() {
            GeoCoordinate point = new GeoCoordinate(51.5074, -0.1278);
            assertEquals(0.0, point.distanceKmTo(point), 1e-9);
        }

        @Test
        @DisplayName("distance is symmetric")
        void distanceIsSymmetric() {
            GeoCoordinate a = new GeoCoordinate(34.0522, -118.2437);
            GeoCoordinate b = new GeoCoordinate(40.7128, -74.0060);
            assertEquals(a.distanceKmTo(b), b.distanceKmTo(a), 1e-9);
        }
    }

    // -------------------------------------------------------------------------
    // Display formatting
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toDisplayString formats to 4 decimal places with comma-space")
    void toDisplayString() {
        GeoCoordinate coord = new GeoCoordinate(34.0522, -118.2437);
        assertEquals("34.0522, -118.2437", coord.toDisplayString());
    }

    @Test
    @DisplayName("toLeafletLatLng formats as bracket array")
    void toLeafletLatLng() {
        GeoCoordinate coord = new GeoCoordinate(40.7128, -74.006);
        String result = coord.toLeafletLatLng();
        assertTrue(result.startsWith("["), "Should start with [");
        assertTrue(result.endsWith("]"), "Should end with ]");
        assertTrue(result.contains(","), "Should contain comma separator");
    }

    // -------------------------------------------------------------------------
    // Record equality
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("two coordinates with same values are equal")
    void equalityByValue() {
        GeoCoordinate a = new GeoCoordinate(34.0522, -118.2437);
        GeoCoordinate b = new GeoCoordinate(34.0522, -118.2437);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("two coordinates with different values are not equal")
    void inequalityByValue() {
        GeoCoordinate a = new GeoCoordinate(34.0522, -118.2437);
        GeoCoordinate b = new GeoCoordinate(40.7128, -74.0060);
        assertNotEquals(a, b);
    }
}
