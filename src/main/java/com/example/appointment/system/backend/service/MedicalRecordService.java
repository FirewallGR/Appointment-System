package com.example.appointment.system.backend.service;

import com.example.appointment.system.backend.dto.medicalRecord.MedicalRecordRequestDTO;
import com.example.appointment.system.backend.dto.medicalRecord.MedicalRecordResponseDTO;
import com.example.appointment.system.backend.exception.AppErorr;
import com.example.appointment.system.backend.model.MedicalRecord;
import com.example.appointment.system.backend.model.User;
import com.example.appointment.system.backend.repository.MedicalRecordRepository;
import com.example.appointment.system.backend.repository.UserRepository;
import com.example.appointment.system.backend.utils.MedicalRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository repository;
    private final UserRepository userRepository;
    private final MedicalRecordMapper mapper;

    public List<MedicalRecordResponseDTO> getAllRecords() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MedicalRecordResponseDTO getRecordById(UUID id) {
        MedicalRecord record = repository.findById(id).orElseThrow(() ->
                new AppErorr(HttpStatus.NOT_FOUND.value(), "Запись не найдена"));
        return mapper.toDto(record);
    }

    public MedicalRecordResponseDTO createRecord(MedicalRecordRequestDTO dto) {
        User patient = userRepository.findById(dto.getPatientId()).orElseThrow(() ->
                new AppErorr(HttpStatus.NOT_FOUND.value(), "Пациент не найден"));
        MedicalRecord record = mapper.toEntity(dto, patient);
        repository.save(record);
        return mapper.toDto(record);
    }

    public MedicalRecordResponseDTO updateRecord(UUID id, MedicalRecordRequestDTO dto) {
        MedicalRecord record = repository.findById(id).orElseThrow(() ->
                new AppErorr(HttpStatus.NOT_FOUND.value(), "Запись не найдена"));
        User patient = userRepository.findById(dto.getPatientId()).orElseThrow(() ->
                new AppErorr(HttpStatus.NOT_FOUND.value(), "Пациент не найден"));

        MedicalRecord updatedRecord = mapper.toEntity(dto, patient);
        updatedRecord.setId(record.getId());
        repository.save(updatedRecord);
        return mapper.toDto(updatedRecord);
    }

    public void deleteRecord(UUID id) {
        repository.deleteById(id);
    }
}
