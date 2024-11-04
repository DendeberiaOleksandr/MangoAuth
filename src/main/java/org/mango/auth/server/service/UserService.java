package org.mango.auth.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.mango.auth.server.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    void save(User user);

    void deleteAll();

    default User getByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User is not found by email: %s".formatted(email)));
    }

}
