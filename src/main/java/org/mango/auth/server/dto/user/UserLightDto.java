package org.mango.auth.server.dto.user;

import org.mango.auth.server.enums.Role;
import org.mango.auth.server.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserLightDto(UUID id, String firstName, String lastName, String email,
                           Role role, UserStatus userStatus, LocalDateTime registeredAt) {
}
