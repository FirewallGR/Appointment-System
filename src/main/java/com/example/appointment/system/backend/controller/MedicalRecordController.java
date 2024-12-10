package com.example.appointment.system.backend.controller;

import com.example.appointment.system.backend.dto.MedicalRecordRequestDTO;
import com.example.appointment.system.backend.dto.MedicalRecordResponseDTO;
import com.example.appointment.system.backend.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {
    private final MedicalRecordService service;

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAllRecords() {
        return ResponseEntity.ok(service.getAllRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getRecordById(id));
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> createRecord(
            @RequestBody MedicalRecordRequestDTO dto) {
        return ResponseEntity.ok(service.createRecord(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> updateRecord(
            @PathVariable UUID id,
            @RequestBody MedicalRecordRequestDTO dto) {
        return ResponseEntity.ok(service.updateRecord(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable UUID id) {
        service.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
