package org.mango.auth.server.dto.token;

import org.mango.auth.server.enums.Role;

import java.util.UUID;

public record TokenResponse(UUID id,
                            String email,
                            Role role,
                            TokenDto accessToken,
                            TokenDto refreshToken
) {
}
