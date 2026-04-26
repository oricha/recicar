CREATE TABLE newsletter_subscribers (
    id             BIGSERIAL PRIMARY KEY,
    email          VARCHAR(255) NOT NULL,
    subscribed_at  TIMESTAMP NOT NULL,
    source         VARCHAR(100)
);

CREATE UNIQUE INDEX uq_newsletter_subscribers_email ON newsletter_subscribers (email);

COMMENT ON TABLE newsletter_subscribers IS 'Marketing newsletter opt-in (double opt-in not implemented)';
