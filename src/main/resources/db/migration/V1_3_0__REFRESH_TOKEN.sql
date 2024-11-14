CREATE TABLE refresh_token (
    id UUID PRIMARY KEY ,
    token TEXT UNIQUE NOT NULL,
    user_id UUID NOT NULL,
    client_id UUID NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    expiry_at TIMESTAMP NOT NULL,
    device_agent VARCHAR(255),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);