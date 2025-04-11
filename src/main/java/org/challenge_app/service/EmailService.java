package org.challenge_app.service;

public interface EmailService {
    public void sendCustomerCreatedEmail(String to, String subject, String body);
}
