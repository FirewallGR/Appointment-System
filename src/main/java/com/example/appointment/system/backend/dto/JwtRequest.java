package com.example.appointment.system.backend.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
