package com.projects.sentinel.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserEvent {
    private String username;
    private String email;
    private String eventType;
    private String deviceType;
}
