package org.mango.auth.server.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(@NotBlank String name, @Email String userEmail) {}
