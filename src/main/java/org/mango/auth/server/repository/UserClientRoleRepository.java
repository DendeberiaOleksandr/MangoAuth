package org.mango.auth.server.repository;

import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserClientRoleRepository extends JpaRepository<UserClientRole, Long> {
    List<UserClientRole> findAllByUser_EmailAndRoleIn(String email, List<Role> roles);
    Page<UserClientRole> findAllByClient_Id(UUID clientId, Pageable pageable);
    Optional<UserClientRole> findByUser_EmailAndClient_Id(String email, UUID clientId);
    Optional<UserClientRole> findByUser_EmailAndClient_Name(String email, String clientName);
    Optional<UserClientRole> findByUser(User user);
}
