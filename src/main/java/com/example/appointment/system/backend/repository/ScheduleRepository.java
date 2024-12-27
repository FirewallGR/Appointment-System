package com.example.appointment.system.backend.repository;

import com.example.appointment.system.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByDoctorId(UUID doctorId);

    @Query("SELECT s FROM Schedule s WHERE s.date = :date")
    List<Schedule> findByDate(LocalDate date);
}
