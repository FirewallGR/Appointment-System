package com.example.appointment.system.backend.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationUserDTO {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
    private String secondName;
    private String thirdName;
}
