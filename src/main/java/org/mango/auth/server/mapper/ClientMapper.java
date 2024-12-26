package org.mango.auth.server.mapper;

import org.mango.auth.server.dto.client.ClientDto;
import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.dto.client.CreateClientResponse;
import org.mango.auth.server.dto.key.SecretKey;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.service.impl.KeyServiceImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, KeyServiceImpl.class})
public interface ClientMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "publicKey", source = "rsaPublicKey")
    @Mapping(target = "privateKey", source = "rsaPrivateKey")
    @Mapping(target = "apiKeyHash", source = "apiKeyHash")
    Client map(CreateClientRequest request, String rsaPublicKey, String rsaPrivateKey, String apiKeyHash);

    ClientDto map(Client client);

    @Mapping(target = "id", source = "client.id")
    @Mapping(target = "name", source = "client.name")
    @Mapping(target = "createdAt", source = "client.createdAt")
    @Mapping(target = "publicKey", source = "publicKey")
    @Mapping(target = "apiKey", source = "apiKey")
    CreateClientResponse mapToResponse(Client client, String publicKey, String apiKey);

}
