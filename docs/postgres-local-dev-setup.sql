-- =============================================================================
-- PostgreSQL local (DEV) — Recicar / Flyway
-- =============================================================================
-- Ejecutar como superusuario, por ejemplo:
--   psql -U postgres -f docs/postgres-local-dev-setup.sql
--
-- Si la base y el usuario YA existen pero ves:
--   ERROR: permission denied for schema public
-- ejecuta solo la sección 3 (permisos en public).
-- =============================================================================

-- 1) Usuario (omitir si marketplace_user ya existe)
CREATE USER marketplace_user WITH PASSWORD 'marketplace_pass';

-- 2) Base de datos (omitir si marketplace_dev ya existe)
CREATE DATABASE marketplace_dev OWNER marketplace_user;

-- 3) PostgreSQL 15+: sin esto Flyway no puede crear flyway_schema_history en public
\c marketplace_dev

GRANT CONNECT ON DATABASE marketplace_dev TO marketplace_user;
GRANT CREATE, USAGE ON SCHEMA public TO marketplace_user;
ALTER SCHEMA public OWNER TO marketplace_user;

-- Asegura permisos sobre objetos futuros creados por el rol de la app
ALTER DEFAULT PRIVILEGES FOR ROLE marketplace_user IN SCHEMA public
  GRANT ALL ON TABLES TO marketplace_user;
ALTER DEFAULT PRIVILEGES FOR ROLE marketplace_user IN SCHEMA public
  GRANT ALL ON SEQUENCES TO marketplace_user;
