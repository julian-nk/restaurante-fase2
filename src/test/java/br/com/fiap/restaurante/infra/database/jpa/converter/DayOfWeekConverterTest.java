package br.com.fiap.restaurante.infra.database.jpa.converter;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

class DayOfWeekConverterTest {

    private final DayOfWeekConverter converter = new DayOfWeekConverter();

    @Test
    void shouldUseIsoNumberingMondayOneSundaySeven() {
        assertEquals((short) 1, converter.convertToDatabaseColumn(DayOfWeek.MONDAY));
        assertEquals((short) 7, converter.convertToDatabaseColumn(DayOfWeek.SUNDAY));
        assertEquals(DayOfWeek.MONDAY, converter.convertToEntityAttribute((short) 1));
        assertEquals(DayOfWeek.SUNDAY, converter.convertToEntityAttribute((short) 7));
    }

    @Test
    void shouldHandleNull() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
    }
}
