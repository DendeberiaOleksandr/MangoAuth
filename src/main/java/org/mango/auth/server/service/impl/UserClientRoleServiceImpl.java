package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.service.UserClientRoleService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public UserClientRole findByUserEmailAndClientId(String email, UUID clientId) {
        return userClientRoleRepository.findByUser_EmailAndClient_Id(email, clientId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for the specified client"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserClientRole findByUser(User user) {
        return userClientRoleRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User client role not found"));
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
