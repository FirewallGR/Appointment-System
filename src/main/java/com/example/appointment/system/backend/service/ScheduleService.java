package com.example.appointment.system.backend.service;

import com.example.appointment.system.backend.dto.schedule.ScheduleRequestDTO;
import com.example.appointment.system.backend.dto.schedule.ScheduleResponseDTO;
import com.example.appointment.system.backend.model.Schedule;
import com.example.appointment.system.backend.repository.ScheduleRepository;
import com.example.appointment.system.backend.utils.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<ScheduleResponseDTO> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(ScheduleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ScheduleResponseDTO getScheduleById(UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        return ScheduleMapper.toDTO(schedule);
    }
    public List<ScheduleResponseDTO> getSchedulesByDoctorId(UUID doctorId) {
        List<Schedule> schedules = scheduleRepository.findByDoctorId(doctorId);

        if (schedules.isEmpty()) {
            throw new RuntimeException("Schedules not found for doctor with ID: " + doctorId);
        }

        return schedules.stream()
                .map(ScheduleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO dto) {
        Schedule schedule = ScheduleMapper.toEntity(dto);
        schedule = scheduleRepository.save(schedule);
        return ScheduleMapper.toDTO(schedule);
    }

    public ScheduleResponseDTO updateSchedule(UUID id, ScheduleRequestDTO dto) {
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        existingSchedule.setDate(dto.getDate());
        existingSchedule.setDoctorId(dto.getDoctorId());
        existingSchedule.setSlots(dto.getSlots().stream()
                .map(slotDTO -> {
                    Schedule.Slot slot = new Schedule.Slot();
                    slot.setTime(slotDTO.getTime());
                    slot.setBooked(slotDTO.isBooked());
                    slot.setPatientId(slotDTO.getPatientId());
                    return slot;
                })
                .collect(Collectors.toList()));

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule);
        return ScheduleMapper.toDTO(updatedSchedule);
    }

    public void deleteSchedule(UUID id) {
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Schedule not found");
        }
        scheduleRepository.deleteById(id);
    }
}
