-- =============================================================================
-- PostgreSQL initialisation script — creates one database and dedicated user
-- per microservice.
--
-- This script runs automatically when the postgres container starts for the
-- first time (Docker mounts it into /docker-entrypoint-initdb.d/).
--
-- Production: replace with separate RDS instances per service.
-- =============================================================================

-- Company Service database
CREATE USER company_svc WITH PASSWORD 'company_pass';
CREATE DATABASE company_db OWNER company_svc ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE company_db TO company_svc;

-- Site Service database
CREATE USER site_svc WITH PASSWORD 'site_pass';
CREATE DATABASE site_db OWNER site_svc ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE site_db TO site_svc;

-- Calculation Service database
CREATE USER calc_svc WITH PASSWORD 'calc_pass';
CREATE DATABASE calculation_db OWNER calc_svc ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE calculation_db TO calc_svc;

-- Grant schema creation rights (Flyway needs this on first run)
\c company_db
GRANT CREATE ON DATABASE company_db TO company_svc;

\c site_db
GRANT CREATE ON DATABASE site_db TO site_svc;

\c calculation_db
GRANT CREATE ON DATABASE calculation_db TO calc_svc;
