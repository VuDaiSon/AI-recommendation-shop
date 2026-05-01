package com.example.recommendershop.service.emailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPassword(String to, String link) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("tatbara2@gmail.com");
            message.setTo(to);
            message.setSubject("Reset mật khẩu");
            message.setText("Click link để reset mật khẩu:\n" + link);

            mailSender.send(message);

            log.info("✅ Sent to {}", to);

        } catch (Exception e) {
            log.error("❌ Send fail", e);
        }
    }
}