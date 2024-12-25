package com.example.appointment.system.backend;

import com.example.appointment.system.backend.dto.ScheduleRequestDTO;
import com.example.appointment.system.backend.dto.ScheduleResponseDTO;
import com.example.appointment.system.backend.model.Schedule;
import com.example.appointment.system.backend.repository.ScheduleRepository;
import com.example.appointment.system.backend.service.ScheduleService;
import com.example.appointment.system.backend.utils.mapper.ScheduleMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ScheduleServiceTests {
    private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
    private final ScheduleService scheduleService = new ScheduleService(scheduleRepository);

    @Test
    void testGetAllSchedules() {
        // Arrange
        Schedule schedule1 = new Schedule();
        Schedule schedule2 = new Schedule();
        schedule1.setSlots(List.of(new Schedule.Slot("2024-12-12", false, "")));
        schedule2.setSlots(List.of(new Schedule.Slot("2024-12-13", false, "")));
        List<Schedule> schedules = Arrays.asList(schedule1, schedule2);
        List<ScheduleResponseDTO> dtos = schedules.stream().map(ScheduleMapper::toDTO).collect(Collectors.toList());
        when(scheduleRepository.findAll()).thenReturn(schedules);

        // Act
        List<ScheduleResponseDTO> result = scheduleService.getAllSchedules();

        // Assert
        assertEquals(dtos, result);
        verify(scheduleRepository).findAll();
    }

    @Test
    void testGetScheduleById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        Schedule schedule = new Schedule();
        schedule.setSlots(List.of(new Schedule.Slot("2024-12-11", false, "")));
        ScheduleResponseDTO dto = ScheduleMapper.toDTO(schedule);
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));

        // Act
        ScheduleResponseDTO result = scheduleService.getScheduleById(id);

        // Assert
        assertEquals(dto, result);
        verify(scheduleRepository).findById(id);
    }

    @Test
    void testGetScheduleById_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> scheduleService.getScheduleById(id));
        assertEquals("Schedule not found", exception.getMessage());
        verify(scheduleRepository).findById(id);
    }

    @Test
    void testCreateSchedule() {
        // Arrange
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setSlots(List.of(new ScheduleRequestDTO.SlotDTO("2024-12-9", false, "")));
        Schedule schedule = ScheduleMapper.toEntity(dto);
        Schedule savedSchedule = new Schedule();
        savedSchedule.setSlots(List.of(new Schedule.Slot("2024-12-10", false, "")));
        ScheduleResponseDTO responseDto = ScheduleMapper.toDTO(savedSchedule);

        when(scheduleRepository.save(schedule)).thenReturn(savedSchedule);

        // Act
        ScheduleResponseDTO result = scheduleService.createSchedule(dto);

        // Assert
        assertEquals(responseDto, result);
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void testUpdateSchedule_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setDate(LocalDate.parse("2024-12-31"));
        dto.setDoctorId(UUID.randomUUID());
        dto.setSlots(Arrays.asList(new ScheduleRequestDTO.SlotDTO("10:00", false, null)));

        Schedule existingSchedule = new Schedule();
        when(scheduleRepository.findById(id)).thenReturn(Optional.of(existingSchedule));

        Schedule updatedSchedule = new Schedule();
        updatedSchedule.setDate(dto.getDate());
        updatedSchedule.setDoctorId(dto.getDoctorId());
        updatedSchedule.setSlots(dto.getSlots().stream().map(slotDTO -> {
            Schedule.Slot slot = new Schedule.Slot();
            slot.setTime(slotDTO.getTime());
            slot.setBooked(slotDTO.isBooked());
            slot.setPatientId(slotDTO.getPatientId());
            return slot;
        }).collect(Collectors.toList()));

        when(scheduleRepository.save(existingSchedule)).thenReturn(updatedSchedule);

        ScheduleResponseDTO responseDto = ScheduleMapper.toDTO(updatedSchedule);

        // Act
        ScheduleResponseDTO result = scheduleService.updateSchedule(id, dto);

        // Assert
        assertEquals(responseDto, result);
        verify(scheduleRepository).findById(id);
        verify(scheduleRepository).save(existingSchedule);
    }

    @Test
    void testUpdateSchedule_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        when(scheduleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> scheduleService.updateSchedule(id, dto));
        assertEquals("Schedule not found", exception.getMessage());
        verify(scheduleRepository).findById(id);
    }

    @Test
    void testDeleteSchedule_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(scheduleRepository.existsById(id)).thenReturn(true);

        // Act
        scheduleService.deleteSchedule(id);

        // Assert
        verify(scheduleRepository).existsById(id);
        verify(scheduleRepository).deleteById(id);
    }

    @Test
    void testDeleteSchedule_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(scheduleRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> scheduleService.deleteSchedule(id));
        assertEquals("Schedule not found", exception.getMessage());
        verify(scheduleRepository).existsById(id);
    }
}
