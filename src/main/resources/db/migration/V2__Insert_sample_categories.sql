-- Insert sample categories for the Car Parts Marketplace
-- This migration adds a hierarchical category structure

-- Root categories
INSERT INTO categories (name, description, slug, active, sort_order, created_at) VALUES
('Engine Parts', 'Components and parts for vehicle engines', 'engine-parts', true, 1, CURRENT_TIMESTAMP),
('Brake System', 'Brake pads, discs, calipers and brake components', 'brake-system', true, 2, CURRENT_TIMESTAMP),
('Suspension', 'Shock absorbers, springs, and suspension components', 'suspension', true, 3, CURRENT_TIMESTAMP),
('Electrical', 'Batteries, alternators, starters, and electrical components', 'electrical', true, 4, CURRENT_TIMESTAMP),
('Body Parts', 'Doors, panels, bumpers, and exterior body components', 'body-parts', true, 5, CURRENT_TIMESTAMP),
('Interior', 'Seats, dashboard, trim, and interior accessories', 'interior', true, 6, CURRENT_TIMESTAMP),
('Transmission', 'Gearboxes, clutches, and transmission components', 'transmission', true, 7, CURRENT_TIMESTAMP),
('Exhaust System', 'Mufflers, catalytic converters, and exhaust pipes', 'exhaust-system', true, 8, CURRENT_TIMESTAMP),
('Cooling System', 'Radiators, water pumps, and cooling components', 'cooling-system', true, 9, CURRENT_TIMESTAMP),
('Wheels & Tires', 'Wheels, tires, and wheel accessories', 'wheels-tires', true, 10, CURRENT_TIMESTAMP);

-- Engine Parts subcategories
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Air Filters', 'Engine air filters and intake components', (SELECT id FROM categories WHERE slug = 'engine-parts'), 'air-filters', true, 1, CURRENT_TIMESTAMP),
('Oil Filters', 'Engine oil filters and oil system components', (SELECT id FROM categories WHERE slug = 'engine-parts'), 'oil-filters', true, 2, CURRENT_TIMESTAMP),
('Fuel Filters', 'Fuel filters and fuel system components', (SELECT id FROM categories WHERE slug = 'engine-parts'), 'fuel-filters', true, 3, CURRENT_TIMESTAMP),
('Spark Plugs', 'Spark plugs and ignition components', (SELECT id FROM categories WHERE slug = 'engine-parts'), 'spark-plugs', true, 4, CURRENT_TIMESTAMP),
('Belts & Hoses', 'Engine belts, hoses, and drive components', (SELECT id FROM categories WHERE slug = 'engine-parts'), 'belts-hoses', true, 5, CURRENT_TIMESTAMP);

-- Brake System subcategories
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Brake Pads', 'Front and rear brake pads for all vehicle types', (SELECT id FROM categories WHERE slug = 'brake-system'), 'brake-pads', true, 1, CURRENT_TIMESTAMP),
('Brake Discs', 'Brake discs and rotors for optimal braking performance', (SELECT id FROM categories WHERE slug = 'brake-system'), 'brake-discs', true, 2, CURRENT_TIMESTAMP),
('Brake Calipers', 'Brake calipers and caliper components', (SELECT id FROM categories WHERE slug = 'brake-system'), 'brake-calipers', true, 3, CURRENT_TIMESTAMP),
('Brake Fluid', 'Brake fluid and hydraulic brake components', (SELECT id FROM categories WHERE slug = 'brake-system'), 'brake-fluid', true, 4, CURRENT_TIMESTAMP);

-- Suspension subcategories
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Shock Absorbers', 'Front and rear shock absorbers and struts', (SELECT id FROM categories WHERE slug = 'suspension'), 'shock-absorbers', true, 1, CURRENT_TIMESTAMP),
('Springs', 'Coil springs and leaf springs for all vehicles', (SELECT id FROM categories WHERE slug = 'suspension'), 'springs', true, 2, CURRENT_TIMESTAMP),
('Bushings', 'Suspension bushings and mounting components', (SELECT id FROM categories WHERE slug = 'suspension'), 'bushings', true, 3, CURRENT_TIMESTAMP);

-- Electrical subcategories
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Batteries', 'Car batteries and battery accessories', (SELECT id FROM categories WHERE slug = 'electrical'), 'batteries', true, 1, CURRENT_TIMESTAMP),
('Alternators', 'Alternators and charging system components', (SELECT id FROM categories WHERE slug = 'electrical'), 'alternators', true, 2, CURRENT_TIMESTAMP),
('Starters', 'Starter motors and starting system components', (SELECT id FROM categories WHERE slug = 'electrical'), 'starters', true, 3, CURRENT_TIMESTAMP),
('Lights', 'Headlights, taillights, and automotive lighting', (SELECT id FROM categories WHERE slug = 'electrical'), 'lights', true, 4, CURRENT_TIMESTAMP);

-- Body Parts subcategories
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Doors', 'Car doors and door components', (SELECT id FROM categories WHERE slug = 'body-parts'), 'doors', true, 1, CURRENT_TIMESTAMP),
('Bumpers', 'Front and rear bumpers and bumper components', (SELECT id FROM categories WHERE slug = 'body-parts'), 'bumpers', true, 2, CURRENT_TIMESTAMP),
('Mirrors', 'Side mirrors and rearview mirrors', (SELECT id FROM categories WHERE slug = 'body-parts'), 'mirrors', true, 3, CURRENT_TIMESTAMP),
('Hoods', 'Engine hoods and hood components', (SELECT id FROM categories WHERE slug = 'body-parts'), 'hoods', true, 4, CURRENT_TIMESTAMP);

-- Wheels & Tires subcategories
INSERT INTO categories (name, description, parent_id, slug, active, sort_order, created_at) VALUES
('Alloy Wheels', 'Alloy wheels and rims for all vehicle types', (SELECT id FROM categories WHERE slug = 'wheels-tires'), 'alloy-wheels', true, 1, CURRENT_TIMESTAMP),
('Steel Wheels', 'Steel wheels and basic rims', (SELECT id FROM categories WHERE slug = 'wheels-tires'), 'steel-wheels', true, 2, CURRENT_TIMESTAMP),
('Summer Tires', 'Summer tires for optimal warm weather performance', (SELECT id FROM categories WHERE slug = 'wheels-tires'), 'summer-tires', true, 3, CURRENT_TIMESTAMP),
('Winter Tires', 'Winter tires for cold weather and snow conditions', (SELECT id FROM categories WHERE slug = 'wheels-tires'), 'winter-tires', true, 4, CURRENT_TIMESTAMP),
('All-Season Tires', 'All-season tires for year-round use', (SELECT id FROM categories WHERE slug = 'wheels-tires'), 'all-season-tires', true, 5, CURRENT_TIMESTAMP);