package org.mango.auth.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.controller.handler.ApiErrorHandler;
import org.mango.auth.server.entity.Client;
import org.mango.auth.server.enums.AccountType;
import org.mango.auth.server.enums.Role;
import org.mango.auth.server.exception.BadCredentialsException;
import org.mango.auth.server.service.ClientService;
import org.mango.auth.server.service.SecretKeyService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceAccountAuthenticationFilter extends OncePerRequestFilter {

    public static final String X_CLIENT_ID = "X-Client-Id";
    private final ClientService clientService;
    private final SecretKeyService secretKeyService;
    private final ApiErrorHandler apiErrorHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String clientIdHeader = request.getHeader(X_CLIENT_ID);
        if (!StringUtils.hasText(authHeader) || !StringUtils.hasText(clientIdHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Client client = clientService.getById(UUID.fromString(clientIdHeader));

            boolean matches = secretKeyService.matches(authHeader, client.getSecretKey());
            if (matches) {
                UserDetailsImpl userDetails = new UserDetailsImpl(null, client, Role.ADMIN, AccountType.SERVICE);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                throw new BadCredentialsException("Invalid credentials provided for client: %s".formatted(clientIdHeader));
            }

        } catch (Exception e) {
            apiErrorHandler.handleException(e, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
