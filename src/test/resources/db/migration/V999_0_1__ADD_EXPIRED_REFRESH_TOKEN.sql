INSERT INTO users (id, email, password) VALUES ('a3dc28ce-39e5-4797-8d7a-f6809b6f1f03'::uuid, 'a3dc28ce-39e5-4797-8d7a-f6809b6f1f03', 'a3dc28ce-39e5-4797-8d7a-f6809b6f1f03');
INSERT INTO users_client_role (id, user_id, client_id, role) VALUES ('a3dc28ce-39e5-4797-8d7a-f6809b6f1f03'::uuid, 'a3dc28ce-39e5-4797-8d7a-f6809b6f1f03'::uuid, '9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9'::uuid, 'OWNER');

INSERT INTO refresh_token (id, token, user_id, client_id, issued_at, expiry_at, device_agent)
VALUES (
    gen_random_uuid(),
    'expired_token_example',
    'a3dc28ce-39e5-4797-8d7a-f6809b6f1f03',
    '9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9',
    NOW() - INTERVAL '30 days',
    NOW() - INTERVAL '1 day',
    'User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)'
);