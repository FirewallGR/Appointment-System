package com.example.appointment.system.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DoctorDTO {
    private UUID id;
    private String name;
    private String secondName;
    private String thirdName;
    private String username;
}
