package com.example.appointment.system.backend;

import com.example.appointment.system.backend.controller.ScheduleController;
import com.example.appointment.system.backend.dto.ScheduleRequestDTO;
import com.example.appointment.system.backend.dto.ScheduleResponseDTO;
import com.example.appointment.system.backend.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScheduleControllerTests {

    private final ScheduleService scheduleService = Mockito.mock(ScheduleService.class);
    private final ScheduleController scheduleController = new ScheduleController(scheduleService);

    @Test
    void testGetAllSchedules() {
        // Arrange
        List<ScheduleResponseDTO> mockSchedules = Arrays.asList(new ScheduleResponseDTO(), new ScheduleResponseDTO());
        when(scheduleService.getAllSchedules()).thenReturn(mockSchedules);

        // Act
        ResponseEntity<List<ScheduleResponseDTO>> response = scheduleController.getAllSchedules();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSchedules, response.getBody());
        verify(scheduleService).getAllSchedules();
    }

    @Test
    void testGetScheduleById() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        ScheduleResponseDTO mockSchedule = new ScheduleResponseDTO();
        when(scheduleService.getScheduleById(mockId)).thenReturn(mockSchedule);

        // Act
        ResponseEntity<ScheduleResponseDTO> response = scheduleController.getScheduleById(mockId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSchedule, response.getBody());
        verify(scheduleService).getScheduleById(mockId);
    }

    @Test
    void testCreateSchedule() {
        // Arrange
        ScheduleRequestDTO mockRequest = new ScheduleRequestDTO();
        ScheduleResponseDTO mockResponse = new ScheduleResponseDTO();
        when(scheduleService.createSchedule(any(ScheduleRequestDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<ScheduleResponseDTO> response = scheduleController.createSchedule(mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(scheduleService).createSchedule(mockRequest);
    }

    @Test
    void testUpdateSchedule() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        ScheduleRequestDTO mockRequest = new ScheduleRequestDTO();
        ScheduleResponseDTO mockResponse = new ScheduleResponseDTO();
        when(scheduleService.updateSchedule(any(UUID.class), any(ScheduleRequestDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<ScheduleResponseDTO> response = scheduleController.updateSchedule(mockId, mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(scheduleService).updateSchedule(mockId, mockRequest);
    }

    @Test
    void testDeleteSchedule() {
        // Arrange
        UUID mockId = UUID.randomUUID();

        // Act
        ResponseEntity<Void> response = scheduleController.deleteSchedule(mockId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(scheduleService).deleteSchedule(mockId);
    }
}
