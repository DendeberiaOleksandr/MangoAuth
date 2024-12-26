package org.mango.auth.server.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mango.auth.server.dto.key.SecretKey;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class KeyServiceImplTest {

    KeyServiceImpl service = new KeyServiceImpl(new BCryptPasswordEncoder());

    KeyServiceImplTest() throws NoSuchAlgorithmException {}

    @Test
    void generateApiKey() {
        SecretKey key = service.generateApiKey();
        assertNotNull(key);
        String encryptedKey = key.keyHash();
        String rawKey = key.key();
        assertNotNull(key);
        assertNotNull(encryptedKey);
        assertNotNull(rawKey);
        assertNotEquals(encryptedKey, rawKey);
    }

    @Test
    void validateApiKey() {
        SecretKey secretKey = service.generateApiKey();

        boolean result = service.validateApiKey(secretKey.key(), secretKey.keyHash());

        assertTrue(result);
    }

    @Test
    void validateApiKey_invalidKey() {
        SecretKey secretKey = service.generateApiKey();

        boolean result = service.validateApiKey("randomKey", secretKey.keyHash());

        assertFalse(result);
    }

    @Test
    void generateApiKeyPublicPrivateKeysPair() {
        Pair<String, String> keysPair = service.generatePublicPrivateKeysPair();

        assertNotNull(keysPair);
        assertTrue(StringUtils.hasText(keysPair.getFirst()));
        assertTrue(StringUtils.hasText(keysPair.getSecond()));
    }

    @Test
    void decodeBase64ToRSAPublicKey() {
        Pair<String, String> keysPair = service.generatePublicPrivateKeysPair();

        RSAPublicKey rsaPublicKey = KeyServiceImpl.decodeBase64ToRSAPublicKey(keysPair.getFirst());
        assertNotNull(rsaPublicKey);
    }

    @Test
    void decodeBase64ToRSAPrivateKey() {
        Pair<String, String> keysPair = service.generatePublicPrivateKeysPair();

        RSAPrivateKey rsaPrivateKey = KeyServiceImpl.decodeBase64ToRSAPrivateKey(keysPair.getSecond());
        assertNotNull(rsaPrivateKey);
    }

}