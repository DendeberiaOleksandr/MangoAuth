package org.mango.auth.server.dto.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mango.auth.server.enums.Role;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserClientRoleLightDto {

    private UUID clientId;
    private String clientName;
    private Role role;

}
