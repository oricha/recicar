-- Search and filtering foundation.
CREATE INDEX idx_products_search_tsv ON products
USING GIN (to_tsvector('simple', coalesce(name, '') || ' ' || coalesce(part_number, '') || ' ' || coalesce(oem_number, '')));

CREATE INDEX idx_products_name_trgm ON products (name);
CREATE INDEX idx_products_part_number_trgm ON products (part_number);
CREATE INDEX idx_products_oem_number_trgm ON products (oem_number);

CREATE TABLE saved_searches (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    search_query VARCHAR(255) NOT NULL,
    filters_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_saved_searches_user_created ON saved_searches(user_id, created_at DESC);
