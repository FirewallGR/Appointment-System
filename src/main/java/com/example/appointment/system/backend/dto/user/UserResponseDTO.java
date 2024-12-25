package com.example.appointment.system.backend.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String username;
    private String email;
    private String name;
    private String secondName;
    private String thirdName;

    public UserResponseDTO(UUID id, String username, String name, String secondName, String thirdName, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.secondName = secondName;
        this.thirdName = thirdName;
    }
}
