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

    public void processWelcomeEmail(UserEvent event) throws Exception{

        log.info("starting signup process for "+ event.getUsername());


        MimeMessage messege = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(messege,true,"UTF-8");

        org.thymeleaf.context.Context context = new Context();
        context.setVariable("username", event.getUsername());

        String htmlContent = templateEngine.process("signup-email", context);

        helper.setTo(event.getEmail());
        helper.setSubject("Welcome to the Platform! 🚀");
        helper.setText(htmlContent, true);

        mailSender.send(messege);
    }

    public void processLoginAlert(UserEvent event ) throws Exception{
        log.info("started the security check for", event.getUsername());
        Thread.sleep(2000);
        log.info("process finished for ", event.getUsername());
    }

}
