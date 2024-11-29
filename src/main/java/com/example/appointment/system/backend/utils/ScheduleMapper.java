package com.example.appointment.system.backend.utils;

import com.example.appointment.system.backend.dto.ScheduleRequestDTO;
import com.example.appointment.system.backend.dto.ScheduleResponseDTO;
import com.example.appointment.system.backend.model.Schedule;

import java.util.stream.Collectors;

public class ScheduleMapper {

    public static Schedule toEntity(ScheduleRequestDTO dto) {
        Schedule schedule = new Schedule();
        schedule.setDate(dto.getDate());
        schedule.setDoctorId(dto.getDoctorId());
        schedule.setSlots(dto.getSlots().stream()
                .map(slotDTO -> {
                    Schedule.Slot slot = new Schedule.Slot();
                    slot.setTime(slotDTO.getTime());
                    slot.setBooked(slotDTO.isBooked());
                    slot.setPatientId(slotDTO.getPatientId());
                    return slot;
                })
                .collect(Collectors.toList()));
        return schedule;
    }

    public static ScheduleResponseDTO toDTO(Schedule schedule) {
        ScheduleResponseDTO dto = new ScheduleResponseDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setDoctorId(schedule.getDoctorId());
        dto.setSlots(schedule.getSlots().stream()
                .map(slot -> {
                    ScheduleResponseDTO.SlotDTO slotDTO = new ScheduleResponseDTO.SlotDTO();
                    slotDTO.setTime(slot.getTime());
                    slotDTO.setBooked(slot.isBooked());
                    slotDTO.setPatientId(slot.getPatientId());
                    return slotDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }
}
