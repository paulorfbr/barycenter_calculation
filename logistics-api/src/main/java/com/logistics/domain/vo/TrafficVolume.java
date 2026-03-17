package com.logistics.domain.vo;

/**
 * Immutable value object representing a traffic volume measured in metric tons.
 *
 * Encapsulating the numeric value in a dedicated type:
 *   - Prevents accidental mixing of weight, distance, and other double fields.
 *   - Carries the unit of measurement together with the value.
 *   - Provides arithmetic helpers used by the barycenter calculation engine
 *     without polluting the calculation classes with primitive boxing noise.
 *
 * The barycenter algorithm requires a positive, finite weight — the compact
 * constructor enforces this invariant at the boundary of the domain.
 */
public record TrafficVolume(double tons) {

    // -------------------------------------------------------------------------
    // Compact canonical constructor — validation
    // -------------------------------------------------------------------------

    public TrafficVolume {
        if (!Double.isFinite(tons)) {
            throw new IllegalArgumentException("Traffic volume must be finite, got: " + tons);
        }
        if (tons < 0.0) {
            throw new IllegalArgumentException(
                    "Traffic volume must be non-negative, got: " + tons);
        }
    }

    // -------------------------------------------------------------------------
    // Factory helpers
    // -------------------------------------------------------------------------

    /** Zero-volume sentinel — useful when a site has no active traffic yet. */
    public static final TrafficVolume ZERO = new TrafficVolume(0.0);

    /**
     * Creates a TrafficVolume from a plain double.
     *
     * @param tons non-negative tonnage
     * @return validated TrafficVolume
     */
    public static TrafficVolume ofTons(double tons) {
        return new TrafficVolume(tons);
    }

    // -------------------------------------------------------------------------
    // Arithmetic (returns new instances — value objects are immutable)
    // -------------------------------------------------------------------------

    /** @return sum of this and {@code other} volumes */
    public TrafficVolume add(TrafficVolume other) {
        return new TrafficVolume(this.tons + other.tons);
    }

    /** @return true if this volume is strictly greater than zero */
    public boolean isPositive() {
        return tons > 0.0;
    }

    /** @return formatted display string, e.g. {@code "1,450.0 t"} */
    public String toDisplayString() {
        return String.format("%,.1f t", tons);
    }
}
