package com.example.appointment.system.backend.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
}

