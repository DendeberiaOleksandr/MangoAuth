ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_code VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_code_last_sent_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_code_sent_times INT NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_code_last_entered_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_code_entered_times INT NOT NULL DEFAULT 0;
