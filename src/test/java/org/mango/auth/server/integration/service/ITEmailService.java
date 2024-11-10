package org.mango.auth.server.integration.service;

import org.junit.jupiter.api.Test;
import org.mango.auth.server.config.MailConfig;
import org.mango.auth.server.dto.EmailProperties;
import org.mango.auth.server.service.EmailService;
import org.mango.auth.server.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = { EmailServiceImpl.class, JavaMailSender.class, MailConfig.class})
public class ITEmailService {

    private static final String HTML = """
            <!DOCTYPE html>
            <html>
            <head>
            <title>Page Title</title>
            </head>
            <body>
            <h1>Test</h1>
            </body>
            </html>
            """;

    @Autowired
    EmailService emailService;

    @Test
    void sendEmail() {
        EmailProperties emailProperties = new EmailProperties("test", "oleksandr.dendeberia@gmail.com", HTML);

        boolean result = emailService.sendEmail(emailProperties);

        assertTrue(result);
    }

}
