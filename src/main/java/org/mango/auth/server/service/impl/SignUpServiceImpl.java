package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.SignUp.SignUpRequest;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.exception.UserAlreadyExistsException;
import org.mango.auth.server.mapper.UserClientRoleMapper;
import org.mango.auth.server.mapper.UserMapper;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.SignUpService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserClientRoleService userClientRoleService;
    private final ClientService clientService;
    private final UserMapper userMapper;
    private final UserClientRoleMapper userClientRoleMapper;


    @Override
    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        UUID clientId = signUpRequest.clientId();
        String email = signUpRequest.email();

        try {
            userClientRoleService.findByUserEmailAndClientId(email, clientId);
            throw new UserAlreadyExistsException("User is already registered by email: %s in client: %s"
                    .formatted(email, clientId.toString()));
        } catch (UsernameNotFoundException e) {
            Client client = clientService.getById(clientId);

            User user = userMapper.map(signUpRequest, passwordEncoder);
            userService.save(user);

            UserClientRole userClientRole = userClientRoleMapper.map(client, user, Role.USER);
            userClientRoleService.save(userClientRole);
        }
    }
}
