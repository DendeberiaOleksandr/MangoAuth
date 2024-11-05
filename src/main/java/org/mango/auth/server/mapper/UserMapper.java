package org.mango.auth.server.mapper;

import org.mango.auth.server.dto.SignUpRequest;
import org.mango.auth.server.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(signUpRequest.password()))")
    User map(SignUpRequest signUpRequest, PasswordEncoder passwordEncoder);
}