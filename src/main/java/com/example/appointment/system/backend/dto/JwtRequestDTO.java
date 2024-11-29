package com.example.appointment.system.backend.dto;

import lombok.Data;

@Data
public class JwtRequestDTO {
    private String username;
    private String password;
}
