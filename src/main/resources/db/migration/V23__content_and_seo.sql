-- Content & SEO: category landing fields, part code reference list, blog meta

ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS meta_title VARCHAR(255);

ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500);

ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS seo_content TEXT;

ALTER TABLE blog_posts
    ADD COLUMN IF NOT EXISTS meta_description VARCHAR(500);

CREATE TABLE part_code_references (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(100) NOT NULL,
    manufacturer  VARCHAR(200),
    part_type     VARCHAR(200),
    description   TEXT,
    sort_order    INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_part_code_sort ON part_code_references (sort_order, id);

INSERT INTO part_code_references (code, manufacturer, part_type, description, sort_order) VALUES
    ('OEM-REF', '—', 'Convención', 'Suele identificar el repuesto aprobado por el fabricante del vehículo.', 0),
    ('ATE', 'Continental/ATE', 'Frenos', 'Referencia frecuente en pastillas y discos de freno.', 10),
    ('BOSCH', 'Robert Bosch', 'Multisistema', 'Inyección, arranque, filtros, electricidad; cruzar siempre con OEM del vehículo.', 20),
    ('VALEO', 'Valeo', 'Embrague / Térmico', 'Kits de embrague, radiadores; validar con bastidor o referencia de motor.', 30),
    ('GATES', 'Gates', 'Distribución / Correas', 'Correas y kits; comprobar juego exacto del fabricante de motor.', 40),
    ('SACHS', 'ZF Sachs', 'Transmisión / Suspensión', 'Amortiguadores, embrague; comprobar homologación por modelo.', 50);

COMMENT ON TABLE part_code_references IS 'Educational / illustrative OEM & brand references (content SEO page)';
