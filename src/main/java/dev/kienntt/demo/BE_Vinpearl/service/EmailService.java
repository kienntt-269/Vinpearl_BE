package dev.kienntt.demo.BE_Vinpearl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        emailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String body) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(subject);
        helper.setTo(to);

        String content = "<b>Dear guru</b>,<br><i>Please look at this nice picture:.</i>"
                + "<br><img src='cid:image001'/><br><b>Best Regards</b>";
        helper.setText(content, true);

        FileSystemResource resource = new FileSystemResource(new File("picture.png"));
        helper.addInline("image001", resource);

        emailSender.send(message);
//        MimeMessage message = emailSender.createMimeMessage();
//
//        message.setFrom(new InternetAddress(to));
////        message.setRecipients(MimeMessage.RecipientType.TO, "recipient@example.com");
//        message.setSubject(subject);
//
//        String htmlContent = String.format("<h1>This is a test Spring Boot email</h1>" +
//                "<p>It can contain <strong>%s</strong> content.</p>", body);
//        message.setContent(htmlContent, "text/html; charset=utf-8");
//
//        emailSender.send(message);
    }
}
