package br.com.fiap.restaurante.domain.entities.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.DayOfWeek;

/**
 * Converte {@link DayOfWeek} para a coluna SMALLINT {@code day_of_week}
 * (1 = segunda ... 7 = domingo, mesma numeração ISO usada por {@link DayOfWeek#getValue()}).
 */
@Converter
public class DayOfWeekConverter implements AttributeConverter<DayOfWeek, Short> {

    @Override
    public Short convertToDatabaseColumn(DayOfWeek dayOfWeek) {
        return dayOfWeek == null ? null : (short) dayOfWeek.getValue();
    }

    @Override
    public DayOfWeek convertToEntityAttribute(Short value) {
        return value == null ? null : DayOfWeek.of(value);
    }
}
