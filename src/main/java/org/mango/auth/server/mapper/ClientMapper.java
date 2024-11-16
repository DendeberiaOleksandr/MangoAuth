package org.mango.auth.server.mapper;

import org.mango.auth.server.dto.client.CreateClientRequest;
import org.mango.auth.server.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface ClientMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "apiKey", source = "apiKey")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    Client map(CreateClientRequest request, String apiKey);

}
