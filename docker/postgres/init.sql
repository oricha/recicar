-- Initialize the marketplace database
-- This script runs when the PostgreSQL container starts for the first time

-- Create additional databases for testing if needed
CREATE DATABASE marketplace_test;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE marketplace_dev TO marketplace_user;
GRANT ALL PRIVILEGES ON DATABASE marketplace_test TO marketplace_user;