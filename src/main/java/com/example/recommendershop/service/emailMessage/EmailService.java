package com.example.recommendershop.service.emailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPassword(String to, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom("Recommender Shop <a9e948001@smtp-brevo.com>");
            helper.setTo(to);
            helper.setSubject("Reset mật khẩu");

            String html = """
            <div>
                <p>Click link để reset mật khẩu:</p>
                <a href="%s">%s</a>
            </div>
            """.formatted(link, link);

            helper.setText(html, true);

            mailSender.send(message);

            log.info("✅ EMAIL SENT TO: {}", to);

        } catch (Exception e) {
            log.error("❌ EMAIL FAILED", e);
        }
    }
}