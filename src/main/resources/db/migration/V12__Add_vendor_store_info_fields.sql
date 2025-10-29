-- Add store info fields to vendors table
ALTER TABLE vendors
ADD COLUMN logo_url VARCHAR(500),
ADD COLUMN banner_url VARCHAR(500),
ADD COLUMN contact_phone VARCHAR(20),
ADD COLUMN contact_email VARCHAR(255);
