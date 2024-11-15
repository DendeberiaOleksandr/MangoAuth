package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.repository.ClientRepository;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.repository.UserRepository;
import org.mango.auth.server.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final UserClientRoleRepository userClientRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String[] parts = username.split("--");
        if (parts.length != 2) {
            throw new UsernameNotFoundException("Invalid username format. Expected format: email--clientId");
        }

        String email = parts[0];
        UUID clientId;
        try {
            clientId = UUID.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid clientId format");
        }


        UserClientRole userClientRole = userClientRoleRepository.findByUser_EmailAndClient_Id(email, clientId)
                .orElseThrow(() -> new UsernameNotFoundException("UserClientRole not found for email: " + email + " and clientId: " + clientId));


        return new UserDetailsImpl(userClientRole.getUser(), userClientRole.getClient(), userClientRole.getRole());
    }
}
