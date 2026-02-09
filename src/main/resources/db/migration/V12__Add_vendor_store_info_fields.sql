-- Add store info fields to vendors table (separate statements for H2 compatibility)
ALTER TABLE vendors ADD COLUMN logo_url VARCHAR(500);
ALTER TABLE vendors ADD COLUMN banner_url VARCHAR(500);
ALTER TABLE vendors ADD COLUMN contact_phone VARCHAR(20);
ALTER TABLE vendors ADD COLUMN contact_email VARCHAR(255);
