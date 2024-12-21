package com.example.appointment.system.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationUserDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String name;
    private String secondName;
    private String thirdName;
    private String role;
}
