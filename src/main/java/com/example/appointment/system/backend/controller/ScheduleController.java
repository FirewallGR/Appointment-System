package com.example.appointment.system.backend.controller;

import com.example.appointment.system.backend.dto.schedule.ScheduleRequestDTO;
import com.example.appointment.system.backend.dto.schedule.ScheduleResponseDTO;
import com.example.appointment.system.backend.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getScheduleById(@PathVariable UUID id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<ScheduleResponseDTO>> getScheduleByDoctorId(@PathVariable UUID id) {
        List<ScheduleResponseDTO> schedules = scheduleService.getSchedulesByDoctorId(id);
        System.out.println(id);
        System.out.println(schedules);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDTO> createSchedule(
            @RequestBody ScheduleRequestDTO scheduleRequestDTO) {
        return ResponseEntity.ok(scheduleService.createSchedule(scheduleRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(
            @PathVariable UUID id,
            @RequestBody ScheduleRequestDTO scheduleRequestDTO) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, scheduleRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}

