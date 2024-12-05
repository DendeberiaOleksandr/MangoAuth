package org.mango.auth.server.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.dto.key.SecretKey;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SecretKeyServiceImplTest {

    SecretKeyServiceImpl service = new SecretKeyServiceImpl(new BCryptPasswordEncoder());

    @Test
    void generate() {
        SecretKey key = service.generate();
        assertNotNull(key);
        String encryptedKey = key.encryptedKey();
        String rawKey = key.key();
        assertNotNull(key);
        assertNotNull(encryptedKey);
        assertNotNull(rawKey);
        assertNotEquals(encryptedKey, rawKey);
    }

    @Test
    void matches() {
        SecretKey secretKey = service.generate();

        boolean result = service.matches(secretKey.key(), secretKey.encryptedKey());

        assertTrue(result);
    }

    @Test
    void matches_invalidKey() {
        SecretKey secretKey = service.generate();

        boolean result = service.matches("randomKey", secretKey.encryptedKey());

        assertFalse(result);
    }

}