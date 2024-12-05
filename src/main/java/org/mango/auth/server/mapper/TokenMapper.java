package org.mango.auth.server.mapper;

import org.mango.auth.server.dto.token.IntrospectTokenResponse;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    @Mapping(target = "email", source = "userDetails.user.email")
    @Mapping(target = "clientId", source = "userDetails.client.id")
    @Mapping(target = "clientName", source = "userDetails.client.name")
    @Mapping(target = "role", source = "userDetails.role")
    @Mapping(target = "registeredAt", source = "userDetails.user.createdAt")
    IntrospectTokenResponse map(UserDetailsImpl userDetails);

}
