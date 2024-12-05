package org.mango.auth.server.dto.token;

import org.mango.auth.server.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record IntrospectTokenResponse(String email, UUID clientId, String clientName,
                                      Role role, LocalDateTime registeredAt) {
}
