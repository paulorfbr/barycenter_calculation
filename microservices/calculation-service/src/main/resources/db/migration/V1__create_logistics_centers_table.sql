-- =============================================================================
-- V1 — Create logistics_centers table in the 'calculation' schema.
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS calculation;

CREATE TABLE IF NOT EXISTS calculation.logistics_centers (
    id                    VARCHAR(36)      NOT NULL,
    company_id            VARCHAR(36)      NOT NULL,
    optimal_latitude      DOUBLE PRECISION NOT NULL,
    optimal_longitude     DOUBLE PRECISION NOT NULL,
    total_weighted_tons   DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    algorithm_description VARCHAR(100)     NOT NULL,
    iteration_count       INTEGER          NOT NULL DEFAULT 1,
    convergence_error_km  DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    status                VARCHAR(15)      NOT NULL DEFAULT 'CANDIDATE'
                                           CHECK (status IN ('CANDIDATE','APPROVED','REJECTED','CONFIRMED')),
    reviewer_notes        TEXT,
    calculated_at         TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    reviewed_at           TIMESTAMPTZ,

    CONSTRAINT pk_logistics_centers PRIMARY KEY (id),
    CONSTRAINT chk_optimal_latitude  CHECK (optimal_latitude  >= -90.0  AND optimal_latitude  <= 90.0),
    CONSTRAINT chk_optimal_longitude CHECK (optimal_longitude >= -180.0 AND optimal_longitude <= 180.0),
    CONSTRAINT chk_total_tons        CHECK (total_weighted_tons >= 0.0)
);

-- Store input site IDs as a separate normalised table
CREATE TABLE IF NOT EXISTS calculation.logistics_center_sites (
    logistics_center_id VARCHAR(36) NOT NULL,
    site_id             VARCHAR(36) NOT NULL,
    sort_order          INTEGER     NOT NULL DEFAULT 0,

    CONSTRAINT pk_lc_sites PRIMARY KEY (logistics_center_id, site_id),
    CONSTRAINT fk_lc_sites_center FOREIGN KEY (logistics_center_id)
        REFERENCES calculation.logistics_centers (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_lc_company_id ON calculation.logistics_centers (company_id);
CREATE INDEX IF NOT EXISTS idx_lc_status     ON calculation.logistics_centers (status);
CREATE INDEX IF NOT EXISTS idx_lc_sites_center ON calculation.logistics_center_sites (logistics_center_id);

COMMENT ON TABLE calculation.logistics_centers IS 'Barycenter calculation results — owned by Calculation Service';
