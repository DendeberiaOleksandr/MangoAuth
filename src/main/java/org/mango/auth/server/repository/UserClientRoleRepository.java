package org.mango.auth.server.repository;

import org.mango.auth.server.entity.UserClientRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserClientRoleRepository extends JpaRepository<UserClientRole, Long> {
    Optional<UserClientRole> findByUser_EmailAndClient_Id(String email, UUID clientId);
}
