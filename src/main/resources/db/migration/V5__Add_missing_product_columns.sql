-- Add missing columns to products table
ALTER TABLE products ADD COLUMN old_price NUMERIC(10,2);
ALTER TABLE products ADD COLUMN discount INTEGER DEFAULT 0;

-- Update existing records to have default values
UPDATE products SET discount = 0 WHERE discount IS NULL;
