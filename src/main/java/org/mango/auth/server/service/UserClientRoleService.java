package org.mango.auth.server.service;

import org.mango.auth.server.dto.client.UserClientRoleLightDto;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserClientRoleService {
    List<UserClientRoleLightDto> getUserClientsWhereIsAdminOrOwner(String email);
    UserClientRole save(UserClientRole userClientRole);
    Optional<UserClientRole> findByUserEmailAndClientId(String email, UUID clientId);
    Optional<UserClientRole> findByUser(User user);
    UserClientRole getByUserEmailAndClientId(String email, UUID clientId);
    UserClientRole getByUser(User user);
    void deleteAll();
}
