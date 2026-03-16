package com.projects.sentinel.Service;

import com.projects.sentinel.Dto.UserEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public EmailService(TemplateEngine templateEngine, JavaMailSender mailSender){
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public void processWelcomeEmail(UserEvent event){

        log.info("starting heavy task for "+ event.getUsername());

        try{
            MimeMessage messege = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(messege,true,"UTF-8");

            org.thymeleaf.context.Context context = new Context();
            context.setVariable("username", event.getUsername());

            String htmlContent = templateEngine.process("welcome-email", context);

            helper.setTo(event.getEmail());
            helper.setSubject("Welcome to the Platform! 🚀");
            helper.setText(htmlContent, true); // 'true' tells Gmail this is HTML, not plain text

            mailSender.send(messege);

            log.info("✅ SUCCESS! Real HTML Email sent to: {}", event.getEmail());
        }catch(Exception e){
            log.info("error occured with email sending service",event.getEmail(), e);
        }

        log.info("process completed successfully");
    }
}
