package com.example.appointment.system.backend.repository;

import com.example.appointment.system.backend.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {
}
