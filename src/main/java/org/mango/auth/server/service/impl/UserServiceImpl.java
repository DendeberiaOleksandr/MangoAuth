package org.mango.auth.server.service.impl;

import lombok.AllArgsConstructor;
import org.mango.auth.server.dto.user.UserLightDto;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.exception.NotFoundException;
import org.mango.auth.server.mapper.UserMapper;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.repository.UserRepository;
import org.mango.auth.server.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserClientRoleRepository userClientRoleRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User is not found by id: %s".formatted(id.toString())));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserLightDto> search(UUID clientId, Pageable pageable) {
        Page<UserClientRole> users = userClientRoleRepository.findAllByClient_Id(clientId, pageable);
        return users.map(userClientRole -> userMapper.map(userClientRole.getUser(), userClientRole.getRole()));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
