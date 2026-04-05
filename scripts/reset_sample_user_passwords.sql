-- =============================================================================
-- Reset de contraseñas para usuarios de ejemplo (Recicar)
-- =============================================================================
-- Contraseñas tras ejecutar este script:
--   admin@recicar.com    -> admin
--   vendor@recicar.com   -> vendor
--   customer@recicar.com -> customer
--
-- Hashes BCrypt strength 12 (alineado con SecurityConfig / BCryptPasswordEncoder(12))
--
-- Uso (ajusta esquema según tu entorno):
--   psql -U marketplace_user -d marketplace_dev -f scripts/reset_sample_user_passwords.sql
--
-- Dev local con esquema "recicar" (application.yml perfil dev): descomenta SET search_path.
-- Si las tablas están solo en public (test/prod estándar): comenta SET search_path.
-- =============================================================================

-- Descomenta si tus tablas están en el esquema recicar:
SET search_path TO recicar, public;

UPDATE users
SET password_hash = '$2a$12$aoPG6UlSEkQy6alBS59gAOhns1i.xeXJimv8qlBGLNiYdDbEO9awW',
    updated_at    = CURRENT_TIMESTAMP
WHERE email = 'admin@recicar.com';

UPDATE users
SET password_hash = '$2a$12$veFiOYrd8DK9oUnvEpo5TeihYwKT49fuqDJSNSUzCFDqFfPbZaD0W',
    updated_at    = CURRENT_TIMESTAMP
WHERE email = 'vendor@recicar.com';

UPDATE users
SET password_hash = '$2a$12$qexm6WEpZf94KHQx3X.F1.Khj8V30LT1n6P/SB4vTgRu9T29zkLKG',
    updated_at    = CURRENT_TIMESTAMP
WHERE email = 'customer@recicar.com';

-- Comprueba filas afectadas (debería ser 1 por UPDATE si el email existe)
-- SELECT email, role FROM users WHERE email LIKE '%@recicar.com';
