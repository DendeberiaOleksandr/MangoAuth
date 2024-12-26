package org.mango.auth.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.dto.key.SecretKey;
import org.mango.auth.server.service.KeyService;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class KeyServiceImpl implements KeyService {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    private final PasswordEncoder passwordEncoder;
    private final KeyPairGenerator rsaKeyPairGenerator;

    public KeyServiceImpl(PasswordEncoder passwordEncoder) throws NoSuchAlgorithmException {
        this.passwordEncoder = passwordEncoder;
        this.rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
        rsaKeyPairGenerator.initialize(2048, secureRandom);
    }

    @Override
    public Pair<String, String> generatePublicPrivateKeysPair() {
        KeyPair keyPair = rsaKeyPairGenerator.generateKeyPair();

        String publicKeyBase64 = convertKeyToBase64(keyPair.getPublic());
        String privateKeyBase64 = convertKeyToBase64(keyPair.getPrivate());

        return Pair.of(publicKeyBase64, privateKeyBase64);
    }

    public static String convertKeyToBase64(Key key) {
        byte[] encoded = key.getEncoded();
        return Base64.getEncoder().encodeToString(encoded);
    }

    public static RSAPublicKey decodeBase64ToRSAPublicKey(String base64RSAPublicKey) {
        byte[] keyBytes = Base64.getDecoder().decode(base64RSAPublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static RSAPrivateKey decodeBase64ToRSAPrivateKey(String base64RSAPrivateKey) {
        byte[] keyBytes = Base64.getDecoder().decode(base64RSAPrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public SecretKey generateApiKey() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String secretKey = base64Encoder.encodeToString(bytes);
        return new SecretKey(secretKey, passwordEncoder.encode(secretKey));
    }

    @Override
    public boolean validateApiKey(String apiKey, String apiKeyHash) {
        return passwordEncoder.matches(apiKey, apiKeyHash);
    }
}
