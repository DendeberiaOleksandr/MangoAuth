package org.mango.auth.server.service.impl;

import org.mango.auth.server.dto.key.SecretKey;
import org.mango.auth.server.service.SecretKeyService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class SecretKeyServiceImpl implements SecretKeyService {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final PasswordEncoder passwordEncoder;

    public SecretKeyServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SecretKey generate() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String secretKey = base64Encoder.encodeToString(bytes);
        return new SecretKey(secretKey, passwordEncoder.encode(secretKey));
    }

    @Override
    public boolean matches(String secretKey, String encodedSecretKey) {
        return passwordEncoder.matches(secretKey, encodedSecretKey);
    }
}
