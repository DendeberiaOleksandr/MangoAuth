package org.mango.auth.server.service;

import org.mango.auth.server.dto.key.SecretKey;
import org.springframework.data.util.Pair;

public interface KeyService {

    Pair<String, String> generatePublicPrivateKeysPair();

    SecretKey generateApiKey();

    boolean validateApiKey(String apiKey, String apiKeyHash);

}
