-- 3-4 level depth under main Spanish categories (V10): motor, sistema-frenos
-- Subqueries use MIN(id) for H2/PostgreSQL compatibility
-- L2
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Bloque motor', 'Carter, culata, pistones, cigüeñal', (SELECT min(id) FROM categories WHERE slug = 'motor' AND parent_id IS NULL), 'motor-bloque', true, 1, CURRENT_TIMESTAMP),
('Alimentación y escape', 'Admisión, escape, intercooler', (SELECT min(id) FROM categories WHERE slug = 'motor' AND parent_id IS NULL), 'motor-alimentacion-escape', true, 2, CURRENT_TIMESTAMP);

-- L3 (under bloque motor)
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Cigüeñal y bielas', 'Cigüeñal, bielas, cojinetes', (SELECT min(id) FROM categories WHERE slug = 'motor-bloque'), 'motor-bloque-cigueñal', true, 1, CURRENT_TIMESTAMP);

-- L4
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Cojinetes y casquillos de biela', 'Cojinetes, casquillos y arandelas', (SELECT min(id) FROM categories WHERE slug = 'motor-bloque-cigueñal'), 'motor-bloque-cigueñal-cojinetes', true, 1, CURRENT_TIMESTAMP);

-- Second branch: frenos (L2–L4)
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Freno a disco', 'Discos, pinzas, pastillas de disco', (SELECT min(id) FROM categories WHERE slug = 'sistema-frenos' AND parent_id IS NULL), 'frenos-disco', true, 1, CURRENT_TIMESTAMP),
('Freno a tambor', 'Tambores, zapatas, mordazas', (SELECT min(id) FROM categories WHERE slug = 'sistema-frenos' AND parent_id IS NULL), 'frenos-tambor', true, 2, CURRENT_TIMESTAMP);

INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Eje delantero (disco)', 'Componentes freno delantero a disco', (SELECT min(id) FROM categories WHERE slug = 'frenos-disco'), 'frenos-disco-delantero', true, 1, CURRENT_TIMESTAMP);

INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Kits freno de disco delantero', 'Kits completos y pastillas eje delantero', (SELECT min(id) FROM categories WHERE slug = 'frenos-disco-delantero'), 'frenos-disco-delantero-kits', true, 1, CURRENT_TIMESTAMP);
