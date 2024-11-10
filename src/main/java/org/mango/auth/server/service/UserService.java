package org.mango.auth.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.mango.auth.server.dto.user.UserLightDto;
import org.mango.auth.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Page<UserLightDto> search(UUID clientId, Pageable pageable);

    Optional<User> findByEmail(String email);

    User save(User user);

    void deleteAll();

    default User getByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User is not found by email: %s".formatted(email)));
    }

}
