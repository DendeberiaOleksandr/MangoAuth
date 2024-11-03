package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.SignUpRequest;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.exception.UserAlreadyExistsException;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.SignUpService;
import org.mango.auth.server.service.UserClientRoleService;
import org.mango.auth.server.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserClientRoleService userClientRoleService;
    private final ClientService clientService;


    @Override
    public void signUp(SignUpRequest signUpRequest) {
        UUID clientId = signUpRequest.clientId();
        String email = signUpRequest.email();
        String firstName = signUpRequest.firstName();
        String lastName = signUpRequest.lastName();

        Client client = clientService.getById(clientId);

        Optional<UserClientRole> userClientRoleOptional = userClientRoleService
                .findByUser_EmailAndClient_Id(email, clientId);

        if (userClientRoleOptional.isPresent()) {
            throw new UserAlreadyExistsException("User is already registered by email: %s in client: %s"
                    .formatted(email, clientId.toString()));
        }

        String password = passwordEncoder.encode(signUpRequest.password());

        User user = User.builder()
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        userService.save(user);

        userClientRoleService.save(UserClientRole.builder()
                .client(client)
                .user(user)
                .role(Role.USER)
                .build());
    }
}
