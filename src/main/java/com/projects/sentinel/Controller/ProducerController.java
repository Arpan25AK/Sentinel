package com.projects.sentinel.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.sentinel.Dto.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/kafka/test")
public class ProducerController {

    private final KafkaTemplate<String , String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ProducerController(KafkaTemplate<String , String> kafkaTemplate, ObjectMapper objectMapper){
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/send")
    public String sendTestEvent(@RequestBody UserEvent event){
        try{
        String message = objectMapper.writeValueAsString(event);

        kafkaTemplate.send("user-events",message);

        return "successfully fired the event to kafka";
    }catch(Exception e){
        return "error occured during event firing to kafka";
        }
    }

    @PostMapping("/burst")
    public String burstEvent(@RequestBody UserEvent event){
        try{
            for(int i =0; i<= 15; i++){
                String messege = objectMapper.writeValueAsString(event);
                kafkaTemplate.send("user-events", String.valueOf(i),messege);
            }
            return "evnet successfully bursted 15 times";
        }catch(Exception e){
            return "error occured during event bursting";
        }
    }
}
