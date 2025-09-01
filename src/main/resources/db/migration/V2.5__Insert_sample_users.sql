-- Insert sample users for testing
INSERT INTO users (email, password_hash, first_name, last_name, phone, role, email_verified, active) VALUES
('admin@recicar.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Admin', 'User', '+34600000000', 'ADMIN', true, true),
('vendor@recicar.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Vendor', 'User', '+34600000001', 'VENDOR', true, true),
('customer@recicar.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Customer', 'User', '+34600000002', 'CUSTOMER', true, true)
ON CONFLICT (email) DO NOTHING;
