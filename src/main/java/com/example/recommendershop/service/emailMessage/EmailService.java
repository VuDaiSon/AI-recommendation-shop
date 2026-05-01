package com.example.recommendershop.service.emailMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${brevo.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendResetPassword(String to, String link) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of(
                    "name", "Recommender Shop",
                    "email", "a9e948001@smtp-brevo.com"
            ));

            body.put("to", List.of(
                    Map.of("email", to)
            ));

            body.put("subject", "Reset mật khẩu");

            body.put("htmlContent",
                    "<p>Click vào link để reset mật khẩu:</p>" +
                            "<a href='" + link + "'>" + link + "</a>"
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            log.info("✅ BREVO RESPONSE: {}", response.getBody());

        } catch (Exception e) {
            log.error("❌ SEND EMAIL FAILED", e);
        }
    }
}