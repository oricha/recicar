-- Vehicle dimension tables for DataLoader JSON seed and V27 brand expansion (must run before V27).

CREATE TABLE IF NOT EXISTS car_make (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS car_model (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    make_id BIGINT NOT NULL REFERENCES car_make (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_car_model_make_id ON car_model (make_id);

CREATE TABLE IF NOT EXISTS car_trim (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    model_id BIGINT NOT NULL REFERENCES car_model (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_car_trim_model_id ON car_trim (model_id);
