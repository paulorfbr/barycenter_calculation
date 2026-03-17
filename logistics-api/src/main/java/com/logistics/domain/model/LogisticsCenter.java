package com.logistics.domain.model;

import com.logistics.domain.vo.GeoCoordinate;
import com.logistics.domain.vo.TrafficVolume;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a candidate or confirmed logistics center
 * (warehouse / distribution hub) whose position was derived from a
 * barycenter calculation over a set of consumption sites.
 *
 * A LogisticsCenter records:
 *   - the optimal geographic position produced by the calculation engine.
 *   - a snapshot of which ConsumptionSite IDs were used as inputs.
 *   - the total weighted tonnage that informed the calculation.
 *   - metadata about the algorithm variant used and when it ran.
 *
 * Lifecycle:
 *   CANDIDATE — freshly calculated, not yet validated by a human.
 *   APPROVED  — a planner has reviewed and accepted this position.
 *   REJECTED  — a planner has rejected it; a new calculation is needed.
 *   CONFIRMED — approved and a physical facility is being set up at this site.
 *
 * Relationship to UI:
 *   Displayed on the Locations screen (LocationListScreen) as type "Logistics Center"
 *   and on the Dashboard barycenter map overlay. The LocationViewModelMapper converts
 *   this entity to a LocationRow for the table and to a Leaflet pin for the map.
 */
public class LogisticsCenter {

    // -------------------------------------------------------------------------
    // Lifecycle status
    // -------------------------------------------------------------------------

    public enum Status {
        CANDIDATE,
        APPROVED,
        REJECTED,
        CONFIRMED
    }

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------

    private final String id;
    private final String companyId;

    // -------------------------------------------------------------------------
    // Calculation result
    // -------------------------------------------------------------------------

    /** The barycenter position — the primary output of the calculation engine. */
    private GeoCoordinate optimalPosition;

    /** Total weighted tonnage summed across all input sites. */
    private final TrafficVolume totalWeightedVolume;

    /**
     * IDs of the ConsumptionSite objects that were used as inputs.
     * Stored as a snapshot so that future site edits do not retroactively
     * change the meaning of a historical calculation.
     */
    private final List<String> inputSiteIds;

    /** Human-readable name assigned by a planner or auto-generated. */
    private String name;

    /** Short description of the algorithm variant used. */
    private final String algorithmDescription;

    /** The iteration number if an iterative refinement was used (1 for pure barycenter). */
    private final int iterationCount;

    /** Convergence error at the final iteration (0.0 for the single-step variant). */
    private final double convergenceErrorKm;

    // -------------------------------------------------------------------------
    // Status and audit
    // -------------------------------------------------------------------------

    private Status  status;
    private String  reviewerNotes;
    private final Instant calculatedAt;
    private Instant       reviewedAt;

    // -------------------------------------------------------------------------
    // Constructor — created by the barycenter engine, not by application code directly
    // -------------------------------------------------------------------------

    /**
     * Full constructor used by {@code BarycentreCalculationEngine} when publishing
     * a calculation result.
     *
     * @param id                   unique identifier; pass null to auto-generate
     * @param companyId            owning company
     * @param name                 display name
     * @param optimalPosition      the barycenter coordinate
     * @param totalWeightedVolume  sum of all input site tonnages
     * @param inputSiteIds         snapshot of input site IDs
     * @param algorithmDescription short algorithm label
     * @param iterationCount       number of refinement iterations performed
     * @param convergenceErrorKm   residual error in km at last iteration
     */
    public LogisticsCenter(String id,
                           String companyId,
                           String name,
                           GeoCoordinate optimalPosition,
                           TrafficVolume totalWeightedVolume,
                           List<String> inputSiteIds,
                           String algorithmDescription,
                           int iterationCount,
                           double convergenceErrorKm) {
        this.id                   = id != null ? id : UUID.randomUUID().toString();
        this.companyId            = Objects.requireNonNull(companyId, "companyId must not be null");
        this.name                 = name != null ? name : "Logistics Center";
        this.optimalPosition      = Objects.requireNonNull(optimalPosition, "optimalPosition must not be null");
        this.totalWeightedVolume  = Objects.requireNonNull(totalWeightedVolume, "totalWeightedVolume must not be null");
        this.inputSiteIds         = List.copyOf(Objects.requireNonNull(inputSiteIds, "inputSiteIds must not be null"));
        this.algorithmDescription = algorithmDescription != null ? algorithmDescription : "weighted-barycenter";
        this.iterationCount       = iterationCount;
        this.convergenceErrorKm   = convergenceErrorKm;
        this.status               = Status.CANDIDATE;
        this.calculatedAt         = Instant.now();
    }

    // -------------------------------------------------------------------------
    // Domain behaviour
    // -------------------------------------------------------------------------

    /**
     * A planner approves this logistics center location.
     *
     * @param notes optional review notes
     */
    public void approve(String notes) {
        if (status == Status.REJECTED) {
            throw new IllegalStateException("Cannot approve a rejected logistics center. Re-run the calculation.");
        }
        status       = Status.APPROVED;
        reviewerNotes = notes;
        reviewedAt    = Instant.now();
    }

    /**
     * A planner rejects this logistics center — triggers a new calculation.
     *
     * @param notes required rejection reason
     */
    public void reject(String notes) {
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("A rejection reason must be provided.");
        }
        status        = Status.REJECTED;
        reviewerNotes = notes;
        reviewedAt    = Instant.now();
    }

    /**
     * Moves to CONFIRMED after the facility is operationally committed.
     */
    public void confirm() {
        if (status != Status.APPROVED) {
            throw new IllegalStateException("Only an APPROVED center can be confirmed.");
        }
        status     = Status.CONFIRMED;
        reviewedAt = Instant.now();
    }

    /**
     * Allows a planner to manually override the calculated position
     * (e.g. due to land availability or zoning constraints).
     *
     * @param adjustedPosition the manually set override position
     * @param reason           why the override was made
     */
    public void overridePosition(GeoCoordinate adjustedPosition, String reason) {
        this.optimalPosition = Objects.requireNonNull(adjustedPosition);
        if (reviewerNotes == null) {
            reviewerNotes = reason;
        } else {
            reviewerNotes = reviewerNotes + "\nOverride: " + reason;
        }
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String        getId()                   { return id; }
    public String        getCompanyId()             { return companyId; }
    public String        getName()                  { return name; }
    public GeoCoordinate getOptimalPosition()       { return optimalPosition; }
    public TrafficVolume getTotalWeightedVolume()   { return totalWeightedVolume; }
    public List<String>  getInputSiteIds()          { return inputSiteIds; }
    public String        getAlgorithmDescription()  { return algorithmDescription; }
    public int           getIterationCount()        { return iterationCount; }
    public double        getConvergenceErrorKm()    { return convergenceErrorKm; }
    public Status        getStatus()                { return status; }
    public String        getReviewerNotes()         { return reviewerNotes; }
    public Instant       getCalculatedAt()          { return calculatedAt; }
    public Instant       getReviewedAt()            { return reviewedAt; }

    public void setName(String name) { this.name = name; }

    // -------------------------------------------------------------------------
    // equals / hashCode
    // -------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogisticsCenter lc)) return false;
        return id.equals(lc.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return "LogisticsCenter{id='" + id + "', company='" + companyId
                + "', position=" + optimalPosition + ", status=" + status + "}";
    }
}
