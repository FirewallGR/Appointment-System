package com.example.appointment.system.backend.dto.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponseDTO {
    private UUID id;
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
