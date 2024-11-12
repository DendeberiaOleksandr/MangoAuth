CREATE TABLE IF NOT EXISTS email_audit (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    client_id UUID NOT NULL,
    email_from VARCHAR(255) NOT NULL,
    email_subject VARCHAR(255) NOT NULL,
    email_event VARCHAR(255) NOT NULL,
    email_event_result VARCHAR(255) NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
