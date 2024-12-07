package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.client.ClientDto;
import org.mango.auth.server.dto.client.UserClientRoleLightDto;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.AccountType;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.exception.NotFoundException;
import org.mango.auth.server.exception.UnsupportedException;
import org.mango.auth.server.mapper.ClientMapper;
import org.mango.auth.server.mapper.UserClientRoleMapper;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserClientRoleServiceImpl implements UserClientRoleService {

    public static final UUID MANGO_CLIENT_ID = UUID.fromString("3a5bef20-b6c1-4b83-8844-058e123d479d");

    private final UserClientRoleRepository userClientRoleRepository;
    private final UserClientRoleMapper userClientRoleMapper;
    private final ClientMapper clientMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserClientRole> findByUserEmailAndClientId(String email, UUID clientId) {
        return userClientRoleRepository.findByUser_EmailAndClient_Id(email, clientId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UserClientRole> findByUserEmailAndClientName(String email, String clientName) {
        return userClientRoleRepository.findByUser_EmailAndClient_Name(email, clientName);
    }

    @Override
    @Transactional(readOnly = true)
    public UserClientRole getByUserEmailAndClientId(String email, UUID clientId) {
        return findByUserEmailAndClientId(email, clientId)
                .orElseThrow(() -> new NotFoundException("User not found for the specified client"));
    }

    @Transactional(readOnly = true)
    @Override
    public UserClientRole getByUserEmailAndMangoClient(String email) {
        return userClientRoleRepository.findByUser_EmailAndClient_Id(email, MANGO_CLIENT_ID)
                .orElseThrow(() -> new NotFoundException("User: %s is not registered in Mango client".formatted(email)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserClientRole> findAllByUserEmailAndClientIdAndRoleIn(String email, UUID clientId, List<Role> roles) {
        return userClientRoleRepository.findAllByUser_EmailAndClient_IdAndRoleIn(email, clientId, roles);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserClientRole> findByUser(User user) {
        return userClientRoleRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    @Override
    public ClientDto getById(UUID id, UserDetailsImpl userDetails) {
        validateUserAccountAccess(userDetails);
        List<UserClientRole> userClientRoles =
                userClientRoleRepository.findAllByUser_EmailAndClient_IdAndRoleIn(userDetails.getEmail(), id, List.of(Role.OWNER, Role.ADMIN));

        if (userClientRoles.isEmpty()) {
            throw new AccessDeniedException("User does not have access to read client: %s".formatted(id.toString()));
        }

        return clientMapper.map(userClientRoles.get(0).getClient());
    }

    @Override
    @Transactional(readOnly = true)
    public UserClientRole getByUser(User user) {
        return findByUser(user)
                .orElseThrow(() -> new NotFoundException("User client role not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserClientRole> findAllByClientId(UUID clientId, Pageable pageable) {
        return userClientRoleRepository.findAllByClient_Id(clientId, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserClientRoleLightDto> getUserClientsWhereIsAdminOrOwner(UserDetailsImpl userDetails) {
        validateUserAccountAccess(userDetails);
        List<UserClientRole> userClientRoles = userClientRoleRepository.findAllByUser_EmailAndRoleIn(userDetails.getEmail(), List.of(Role.ADMIN, Role.OWNER));
        return userClientRoles.stream().map(userClientRoleMapper::map).toList();
    }

    private void validateUserAccountAccess(UserDetailsImpl userDetails) {
        if (AccountType.SERVICE.equals(userDetails.getAccountType())) {
            throw new UnsupportedException("Method is not allowed");
        }
    }

    @Override
    @Transactional
    public UserClientRole save(UserClientRole userClientRole) {
        return userClientRoleRepository.save(userClientRole);
    }

    @Override
    @Transactional
    public void deleteAll() {
        userClientRoleRepository.deleteAll();
    }

}
