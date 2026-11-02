package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.OpeningHours;
import br.com.fiap.restaurante.core.dto.OpeningHoursInputDto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface OpeningHoursFactory {

    OpeningHours create(OpeningHoursInputDto input);

    OpeningHours restore(Long id, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime);
}
