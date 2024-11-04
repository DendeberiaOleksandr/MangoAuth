package org.mango.auth.server.dto.token;

import java.util.UUID;

public record TokenRequest(String email,
                           String password,
                           UUID clientId
) {
}
