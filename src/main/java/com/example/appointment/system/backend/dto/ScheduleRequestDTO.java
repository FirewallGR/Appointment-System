package com.example.appointment.system.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ScheduleRequestDTO {
    private LocalDate date;
    private UUID doctorId;
    private List<SlotDTO> slots;

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class SlotDTO {
        private String time;
        private boolean isBooked;
        private String patientId;
    }
}
