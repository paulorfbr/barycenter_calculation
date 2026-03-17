package com.logistics.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TrafficVolume}.
 */
@DisplayName("TrafficVolume")
class TrafficVolumeTest {

    @Test
    @DisplayName("accepts zero tonnage")
    void acceptsZero() {
        assertDoesNotThrow(() -> TrafficVolume.ofTons(0.0));
    }

    @Test
    @DisplayName("accepts positive tonnage")
    void acceptsPositive() {
        assertDoesNotThrow(() -> TrafficVolume.ofTons(1500.5));
    }

    @Test
    @DisplayName("rejects negative tonnage")
    void rejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> TrafficVolume.ofTons(-1.0));
    }

    @Test
    @DisplayName("rejects NaN")
    void rejectsNaN() {
        assertThrows(IllegalArgumentException.class, () -> TrafficVolume.ofTons(Double.NaN));
    }

    @Test
    @DisplayName("rejects positive infinity")
    void rejectsInfinity() {
        assertThrows(IllegalArgumentException.class,
                () -> TrafficVolume.ofTons(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("add() returns sum of two volumes")
    void addReturnsSumOfTwoVolumes() {
        TrafficVolume a = TrafficVolume.ofTons(300.0);
        TrafficVolume b = TrafficVolume.ofTons(200.0);
        assertEquals(500.0, a.add(b).tons(), 1e-9);
    }

    @Test
    @DisplayName("add() is non-mutating")
    void addIsNonMutating() {
        TrafficVolume a = TrafficVolume.ofTons(300.0);
        TrafficVolume b = TrafficVolume.ofTons(200.0);
        a.add(b);
        assertEquals(300.0, a.tons());
    }

    @Test
    @DisplayName("isPositive() returns true for non-zero")
    void isPositiveReturnsTrueForNonZero() {
        assertTrue(TrafficVolume.ofTons(0.001).isPositive());
    }

    @Test
    @DisplayName("isPositive() returns false for zero")
    void isPositiveReturnsFalseForZero() {
        assertFalse(TrafficVolume.ZERO.isPositive());
    }

    @Test
    @DisplayName("toDisplayString formats with units")
    void toDisplayStringFormatsWithUnits() {
        String display = TrafficVolume.ofTons(1450.0).toDisplayString();
        assertTrue(display.endsWith("t"), "Should end with 't'");
        assertTrue(display.contains("1"), "Should contain the value");
    }
}
