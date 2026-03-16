package com.projects.sentinel.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.sentinel.Dto.UserEvent;
import com.projects.sentinel.Service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public EventListener(ObjectMapper objectMapper, EmailService emailService){
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }
    @KafkaListener(topics = "user-events", groupId = "sentinel-workers")
    public void consumeUserEvent(String message){

        try{
            UserEvent event = objectMapper.readValue(message, UserEvent.class);
            emailService.processWelcomeEmail(event);
        }catch(Exception e){
            log.info("error occurred during email scheduling process");
        }
    }

}
