-- Portal navigation structure: brand and model navigation support
CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(512),
    country VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE brand_models (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT NOT NULL REFERENCES brands(id) ON DELETE CASCADE,
    model_name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    generation VARCHAR(100),
    year_from INTEGER,
    year_to INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_brand_models_brand_slug UNIQUE (brand_id, slug)
);

CREATE INDEX idx_brands_slug ON brands(slug);
CREATE INDEX idx_models_brand ON brand_models(brand_id);
