package org.mango.auth.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mango.auth.server.entity.UserClientRole;
import org.mango.auth.server.repository.UserClientRoleRepository;
import org.mango.auth.server.service.JwtService;
import org.mango.auth.server.service.impl.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserClientRoleRepository userClientRoleRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.getEmailFromToken(token);
        String clientId = jwtService.getClientIdFromToken(token);

        if (email != null && clientId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserClientRole userClientRole = userClientRoleRepository.findByUser_EmailAndClient_Id(email, UUID.fromString(clientId))
                    .orElseThrow(() -> new RuntimeException("UserClientRole not found"));

            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email + "--" + clientId);

            if (jwtService.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
