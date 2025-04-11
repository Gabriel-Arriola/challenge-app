package org.challenge_app.service;

import org.challenge_app.service.impl.emailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private emailServiceImpl emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendCustomerCreatedEmail_shouldSendMessageWithCorrectData() {
        String to = "destinatario@example.com";
        String subject = "Bienvenido";
        String body = "Gracias por registrarte";

        emailService.sendCustomerCreatedEmail(to, subject, body);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(to, message.getTo()[0]);
        assertEquals(subject, message.getSubject());
        assertEquals(body, message.getText());
    }
}
