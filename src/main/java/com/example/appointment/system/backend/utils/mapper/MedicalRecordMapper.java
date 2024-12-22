package com.example.appointment.system.backend.utils;

import com.example.appointment.system.backend.dto.medicalRecord.MedicalRecordRequestDTO;
import com.example.appointment.system.backend.dto.medicalRecord.MedicalRecordResponseDTO;
import com.example.appointment.system.backend.model.MedicalRecord;
import com.example.appointment.system.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordMapper {

    public MedicalRecord toEntity(MedicalRecordRequestDTO dto, User patient) {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatientId(dto.getPatientId());
        medicalRecord.setDoctorId(dto.getDoctorId());
        medicalRecord.setDate(dto.getDate());
        medicalRecord.setTime(dto.getTime());

        MedicalRecord.Report report = new MedicalRecord.Report();
        report.setPatientName(patient.getName() + " " + patient.getSecondName() +
                (patient.getThirdName() != null ? " " + patient.getThirdName() : ""));
        report.setComplaints(dto.getComplaints());
        report.setDiagnosis(dto.getDiagnosis());
        report.setExaminations(dto.getExaminations());
        report.setTreatment(dto.getTreatment());

        medicalRecord.setReport(report);
        return medicalRecord;
    }

    public MedicalRecordResponseDTO toDto(MedicalRecord entity) {
        MedicalRecordResponseDTO dto = new MedicalRecordResponseDTO();
        dto.setId(entity.getId());
        dto.setPatientId(entity.getPatientId());
        dto.setDoctorId(entity.getDoctorId());
        dto.setDate(entity.getDate());
        dto.setTime(entity.getTime());

        MedicalRecordResponseDTO.ReportDTO reportDTO = new MedicalRecordResponseDTO.ReportDTO();
        reportDTO.setPatientName(entity.getReport().getPatientName());
        reportDTO.setComplaints(entity.getReport().getComplaints());
        reportDTO.setDiagnosis(entity.getReport().getDiagnosis());
        reportDTO.setExaminations(entity.getReport().getExaminations());
        reportDTO.setTreatment(entity.getReport().getTreatment());

        dto.setReport(reportDTO);
        return dto;
    }
}
