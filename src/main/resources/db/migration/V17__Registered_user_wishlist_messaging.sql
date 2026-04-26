-- Persisted wishlist for registered users; optional merge with session handled in app.
CREATE TABLE wishlist_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_wishlist_user_product UNIQUE (user_id, product_id)
);
CREATE INDEX idx_wishlist_items_user_id ON wishlist_items(user_id);

-- One thread per customer–vendor pair (MVP); optional product for context.
CREATE TABLE vendor_conversations (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vendor_id BIGINT NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_vendor_conversations_customer_vendor UNIQUE (customer_id, vendor_id)
);
CREATE INDEX idx_vendor_conversations_customer ON vendor_conversations(customer_id);
CREATE INDEX idx_vendor_conversations_vendor ON vendor_conversations(vendor_id);

CREATE TABLE vendor_messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES vendor_conversations(id) ON DELETE CASCADE,
    sender_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    body TEXT NOT NULL,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_vendor_messages_conversation ON vendor_messages(conversation_id);
