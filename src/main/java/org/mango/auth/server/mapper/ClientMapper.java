package org.mango.auth.server.mapper;

import org.mango.auth.server.dto.client.ClientDto;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.dto.client.CreateClientResponse;
import org.mango.auth.server.dto.key.SecretKey;
import org.mango.auth.server.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface ClientMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "secretKey", source = "secretKey.encryptedKey")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    Client map(CreateClientRequest request, SecretKey secretKey);

    ClientDto map(Client client);

    @Mapping(target = "id", source = "client.id")
    @Mapping(target = "name", source = "client.name")
    @Mapping(target = "createdAt", source = "client.createdAt")
    @Mapping(target = "secretKey", source = "secretKey.key")
    CreateClientResponse mapToResponse(Client client, SecretKey secretKey);

}
