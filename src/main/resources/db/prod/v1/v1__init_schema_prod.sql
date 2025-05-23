CREATE TABLE IF NOT EXISTS mini_urls (
    mini_key VARCHAR(32) PRIMARY KEY,
    full_url TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_mini_urls_full_url ON mini_urls(full_url);
