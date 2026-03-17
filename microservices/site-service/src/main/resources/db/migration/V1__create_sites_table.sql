-- =============================================================================
-- V1 — Create the consumption_sites table in the 'site' schema.
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS site;

CREATE TABLE IF NOT EXISTS site.consumption_sites (
    id            VARCHAR(36)     NOT NULL,
    company_id    VARCHAR(36)     NOT NULL,
    name          VARCHAR(200)    NOT NULL,
    description   TEXT,
    latitude      DOUBLE PRECISION NOT NULL,
    longitude     DOUBLE PRECISION NOT NULL,
    weight_tons   DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    address       VARCHAR(500),
    city          VARCHAR(200),
    country       VARCHAR(100),
    status        VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE'
                                  CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_consumption_sites PRIMARY KEY (id),
    CONSTRAINT chk_latitude  CHECK (latitude  >= -90.0  AND latitude  <= 90.0),
    CONSTRAINT chk_longitude CHECK (longitude >= -180.0 AND longitude <= 180.0),
    CONSTRAINT chk_weight    CHECK (weight_tons >= 0.0)
);

CREATE INDEX IF NOT EXISTS idx_sites_company_id ON site.consumption_sites (company_id);
CREATE INDEX IF NOT EXISTS idx_sites_company_status ON site.consumption_sites (company_id, status);

COMMENT ON TABLE site.consumption_sites IS 'Consumption sites — geographic inputs to barycenter calculations';
