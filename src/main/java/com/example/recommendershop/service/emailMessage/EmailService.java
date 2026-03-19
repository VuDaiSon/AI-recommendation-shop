//package com.example.recommendershop.service.emailMessage;
//
//import com.example.recommendershop.dto.EmailMessage;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//    private JavaMailSender javaMailSender;
//
//    public EmailService(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//    public void sendEmail(EmailMessage emailMessage){
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(emailMessage.getTo());
//        mailMessage.setSubject(emailMessage.getSubject());
//        mailMessage.setText(emailMessage.getBody());
//        javaMailSender.send(mailMessage);
//    }
//}
