package org.mango.auth.server.dto.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendUserVerificationEmailRequest {
    @Email
    private String email;
    @NotNull
    private UUID clientId;
}
