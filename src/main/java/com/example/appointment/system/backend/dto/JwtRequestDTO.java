package com.example.appointment.system.backend.dto;

import lombok.Data;

@Data
public class JwtRequestDTO {
    private String username;
    private String password;

    public JwtRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
