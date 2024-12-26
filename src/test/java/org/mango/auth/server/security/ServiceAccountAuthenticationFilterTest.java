package org.mango.auth.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.controller.handler.ApiErrorHandler;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.KeyService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class ServiceAccountAuthenticationFilterTest {

    @InjectMocks
    ServiceAccountAuthenticationFilter filter;

    @Mock
    ClientService clientService;

    @Mock
    ApiErrorHandler apiErrorHandler;

    @Mock
    KeyService keyService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        // given
        UUID clientId = UUID.randomUUID();
        String apiKey = Instancio.create(String.class);
        Client client = Instancio.create(Client.class);

        when(request.getHeader(AUTHORIZATION)).thenReturn(clientId.toString() + ":" + apiKey);
        when(clientService.getById(clientId))
                .thenReturn(client);
        when(keyService.validateApiKey(apiKey, client.getApiKeyHash()))
                .thenReturn(true);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        assertNotNull(authentication);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
        Object principal = usernamePasswordAuthenticationToken.getPrincipal();
        assertInstanceOf(UserDetailsImpl.class, principal);
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        assertEquals(client, userDetails.getClient());
    }

}