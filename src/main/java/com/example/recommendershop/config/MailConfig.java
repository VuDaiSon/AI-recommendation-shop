package com.example.recommendershop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class MailConfig {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${app.mail.sender.name}")
    private String senderName;

    @Value("${app.mail.sender.email}")
    private String senderEmail;
}