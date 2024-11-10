package org.mango.auth.server.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mango.auth.server.dto.EmailProperties;
import org.mango.auth.server.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private String from;
    private final JavaMailSender mailSender;

    @Override
    public boolean sendEmail(EmailProperties emailProperties) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setSubject(emailProperties.getSubject());
            helper.setText(emailProperties.getHtmlContent(), true);
            helper.setTo(emailProperties.getTo());
            helper.setFrom(from);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
            return false;
        }

        return true;
    }

    @Value("${spring.mail.username}")
    public void setFrom(String from) {
        this.from = from;
    }
}
