package com.enterprise.backend.service.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@RequiredArgsConstructor
public class ObjectConverter implements AttributeConverter<Object, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Object convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, Object.class);
        } catch (Exception ex) {
            return s;
        }
    }
}