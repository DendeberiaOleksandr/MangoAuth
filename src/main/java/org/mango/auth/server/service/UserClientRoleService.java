package org.mango.auth.server.service;

import org.mango.auth.server.entity.UserClientRole;

import java.util.Optional;
import java.util.UUID;

public interface UserClientRoleService {
    Optional<UserClientRole> findByUser_EmailAndClient_Id(String email, UUID clientId);
    UserClientRole save(UserClientRole userClientRole);
    void deleteAll();
}
