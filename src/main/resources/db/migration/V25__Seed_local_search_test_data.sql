-- Seed deterministic products and compatibility rows used by local search/E2E scenarios.
-- Idempotent by part_number and email so it can run safely on an existing local PostgreSQL DB.

INSERT INTO users (email, password_hash, first_name, last_name, phone, role, email_verified, active)
SELECT 'search-vendor@recicar.com',
       '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa',
       'Search',
       'Vendor',
       '+34600000999',
       'VENDOR',
       true,
       true
WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'search-vendor@recicar.com'
);

INSERT INTO vendors (user_id, business_name, tax_id, description, status, commission_rate, created_at, updated_at)
SELECT u.id,
       'Search Fixtures Vendor',
       'SEARCH-FIXTURE-001',
       'Vendor seeded for local search and checkout fixtures',
       'APPROVED',
       0.1000,
       NOW(),
       NOW()
FROM users u
WHERE u.email = 'search-vendor@recicar.com'
  AND NOT EXISTS (
      SELECT 1
      FROM vendors v
      WHERE v.tax_id = 'SEARCH-FIXTURE-001'
  );

INSERT INTO products (name, description, part_number, oem_number, price, stock_quantity, condition, active, created_at, updated_at, category_id, vendor_id, weight_kg)
SELECT seed.name,
       seed.description,
       seed.part_number,
       seed.oem_number,
       seed.price,
       seed.stock_quantity,
       seed.product_condition,
       true,
       NOW(),
       NOW(),
       c.id,
       v.id,
       seed.weight_kg
FROM (
    VALUES
        ('Brake Pad Premium Front', 'Brake Pad search fixture for exact and partial part number scenarios', 'P12345', 'OEM12345', 59.90, 12, 'NEW', 'brake-pads', 2.100),
        ('Brake Pad Economy Rear', 'Secondary brake search fixture to guarantee general search pagination/results', 'P12399', 'OEM12399', 34.50, 18, 'USED', 'brake-pads', 1.950),
        ('Engine Filter for Camry', 'Filter fixture for Toyota Camry vehicle and part-name scenarios', 'FILT-CAM-20', 'OEM-FILT-20', 21.90, 20, 'NEW', 'oil-filters', 0.700)
) AS seed(name, description, part_number, oem_number, price, stock_quantity, product_condition, category_slug, weight_kg)
JOIN categories c
  ON c.slug = seed.category_slug
JOIN vendors v
  ON v.tax_id = 'SEARCH-FIXTURE-001'
WHERE NOT EXISTS (
    SELECT 1
    FROM products p
    WHERE p.part_number = seed.part_number
);

INSERT INTO vehicle_compatibility (product_id, make, model, year_from, year_to, engine, created_at)
SELECT p.id,
       compatibility.make,
       compatibility.model,
       compatibility.year_from,
       compatibility.year_to,
       compatibility.engine,
       NOW()
FROM (
    VALUES
        ('P12345', 'Honda', 'Civic', 2016, 2021, '1.8L'),
        ('FILT-CAM-20', 'Toyota', 'Camry', 2018, 2023, '2.0L')
) AS compatibility(part_number, make, model, year_from, year_to, engine)
JOIN products p
  ON p.part_number = compatibility.part_number
WHERE NOT EXISTS (
    SELECT 1
    FROM vehicle_compatibility vc
    WHERE vc.product_id = p.id
      AND vc.make = compatibility.make
      AND vc.model = compatibility.model
      AND vc.engine = compatibility.engine
);
