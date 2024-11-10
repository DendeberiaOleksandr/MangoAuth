package org.mango.auth.server.service.impl;

import org.mango.auth.server.service.CodeGenerationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CodeGenerationServiceImpl implements CodeGenerationService {

    private static final Random random = new Random();

    private int codeLength;

    @Override
    public String generate() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }

    @Value("${app.user.verification.codeLength:6}")
    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }
}
