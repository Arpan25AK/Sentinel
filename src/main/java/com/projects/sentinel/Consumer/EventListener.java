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
    @KafkaListener(topics = "user-events", groupId = "sentinel-workers", concurrency = "3")
    public void consumeUserEvent(String message){

        try{
            UserEvent event = objectMapper.readValue(message, UserEvent.class);

            switch (event.getEventType().toUpperCase()){
                case "SIGNUP":
                    emailService.processWelcomeEmail(event);
                    break;
                case "LOGIN":
                    emailService.processLoginAlert(event);
                    break;
                default :
                    log.info("thread not processed due to unknown even type",Thread.currentThread().getName(), event.getEventType());
            }

        }catch(Exception e){
            log.info("error occurred during email scheduling process");
            throw new RuntimeException("failed to process the event", e);
        }
    }

}
