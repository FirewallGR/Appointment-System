package com.example.appointment.system.backend.dto.user;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String email;
    private String password;
    private String name;
    private String secondName;
    private String thirdName;
}
