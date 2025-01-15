package com.example.sunnymarketbackend.security;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleHtml(Collection<String> receivers, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom("Sunny Market<q12567893@gmail.com>");
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(receivers.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
