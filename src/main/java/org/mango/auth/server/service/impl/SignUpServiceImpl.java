package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.dto.SignUpRequest;
import org.mango.auth.server.entity.User;
import org.mango.auth.server.exception.UserAlreadyExistsException;
import org.mango.auth.server.service.SignUpService;
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


    @Override
    public void signUp(SignUpRequest signUpRequest) {
        UUID clientId  = signUpRequest.clientId();
        String email = signUpRequest.email();
        String firstName = signUpRequest.firstName();
        String lastName = signUpRequest.lastName();

        Optional<User> userOptional = userService.findByEmailAndClientId(email,clientId);

        if(userOptional.isPresent()){
            throw new UserAlreadyExistsException("User is already registered by email: %s".formatted(email));
        }

        String password = passwordEncoder.encode(signUpRequest.password());
        User user = userOptional.orElseGet(() -> User.builder().email(email).build());
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        userService.save(user);
    }
}
