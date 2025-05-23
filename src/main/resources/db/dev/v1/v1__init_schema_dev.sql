CREATE TABLE IF NOT EXISTS mini_urls (
    mini_key VARCHAR(32) PRIMARY KEY,
    full_url TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_mini_urls_full_url ON mini_urls(full_url);

-- Optionally, uncomment and insert test data for dev (default miniKey length is 8 characters)
-- Sample data for development/testing:
-- INSERT INTO mini_urls (mini_key, full_url) VALUES
--   ('devkey01', 'https://example1.com'),
--   ('devkey02', 'https://www.wikipedia.org'),
--   ('devkey03', 'https://www.github.com'),
--   ('devkey04', 'https://www.stackoverflow.com'),
--   ('devkey05', 'https://news.ycombinator.com'),
--   ('devkey06', 'https://www.reddit.com'),
--   ('devkey07', 'https://www.nytimes.com/section/technology'),
--   ('devkey08', 'https://www.youtube.com/watch?v=BNirQXe8HOA&ab_channel=ChakaKhan'),
--   ('devkey09', 'https://www.linkedin.com'),
--   ('devkey10', 'https://www.openai.com/research/');
