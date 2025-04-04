package com.enterprise.backend.service.converter;

import com.enterprise.backend.model.SalaryRange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Slf4j
@Converter
public class SalaryRangeConverter implements AttributeConverter<SalaryRange, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SalaryRange salaryRange) {
        try {
            return objectMapper.writeValueAsString(salaryRange);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public SalaryRange convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, SalaryRange.class);
        } catch (Exception e) {
            return null;
        }
    }
}
