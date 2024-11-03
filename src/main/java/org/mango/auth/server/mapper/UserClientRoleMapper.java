package org.mango.auth.server.mapper;

import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserClientRoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role")
    UserClientRole map(Client client, User user, Role role);

}
