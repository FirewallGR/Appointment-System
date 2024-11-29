package com.example.appointment.system.backend.model;

import com.example.appointment.system.backend.utils.SlotListConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "slots", columnDefinition = "json", nullable = false)
    private List<Slot> slots;

    @Data
    public static class Slot {
        private String time;
        private boolean isBooked;
        private String patientId;
    }
}
