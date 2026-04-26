-- Breakdown for checkout: service fee (marketplace) separate from VAT.
ALTER TABLE orders ADD COLUMN service_fee NUMERIC(12, 2) NOT NULL DEFAULT 0;
