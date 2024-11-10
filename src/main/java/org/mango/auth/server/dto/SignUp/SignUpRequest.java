package org.mango.auth.server.dto.SignUp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SignUpRequest(
        @NotNull UUID clientId,
        @Email @NotBlank String email,
        @Size(min = 8, message = "Password must be at least 8 characters long") String password,
        String firstName,
        String lastName
) {
}
