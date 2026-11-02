package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.OpeningHours;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OpeningHoursOutputDto(Long id, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {

    public static OpeningHoursOutputDto from(OpeningHours openingHours) {
        return new OpeningHoursOutputDto(
                openingHours.getId(),
                openingHours.getDayOfWeek(),
                openingHours.getOpeningTime(),
                openingHours.getClosingTime()
        );
    }
}
