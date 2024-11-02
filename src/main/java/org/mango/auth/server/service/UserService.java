package org.mango.auth.server.service;

import org.mango.auth.server.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndClientId(String email, UUID clientId);

    User save(User user);

}
