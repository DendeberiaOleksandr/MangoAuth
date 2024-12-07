package org.mango.auth.server.dto.client;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateClientResponse(UUID id, String name, String secretKey, LocalDateTime createdAt) {
}
