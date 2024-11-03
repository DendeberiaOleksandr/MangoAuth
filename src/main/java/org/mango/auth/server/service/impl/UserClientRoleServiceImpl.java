package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserClientRoleServiceImpl implements UserClientRoleService {
    private final UserClientRoleRepository userClientRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserClientRole> findByUser_EmailAndClient_Id(String email, UUID clientId) {
        return userClientRoleRepository.findByUser_EmailAndClient_Id(email, clientId);
    }

    @Transactional
    @Override
    public UserClientRole save(UserClientRole userClientRole) {
        return userClientRoleRepository.save(userClientRole);
    }


}
