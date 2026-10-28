package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.OpeningHours;
import br.com.fiap.restaurante.core.dto.OpeningHoursInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.factory.OpeningHoursFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class OpeningHoursFactoryImpl implements OpeningHoursFactory {

    @Override
    public OpeningHours create(OpeningHoursInputDto input) {
        if (input == null) {
            throw new InvalidFieldException("O horário de funcionamento não pode ser nulo.");
        }

        return OpeningHours.builder()
                .dayOfWeek(input.dayOfWeek())
                .openingTime(input.openingTime())
                .closingTime(input.closingTime())
                .build();
    }

    @Override
    public OpeningHours restore(Long id, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
        return OpeningHours.builder()
                .id(id)
                .dayOfWeek(dayOfWeek)
                .openingTime(openingTime)
                .closingTime(closingTime)
                .build();
    }
}
