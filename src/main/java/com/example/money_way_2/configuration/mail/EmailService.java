package com.example.money_way_2.configuration.mail;

public interface EmailService {

    void sendEmail(String to, String subject, String message);
}
