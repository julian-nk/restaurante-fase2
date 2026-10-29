package br.com.fiap.restaurante.core.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OpeningHoursInputDto(DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
}
