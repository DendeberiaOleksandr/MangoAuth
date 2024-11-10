package org.mango.auth.server.dto.token;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TokenRequest(@Email(message = "Invalid email format")
                           @NotBlank(message = "Email is required")
                           String email,
                           @NotBlank(message = "Password is required")
                           @Size(min = 8, message = "Password must be at least 8 characters long")
                           String password,
                           UUID clientId
) {
}
