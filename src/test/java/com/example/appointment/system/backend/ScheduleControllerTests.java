package com.example.appointment.system.backend;

import com.example.appointment.system.backend.controller.ScheduleController;
import com.example.appointment.system.backend.dto.schedule.ScheduleRequestDTO;
import com.example.appointment.system.backend.dto.schedule.ScheduleResponseDTO;
import com.example.appointment.system.backend.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleControllerTests {

    @Mock
    private ScheduleService scheduleService; // Мокирование сервиса

    @InjectMocks
    private ScheduleController scheduleController; // Внедрение мока в контроллер

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков перед каждым тестом
    }

    @Test
    void testGetAllSchedules() {
        // Создаем фиктивные данные
        ScheduleResponseDTO schedule1 = new ScheduleResponseDTO();
        schedule1.setId(UUID.randomUUID());
        schedule1.setDoctorId(UUID.randomUUID());
        schedule1.setDate(LocalDate.parse("2024-12-27"));
        ScheduleResponseDTO schedule2 = new ScheduleResponseDTO();
        schedule2.setId(UUID.randomUUID());
        schedule2.setDoctorId(UUID.randomUUID());
        schedule2.setDate(LocalDate.parse("2024-12-28"));

        List<ScheduleResponseDTO> schedules = List.of(schedule1, schedule2);

        // Мокаем поведение сервиса
        when(scheduleService.getAllSchedules()).thenReturn(schedules);

        // Вызов контроллера
        ResponseEntity<List<ScheduleResponseDTO>> response = scheduleController.getAllSchedules();

        // Проверка результатов
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(schedules, response.getBody());

        // Проверка, что метод сервиса был вызван один раз
        verify(scheduleService, times(1)).getAllSchedules();
    }

    @Test
    void testGetScheduleById() {
        // Создаем фиктивные данные
        UUID scheduleId = UUID.randomUUID();
        ScheduleResponseDTO schedule = new ScheduleResponseDTO();
        schedule.setId(scheduleId);
        schedule.setDoctorId(UUID.randomUUID());
        schedule.setDate(LocalDate.parse("2024-12-27"));

        // Мокаем поведение сервиса
        when(scheduleService.getScheduleById(scheduleId)).thenReturn(schedule);

        // Вызов контроллера
        ResponseEntity<ScheduleResponseDTO> response = scheduleController.getScheduleById(scheduleId);

        // Проверка результатов
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(schedule, response.getBody());

        // Проверка, что метод сервиса был вызван один раз
        verify(scheduleService, times(1)).getScheduleById(scheduleId);
    }

    @Test
    void testGetSchedulesByDoctorId() {
        // Создаем фиктивные данные
        UUID doctorId = UUID.randomUUID();
        ScheduleResponseDTO schedule1 = new ScheduleResponseDTO();
        schedule1.setId(UUID.randomUUID());
        schedule1.setDoctorId(doctorId);
        schedule1.setDate(LocalDate.parse("2024-12-27"));

        ScheduleResponseDTO schedule2 = new ScheduleResponseDTO();
        schedule2.setId(UUID.randomUUID());
        schedule2.setDoctorId(doctorId);
        schedule2.setDate(LocalDate.parse("2024-12-28"));

        List<ScheduleResponseDTO> schedules = List.of(schedule1, schedule2);

        // Мокаем поведение сервиса
        when(scheduleService.getSchedulesByDoctorId(doctorId)).thenReturn(schedules);

        // Вызов контроллера
        ResponseEntity<List<ScheduleResponseDTO>> response = scheduleController.getScheduleByDoctorId(doctorId);

        // Проверка результатов
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(schedules, response.getBody());

        // Проверка, что метод сервиса был вызван один раз
        verify(scheduleService, times(1)).getSchedulesByDoctorId(doctorId);
    }

    @Test
    void testCreateSchedule() {
        // Создаем фиктивные данные
        UUID doctorId = UUID.randomUUID();
        ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
        requestDTO.setDoctorId(doctorId);
        requestDTO.setDate(LocalDate.parse("2024-12-27"));
        ScheduleResponseDTO responseDTO = new ScheduleResponseDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setDoctorId(doctorId);
        responseDTO.setDate(LocalDate.parse("2024-12-27"));

        // Мокаем поведение сервиса
        when(scheduleService.createSchedule(requestDTO)).thenReturn(responseDTO);

        // Вызов контроллера
        ResponseEntity<ScheduleResponseDTO> response = scheduleController.createSchedule(requestDTO);

        // Проверка результатов
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());

        // Проверка, что метод сервиса был вызван один раз
        verify(scheduleService, times(1)).createSchedule(requestDTO);
    }

    @Test
    void testUpdateSchedule() {
        // Создаем фиктивные данные
        UUID scheduleId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
        requestDTO.setDoctorId(doctorId);
        requestDTO.setDate(LocalDate.parse("2024-12-27"));
        ScheduleResponseDTO responseDTO = new ScheduleResponseDTO();
        responseDTO.setId(scheduleId);
        responseDTO.setDoctorId(doctorId);
        responseDTO.setDate(LocalDate.parse("2024-12-27"));

        // Мокаем поведение сервиса
        when(scheduleService.updateSchedule(scheduleId, requestDTO)).thenReturn(responseDTO);

        // Вызов контроллера
        ResponseEntity<ScheduleResponseDTO> response = scheduleController.updateSchedule(scheduleId, requestDTO);

        // Проверка результатов
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());

        // Проверка, что метод сервиса был вызван один раз
        verify(scheduleService, times(1)).updateSchedule(scheduleId, requestDTO);
    }

    @Test
    void testDeleteSchedule() {
        UUID scheduleId = UUID.randomUUID();

        // Вызов контроллера
        ResponseEntity<Void> response = scheduleController.deleteSchedule(scheduleId);

        // Проверка результатов
        assertEquals(204, response.getStatusCodeValue());

        // Проверка, что метод сервиса был вызван один раз
        verify(scheduleService, times(1)).deleteSchedule(scheduleId);
    }
}
