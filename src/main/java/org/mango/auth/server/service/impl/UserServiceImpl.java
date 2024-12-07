package org.mango.auth.server.service.impl;

import lombok.AllArgsConstructor;
import org.mango.auth.server.dto.user.UserLightDto;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.AccountType;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.exception.InvalidParameterException;
import org.mango.auth.server.exception.NotFoundException;
import org.mango.auth.server.mapper.UserMapper;
import org.mango.auth.server.repository.UserRepository;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserClientRoleService userClientRoleService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User is not found by id: %s".formatted(id.toString())));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserLightDto> search(UUID clientId, UserDetailsImpl userDetails,  Pageable pageable) {
        if (AccountType.USER.equals(userDetails.getAccountType())) {
            if (clientId == null) {
                throw new InvalidParameterException("clientId is required");
            }
        } else {
            clientId = userDetails.getClient().getId();
        }

        validateAccessForClient(userDetails, clientId);

        Page<UserClientRole> users = userClientRoleService.findAllByClientId(clientId, pageable);
        return users.map(userClientRole -> userMapper.map(userClientRole.getUser(), userClientRole.getRole()));
    }

    private void validateAccessForClient(UserDetailsImpl userDetails, UUID clientId) {
        if (AccountType.USER.equals(userDetails.getAccountType())) {
            List<UserClientRole> userClientRoles = userClientRoleService.findAllByUserEmailAndClientIdAndRoleIn(
                    userDetails.getEmail(), clientId, List.of(Role.ADMIN, Role.OWNER));
            if (userClientRoles.isEmpty()) {
                throw new NotFoundException("Client %s does not exist".formatted(clientId.toString()));
            }
        }
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
