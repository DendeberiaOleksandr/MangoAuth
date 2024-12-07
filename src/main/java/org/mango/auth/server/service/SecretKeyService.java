package org.mango.auth.server.service;

import org.mango.auth.server.dto.key.SecretKey;

public interface SecretKeyService {

    SecretKey generate();

    boolean matches(String secretKey, String encodedSecretKey);

}
