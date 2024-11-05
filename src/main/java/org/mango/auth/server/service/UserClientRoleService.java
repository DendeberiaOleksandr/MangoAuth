package org.mango.auth.server.service;

import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;

import java.util.Optional;
import java.util.UUID;

public interface UserClientRoleService {
    Optional<UserClientRole> findByUserEmailAndClientId(String email, UUID clientId);
    UserClientRole findByUser(User user);
    void save(UserClientRole userClientRole);
    void deleteAll();
}
