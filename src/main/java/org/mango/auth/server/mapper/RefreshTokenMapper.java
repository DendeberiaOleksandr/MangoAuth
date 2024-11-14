package org.mango.auth.server.mapper;

import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.RefreshToken;
import org.mango.auth.server.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "client", source = "client")
    @Mapping(target = "deviceAgent", source = "deviceAgent")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "issuedAt", source = "issuedAt")
    @Mapping(target = "expiryAt", source = "expiryAt")
    RefreshToken map(User user, Client client, String deviceAgent, String token, LocalDateTime issuedAt, LocalDateTime expiryAt);

}
