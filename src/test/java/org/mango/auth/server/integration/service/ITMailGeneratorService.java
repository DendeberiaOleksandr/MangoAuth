package org.mango.auth.server.integration.service;

import org.junit.jupiter.api.Test;
import org.mango.auth.server.service.MailGeneratorService;
import org.mango.auth.server.service.impl.MailGeneratorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {MailGeneratorServiceImpl.class, ResourceLoader.class})
public class ITMailGeneratorService {

    @Autowired
    MailGeneratorService mailGeneratorService;

    @Test
    void generateActivationEmail() {
        String email = mailGeneratorService.generateActivationEmail("email@example.com", "123456", "client");

        assertNotNull(email);
        assertTrue(email.contains("html"));
    }

}
