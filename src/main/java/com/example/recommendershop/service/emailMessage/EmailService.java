package com.example.recommendershop.service.emailMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    public void sendResetPassword(String to, String link) {
        try {
            URL url = new URL("https://api.resend.com/emails");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body = """
                {
                  "from": "onboarding@resend.dev",
                  "to": ["%s"],
                  "subject": "Reset mật khẩu",
                  "html": "<p>Click vào link để reset mật khẩu:</p><a href='%s'>Reset Password</a>"
                }
                """.formatted(to, link);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int responseCode = conn.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                log.info("✅ Email sent to {}", to);
            } else {
                log.error("❌ Resend failed with code: {}", responseCode);
            }

        } catch (Exception e) {
            log.error("❌ Send email error", e);
        }
    }
}