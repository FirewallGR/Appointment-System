package com.example.appointment.system.backend;

import com.example.appointment.system.backend.controller.MedicalRecordController;
import com.example.appointment.system.backend.dto.MedicalRecordRequestDTO;
import com.example.appointment.system.backend.dto.MedicalRecordResponseDTO;
import com.example.appointment.system.backend.service.MedicalRecordService;
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

public class MedicalRecordsControllerTests {
    private final MedicalRecordService service = Mockito.mock(MedicalRecordService.class);
    private final MedicalRecordController controller = new MedicalRecordController(service);

    @Test
    void testGetAllRecords() {
        // Arrange
        List<MedicalRecordResponseDTO> mockRecords = Arrays.asList(new MedicalRecordResponseDTO(), new MedicalRecordResponseDTO());
        when(service.getAllRecords()).thenReturn(mockRecords);

        // Act
        ResponseEntity<List<MedicalRecordResponseDTO>> response = controller.getAllRecords();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRecords, response.getBody());
        verify(service).getAllRecords();
    }

    @Test
    void testGetRecordById() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        MedicalRecordResponseDTO mockRecord = new MedicalRecordResponseDTO();
        when(service.getRecordById(mockId)).thenReturn(mockRecord);

        // Act
        ResponseEntity<MedicalRecordResponseDTO> response = controller.getRecordById(mockId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRecord, response.getBody());
        verify(service).getRecordById(mockId);
    }

    @Test
    void testCreateRecord() {
        // Arrange
        MedicalRecordRequestDTO mockRequest = new MedicalRecordRequestDTO();
        MedicalRecordResponseDTO mockResponse = new MedicalRecordResponseDTO();
        when(service.createRecord(any(MedicalRecordRequestDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<MedicalRecordResponseDTO> response = controller.createRecord(mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(service).createRecord(mockRequest);
    }

    @Test
    void testUpdateRecord() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        MedicalRecordRequestDTO mockRequest = new MedicalRecordRequestDTO();
        MedicalRecordResponseDTO mockResponse = new MedicalRecordResponseDTO();
        when(service.updateRecord(any(UUID.class), any(MedicalRecordRequestDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<MedicalRecordResponseDTO> response = controller.updateRecord(mockId, mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(service).updateRecord(mockId, mockRequest);
    }

    @Test
    void testDeleteRecord() {
        // Arrange
        UUID mockId = UUID.randomUUID();

        // Act
        ResponseEntity<Void> response = controller.deleteRecord(mockId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).deleteRecord(mockId);
    }
}
