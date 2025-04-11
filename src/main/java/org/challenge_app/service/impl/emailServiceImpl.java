package org.challenge_app.service.impl;

import lombok.RequiredArgsConstructor;
import org.challenge_app.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class emailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendCustomerCreatedEmail(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailSender.send(mailMessage);
    }

}
