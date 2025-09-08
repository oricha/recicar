-- Insert a sample vendor first
INSERT INTO vendors (user_id, business_name, tax_id, description, status, commission_rate, created_at, updated_at) 
VALUES (1, 'AutoParts Pro', 'TAX123456', 'Premium auto parts supplier', 'APPROVED', 0.1000, NOW(), NOW())
ON CONFLICT (tax_id) DO NOTHING;

-- Insert sample products for testing
INSERT INTO products (name, description, part_number, oem_number, price, stock_quantity, condition, active, created_at, updated_at, category_id, vendor_id) VALUES
('Brake Pads - Front', 'High-quality front brake pads for optimal stopping power', 'BP-F001', 'OEM-BP001', 45.99, 25, 'NEW', true, NOW(), NOW(), 1, 1),
('Oil Filter', 'Premium oil filter for engine protection', 'OF-001', 'OEM-OF001', 12.99, 50, 'NEW', true, NOW(), NOW(), 2, 1),
('Air Filter', 'High-performance air filter for better engine breathing', 'AF-001', 'OEM-AF001', 18.99, 30, 'NEW', true, NOW(), NOW(), 2, 1),
('Spark Plugs (Set of 4)', 'Iridium spark plugs for improved ignition', 'SP-001', 'OEM-SP001', 32.99, 40, 'NEW', true, NOW(), NOW(), 3, 1),
('Headlight Bulb', 'Bright LED headlight bulb replacement', 'HB-001', 'OEM-HB001', 24.99, 35, 'NEW', true, NOW(), NOW(), 4, 1),
('Windshield Wiper Blades', 'Premium wiper blades for clear visibility', 'WW-001', 'OEM-WW001', 15.99, 60, 'NEW', true, NOW(), NOW(), 5, 1),
('Battery', 'Long-lasting car battery', 'BAT-001', 'OEM-BAT001', 89.99, 15, 'NEW', true, NOW(), NOW(), 6, 1),
('Tire Pressure Sensor', 'TPMS sensor for tire monitoring', 'TPS-001', 'OEM-TPS001', 28.99, 45, 'NEW', true, NOW(), NOW(), 7, 1);
