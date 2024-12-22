package com.example.appointment.system.backend.dto.schedule;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ScheduleRequestDTO {
    private LocalDate date;
    private UUID doctorId;
    private List<SlotDTO> slots;

    @Data
    public static class SlotDTO {
        private String time;
        private boolean isBooked;
        private String patientId;
    }
}
