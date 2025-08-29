-- Add missing columns to products table
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS old_price NUMERIC(10,2),
ADD COLUMN IF NOT EXISTS discount INTEGER DEFAULT 0;

-- Update existing records to have default values
UPDATE products SET discount = 0 WHERE discount IS NULL;
