CREATE TABLE vehicle_info (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE REFERENCES products(id) ON DELETE CASCADE,
    make VARCHAR(100),
    model VARCHAR(100),
    generation VARCHAR(100),
    production_year INTEGER,
    engine_displacement_cc INTEGER,
    engine_power_kw INTEGER,
    transmission VARCHAR(50),
    drivetrain VARCHAR(50),
    fuel_type VARCHAR(50),
    body_type VARCHAR(50),
    color VARCHAR(50),
    vin VARCHAR(64),
    mileage_km INTEGER,
    doors INTEGER,
    photo_urls TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vehicle_info_product_id ON vehicle_info(product_id);
