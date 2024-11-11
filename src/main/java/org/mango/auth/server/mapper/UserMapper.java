package org.mango.auth.server.mapper;

import org.mango.auth.server.dto.SignUp.SignUpRequest;
import org.mango.auth.server.dto.user.UserLightDto;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.enums.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", imports = { UserStatus.class })
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(signUpRequest.password()))")
    @Mapping(target = "userStatus", expression = "java(UserStatus.UNVERIFIED)")
    User map(SignUpRequest signUpRequest, PasswordEncoder passwordEncoder);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "userStatus", source = "user.userStatus")
    @Mapping(target = "registeredAt", source = "user.createdAt")
    UserLightDto map(User user, Role role);
}