package com.example.appointment.system.backend.repository;

import com.example.appointment.system.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByDoctorId(UUID doctorId);
}
