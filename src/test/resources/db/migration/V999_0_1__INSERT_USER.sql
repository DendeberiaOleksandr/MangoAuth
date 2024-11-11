INSERT INTO users (id, email, password, created_at, updated_at, email_verification_code)
VALUES ('a3dc28ce-39e5-4797-8d7a-f6809b6f1f03'::uuid, 'testUser1110@example.com', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'jfdsjisfjjlsjfsljfsji');

INSERT INTO users_client_role(id, user_id, client_id, role)
VALUES ('dbf52753-f0e2-4171-8f05-bb216e2864d7'::uuid, 'a3dc28ce-39e5-4797-8d7a-f6809b6f1f03'::uuid,
'9c3c4b6a-d5f9-4d92-857e-55d44dcdeab9'::uuid, 'USER');
