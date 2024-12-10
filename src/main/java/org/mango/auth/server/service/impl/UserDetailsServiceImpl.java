package org.mango.auth.server.service.impl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.security.UserDetailsImpl;
import org.mango.auth.server.service.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserClientRoleRepository userClientRoleRepository;
    private final JwtService jwtService;

    public UserDetails loadByAccessToken(String accessToken) {
        try {
            Claims claims = jwtService.getClaimsFromToken(accessToken);
            String email = claims.getSubject();
            String clientId = claims.get("CLIENT_ID", String.class);
            if (StringUtils.hasText(email) && StringUtils.hasText(clientId)) {
                return loadUserByUsername(email + "--" + clientId);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

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
