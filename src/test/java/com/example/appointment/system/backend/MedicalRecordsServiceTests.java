package com.example.appointment.system.backend;

import com.example.appointment.system.backend.dto.MedicalRecordRequestDTO;
import com.example.appointment.system.backend.dto.MedicalRecordResponseDTO;
import com.example.appointment.system.backend.exception.AppErorr;
import com.example.appointment.system.backend.model.MedicalRecord;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.repository.MedicalRecordRepository;
import com.example.appointment.system.backend.repository.UserRepository;
import com.example.appointment.system.backend.service.MedicalRecordService;
import com.example.appointment.system.backend.utils.MedicalRecordMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MedicalRecordsServiceTests {
    private final MedicalRecordRepository repository = mock(MedicalRecordRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final MedicalRecordMapper mapper = mock(MedicalRecordMapper.class);

    private final MedicalRecordService service = new MedicalRecordService(repository, userRepository, mapper);

    @Test
    void testGetAllRecords() {
        // Arrange
        List<MedicalRecord> records = Arrays.asList(new MedicalRecord(), new MedicalRecord());
        List<MedicalRecordResponseDTO> dtos = Arrays.asList(new MedicalRecordResponseDTO(), new MedicalRecordResponseDTO());
        when(repository.findAll()).thenReturn(records);
        when(mapper.toDto(any(MedicalRecord.class))).thenReturn(dtos.get(0), dtos.get(1));

        // Act
        List<MedicalRecordResponseDTO> result = service.getAllRecords();

        // Assert
        assertEquals(dtos, result);
        verify(repository).findAll();
        verify(mapper, times(2)).toDto(any(MedicalRecord.class));
    }

    @Test
    void testGetRecordById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        MedicalRecord record = new MedicalRecord();
        MedicalRecordResponseDTO dto = new MedicalRecordResponseDTO();
        when(repository.findById(id)).thenReturn(Optional.of(record));
        when(mapper.toDto(record)).thenReturn(dto);

        // Act
        MedicalRecordResponseDTO result = service.getRecordById(id);

        // Assert
        assertEquals(dto, result);
        verify(repository).findById(id);
        verify(mapper).toDto(record);
    }

    @Test
    void testGetRecordById_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        AppErorr error = assertThrows(AppErorr.class, () -> service.getRecordById(id));
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("Запись не найдена", error.getMessage());
        verify(repository).findById(id);
    }

    @Test
    void testCreateRecord_Success() {
        // Arrange
        MedicalRecordRequestDTO dto = new MedicalRecordRequestDTO();
        UUID patientId = UUID.randomUUID();
        dto.setPatientId(patientId);

        User patient = new User();
        MedicalRecord record = new MedicalRecord();
        MedicalRecordResponseDTO responseDto = new MedicalRecordResponseDTO();

        when(userRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(mapper.toEntity(dto, patient)).thenReturn(record);
        when(mapper.toDto(record)).thenReturn(responseDto);

        // Act
        MedicalRecordResponseDTO result = service.createRecord(dto);

        // Assert
        assertEquals(responseDto, result);
        verify(userRepository).findById(patientId);
        verify(repository).save(record);
        verify(mapper).toEntity(dto, patient);
        verify(mapper).toDto(record);
    }

    @Test
    void testCreateRecord_PatientNotFound() {
        // Arrange
        MedicalRecordRequestDTO dto = new MedicalRecordRequestDTO();
        UUID patientId = UUID.randomUUID();
        dto.setPatientId(patientId);

        when(userRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        AppErorr error = assertThrows(AppErorr.class, () -> service.createRecord(dto));
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("Пациент не найден", error.getMessage());
        verify(userRepository).findById(patientId);
    }

    @Test
    void testUpdateRecord_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        MedicalRecordRequestDTO dto = new MedicalRecordRequestDTO();
        UUID patientId = UUID.randomUUID();
        dto.setPatientId(patientId);

        MedicalRecord existingRecord = new MedicalRecord();
        User patient = new User();
        MedicalRecord updatedRecord = new MedicalRecord();
        MedicalRecordResponseDTO responseDto = new MedicalRecordResponseDTO();

        when(repository.findById(id)).thenReturn(Optional.of(existingRecord));
        when(userRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(mapper.toEntity(dto, patient)).thenReturn(updatedRecord);
        when(mapper.toDto(updatedRecord)).thenReturn(responseDto);

        // Act
        MedicalRecordResponseDTO result = service.updateRecord(id, dto);

        // Assert
        assertEquals(responseDto, result);
        verify(repository).findById(id);
        verify(userRepository).findById(patientId);
        verify(repository).save(updatedRecord);
        verify(mapper).toEntity(dto, patient);
        verify(mapper).toDto(updatedRecord);
    }

    @Test
    void testUpdateRecord_RecordNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        MedicalRecordRequestDTO dto = new MedicalRecordRequestDTO();

        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        AppErorr error = assertThrows(AppErorr.class, () -> service.updateRecord(id, dto));
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("Запись не найдена", error.getMessage());
        verify(repository).findById(id);
    }

    @Test
    void testDeleteRecord() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        service.deleteRecord(id);

        // Assert
        verify(repository).deleteById(id);
    }
}
