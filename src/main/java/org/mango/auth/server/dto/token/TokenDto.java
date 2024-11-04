package org.mango.auth.server.dto.token;

public record TokenDto(String token,
                       long expiresAt,
                       long issuedAt
) {
}
