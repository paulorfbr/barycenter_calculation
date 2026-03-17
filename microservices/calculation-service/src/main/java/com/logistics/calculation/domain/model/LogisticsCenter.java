package com.logistics.calculation.domain.model;

import com.logistics.shared.domain.vo.GeoCoordinate;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain model for a barycenter calculation result.
 *
 * Carries the optimal position and all metadata needed to reproduce or
 * audit the calculation.  Status transitions follow the review workflow:
 * CANDIDATE → APPROVED → CONFIRMED  or  CANDIDATE → REJECTED.
 */
public class LogisticsCenter {

    public enum Status { CANDIDATE, APPROVED, REJECTED, CONFIRMED }

    private final String        id;
    private final String        companyId;
    private       GeoCoordinate optimalPosition;
    private final double        totalWeightedTons;
    private final List<String>  inputSiteIds;
    private final String        algorithmDescription;
    private final int           iterationCount;
    private final double        convergenceErrorKm;
    private final Instant       calculatedAt;

    private Status  status;
    private String  reviewerNotes;
    private Instant reviewedAt;

    public LogisticsCenter(String id,
                           String companyId,
                           GeoCoordinate optimalPosition,
                           double totalWeightedTons,
                           List<String> inputSiteIds,
                           String algorithmDescription,
                           int iterationCount,
                           double convergenceErrorKm,
                           Instant calculatedAt) {
        this.id                   = Objects.requireNonNull(id);
        this.companyId            = Objects.requireNonNull(companyId);
        this.optimalPosition      = Objects.requireNonNull(optimalPosition);
        this.totalWeightedTons    = totalWeightedTons;
        this.inputSiteIds         = List.copyOf(Objects.requireNonNull(inputSiteIds));
        this.algorithmDescription = algorithmDescription;
        this.iterationCount       = iterationCount;
        this.convergenceErrorKm   = convergenceErrorKm;
        this.calculatedAt         = calculatedAt;
        this.status               = Status.CANDIDATE;
    }

    // --- domain behaviour ---

    public void approve(String notes) {
        if (status == Status.REJECTED) throw new IllegalStateException("Cannot approve a rejected center.");
        status        = Status.APPROVED;
        reviewerNotes = notes;
        reviewedAt    = Instant.now();
    }

    public void reject(String notes) {
        if (notes == null || notes.isBlank()) throw new IllegalArgumentException("Rejection reason required.");
        status        = Status.REJECTED;
        reviewerNotes = notes;
        reviewedAt    = Instant.now();
    }

    public void confirm() {
        if (status != Status.APPROVED) throw new IllegalStateException("Only APPROVED centers can be confirmed.");
        status     = Status.CONFIRMED;
        reviewedAt = Instant.now();
    }

    public void overridePosition(GeoCoordinate adjusted, String reason) {
        this.optimalPosition = Objects.requireNonNull(adjusted);
        reviewerNotes = (reviewerNotes == null ? "" : reviewerNotes + "\n") + "Override: " + reason;
    }

    // --- accessors ---

    public String        getId()                   { return id; }
    public String        getCompanyId()             { return companyId; }
    public GeoCoordinate getOptimalPosition()       { return optimalPosition; }
    public double        getTotalWeightedTons()     { return totalWeightedTons; }
    public List<String>  getInputSiteIds()          { return Collections.unmodifiableList(inputSiteIds); }
    public String        getAlgorithmDescription()  { return algorithmDescription; }
    public int           getIterationCount()        { return iterationCount; }
    public double        getConvergenceErrorKm()    { return convergenceErrorKm; }
    public Instant       getCalculatedAt()          { return calculatedAt; }
    public Status        getStatus()                { return status; }
    public String        getReviewerNotes()         { return reviewerNotes; }
    public Instant       getReviewedAt()            { return reviewedAt; }
}
