package com.logistics.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link GeoCoordinate} value object.
 */
@DisplayName("GeoCoordinate")
class GeoCoordinateTest {

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("accepts valid coordinate at (0,0)")
        void acceptsOrigin() {
            assertThatCode(() -> new GeoCoordinate(0.0, 0.0)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("accepts boundary latitude values")
        void acceptsBoundaryLatitude() {
            assertThatCode(() -> new GeoCoordinate(-90.0, 0.0)).doesNotThrowAnyException();
            assertThatCode(() -> new GeoCoordinate(90.0, 0.0)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("accepts boundary longitude values")
        void acceptsBoundaryLongitude() {
            assertThatCode(() -> new GeoCoordinate(0.0, -180.0)).doesNotThrowAnyException();
            assertThatCode(() -> new GeoCoordinate(0.0, 180.0)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("throws for latitude outside [-90, 90]")
        void throwsForInvalidLatitude() {
            assertThatThrownBy(() -> new GeoCoordinate(91.0, 0.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Latitude");
            assertThatThrownBy(() -> new GeoCoordinate(-90.1, 0.0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("throws for longitude outside [-180, 180]")
        void throwsForInvalidLongitude() {
            assertThatThrownBy(() -> new GeoCoordinate(0.0, 181.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Longitude");
        }
    }

    @Nested
    @DisplayName("distanceKmTo()")
    class HaversineDistance {

        @Test
        @DisplayName("distance from a point to itself is 0")
        void zeroDistanceToSelf() {
            GeoCoordinate coord = new GeoCoordinate(51.5, -0.1);
            assertThat(coord.distanceKmTo(coord)).isCloseTo(0.0, within(0.001));
        }

        @Test
        @DisplayName("LAX to JFK is approximately 3,983 km")
        void laxToJfk() {
            GeoCoordinate lax = new GeoCoordinate(33.9425, -118.4081);
            GeoCoordinate jfk = new GeoCoordinate(40.6413, -73.7781);
            assertThat(lax.distanceKmTo(jfk)).isCloseTo(3983.0, within(20.0));
        }

        @Test
        @DisplayName("distance is symmetric")
        void distanceIsSymmetric() {
            GeoCoordinate a = new GeoCoordinate(48.85, 2.35);   // Paris
            GeoCoordinate b = new GeoCoordinate(51.50, -0.12);  // London
            assertThat(a.distanceKmTo(b)).isCloseTo(b.distanceKmTo(a), within(0.001));
        }
    }

    @Nested
    @DisplayName("factory and formatting")
    class Factory {

        @Test
        @DisplayName("GeoCoordinate.of() parses decimal strings correctly")
        void parsesDecimalStrings() {
            GeoCoordinate coord = GeoCoordinate.of("34.0522", "-118.2437");
            assertThat(coord.latitude()).isCloseTo(34.0522, within(0.0001));
            assertThat(coord.longitude()).isCloseTo(-118.2437, within(0.0001));
        }

        @Test
        @DisplayName("GeoCoordinate.of() throws for non-numeric input")
        void throwsForNonNumeric() {
            assertThatThrownBy(() -> GeoCoordinate.of("abc", "0.0"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("toDisplayString formats to 4 decimal places")
        void toDisplayStringFormat() {
            GeoCoordinate coord = new GeoCoordinate(34.0522, -118.2437);
            assertThat(coord.toDisplayString()).isEqualTo("34.0522, -118.2437");
        }
    }
}
