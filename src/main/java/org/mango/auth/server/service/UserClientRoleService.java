package org.mango.auth.server.service;

import org.mango.auth.server.dto.client.ClientDto;
import org.mango.auth.server.dto.client.UserClientRoleLightDto;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserClientRoleService {
    Page<UserClientRole> findAllByClientId(UUID clientId, Pageable pageable);
    List<UserClientRoleLightDto> getUserClientsWhereIsAdminOrOwner(String email);
    UserClientRole save(UserClientRole userClientRole);
    Optional<UserClientRole> findByUserEmailAndClientId(String email, UUID clientId);
    Optional<UserClientRole> findByUserEmailAndClientName(String email, String clientName);
    Optional<UserClientRole> findByUser(User user);
    ClientDto getById(UUID id, UserDetailsImpl userDetails);
    UserClientRole getByUserEmailAndClientId(String email, UUID clientId);
    UserClientRole getByUserEmailAndMangoClient(String email);
    List<UserClientRole> findAllByUserEmailAndClientIdAndRoleIn(String email, UUID clientId, List<Role> roles);
    UserClientRole getByUser(User user);
    void deleteAll();
}
