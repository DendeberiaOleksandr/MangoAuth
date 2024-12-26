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
import org.mango.auth.server.service.KeyService;
import org.springframework.data.util.Pair;
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

    private final ClientService clientService;
    private final KeyService keyService;
    private final ApiErrorHandler apiErrorHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Pair<String, String> clientIdAndApiKey = parseClientIdAndApiKey(authHeader);

            if (clientIdAndApiKey != null) {
                UUID clientId = UUID.fromString(clientIdAndApiKey.getFirst());
                Client client = clientService.getById(clientId);
                String apiKey = clientIdAndApiKey.getSecond();

                boolean isValid = keyService.validateApiKey(apiKey, client.getApiKeyHash());
                if (isValid) {
                    UserDetailsImpl userDetails = new UserDetailsImpl(null, client, Role.ADMIN, AccountType.SERVICE);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    throw new BadCredentialsException("Invalid credentials provided");
                }
            }

        } catch (Exception e) {
            apiErrorHandler.handleException(e, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Pair<String, String> parseClientIdAndApiKey(String authorizationHeader) {
        String[] split = authorizationHeader.split(":");
        if (split.length == 2) {
            return Pair.of(split[0], split[1]);
        }
        return null;
    }
}
