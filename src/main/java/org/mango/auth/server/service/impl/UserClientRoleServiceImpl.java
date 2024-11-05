package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.entity.User;
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
    public Optional<UserClientRole> findByUserEmailAndClientId(String email, UUID clientId) {
        return userClientRoleRepository.findByUser_EmailAndClient_Id(email, clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserClientRole findByUser(User user) {
        return userClientRoleRepository.findByUser(user).orElseThrow(() -> new RuntimeException());
    }


    @Override
    @Transactional
    public void save(UserClientRole userClientRole) {
        userClientRoleRepository.save(userClientRole);
    }

    @Override
    @Transactional
    public void deleteAll() {
        userClientRoleRepository.deleteAll();
    }

}
