package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.client.UserClientRoleLightDto;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.exception.NotFoundException;
import org.mango.auth.server.mapper.UserClientRoleMapper;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserClientRoleServiceImpl implements UserClientRoleService {

    private final UserClientRoleRepository userClientRoleRepository;
    private final UserClientRoleMapper userClientRoleMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserClientRole> findByUserEmailAndClientId(String email, UUID clientId) {
        return userClientRoleRepository.findByUser_EmailAndClient_Id(email, clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserClientRole getByUserEmailAndClientId(String email, UUID clientId) {
        return findByUserEmailAndClientId(email, clientId)
                .orElseThrow(() -> new NotFoundException("User not found for the specified client"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserClientRole> findByUser(User user) {
        return userClientRoleRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserClientRole getByUser(User user) {
        return findByUser(user)
                .orElseThrow(() -> new NotFoundException("User client role not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserClientRoleLightDto> getUserClientsWhereIsAdminOrOwner(String email) {
        List<UserClientRole> userClientRoles = userClientRoleRepository.findAllByUser_EmailAndRoleIn(email, List.of(Role.ADMIN, Role.OWNER));
        return userClientRoles.stream().map(userClientRoleMapper::map).toList();
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
