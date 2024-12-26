package org.mango.auth.server.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.service.MailGeneratorService;
import org.mango.auth.server.util.StringUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailGeneratorServiceImpl implements MailGeneratorService {
    private static final String EMAIL_PATTERN = "EMAIL";
    private static final String CODE_PATTERN = "CODE";
    private static final String CLIENT_NAME_PATTERN = "CLIENT_NAME";
    private static final String ACTIVATION_EMAIL_PATH = "mail/activation-email.html";

    private final ResourceLoader resourceLoader;

    @Override
    public String generateActivationEmail(String email, String code, String clientName) {
        String html = readHtml(ACTIVATION_EMAIL_PATH);

        return StringUtil.replaceByPattern(html, Map.of(
                EMAIL_PATTERN, email,
                CODE_PATTERN, code,
                CLIENT_NAME_PATTERN, clientName
        ));
    }

    private String readHtml(String file) {
        Resource resource = resourceLoader.getResource("classpath:" + file);
        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read file: %s".formatted(file), e);
            throw new RuntimeException(e);
        }
    }

}
