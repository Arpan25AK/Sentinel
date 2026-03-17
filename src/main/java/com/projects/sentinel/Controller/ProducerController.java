package com.projects.sentinel.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.sentinel.Dto.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/signup")
    public ResponseEntity<String> userSignUp(@RequestBody UserEvent event){
        try{
            event.setEventType("signup");
            String message = objectMapper.writeValueAsString(event);

            kafkaTemplate.send("user-events",message);

            return ResponseEntity.ok("successfully registered the user");
    }catch(Exception e){
        return ResponseEntity.badRequest().body("error occurred during signup and event firing to kafka");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody UserEvent event){
        try{
            if(event.getDeviceType()==null) event.setDeviceType("UNKNOWN_DEVICE");
            String device = event.getDeviceType().toLowerCase();
            if(device.equals("windows") || device.equals("macbook") || device.equals("android")){
                event.setEventType("LOGIN_SUCCESS");
            }else{
                event.setEventType("SUSPICIOUS_LOGIN");
            }
            String message = objectMapper.writeValueAsString(event);

            kafkaTemplate.send("user-events",event.getUsername(),message);

            return ResponseEntity.ok("user successfully logged in");
        }catch(Exception e){
            return ResponseEntity.internalServerError().body("error occurred during login and event firing to kafka");
        }
    }

}
