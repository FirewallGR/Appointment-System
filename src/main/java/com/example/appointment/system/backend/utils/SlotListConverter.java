package com.example.appointment.system.backend.utils;

import com.example.appointment.system.backend.model.Schedule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class SlotListConverter implements AttributeConverter<List<Schedule.Slot>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Schedule.Slot> slots) {
        try {
            return objectMapper.writeValueAsString(slots);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting list to JSON", e);
        }
    }

    @Override
    public List<Schedule.Slot> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Schedule.Slot>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to list", e);
        }
    }
}
