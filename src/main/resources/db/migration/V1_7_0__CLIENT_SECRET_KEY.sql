ALTER TABLE client ADD COLUMN IF NOT EXISTS secret_key TEXT;
ALTER TABLE client DROP COLUMN IF EXISTS api_key;
