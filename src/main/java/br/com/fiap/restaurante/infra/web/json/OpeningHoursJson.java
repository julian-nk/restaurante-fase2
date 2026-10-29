package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.OpeningHoursInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Ex.: {"dayOfWeek": "FRIDAY", "openingTime": "18:00", "closingTime": "02:00"}.
 */
@Schema(description = "Horário de funcionamento em um dia da semana.")
public record OpeningHoursJson(
        @NotNull(message = "O dia da semana é obrigatório.")
        @Schema(description = "Dia da semana, em inglês (MONDAY a SUNDAY).", example = "FRIDAY")
        DayOfWeek dayOfWeek,

        @NotNull(message = "O horário de abertura é obrigatório.")
        @Schema(description = "Horário de abertura (HH:mm).", example = "18:00")
        LocalTime openingTime,

        @NotNull(message = "O horário de fechamento é obrigatório.")
        @Schema(description = "Horário de fechamento (HH:mm). Se for anterior à abertura, o restaurante fecha no dia seguinte.", example = "02:00")
        LocalTime closingTime
) {

    public OpeningHoursInputDto toInputDto() {
        return new OpeningHoursInputDto(dayOfWeek, openingTime, closingTime);
    }

    static List<OpeningHoursInputDto> toInputDtos(List<OpeningHoursJson> json) {
        return json == null ? null : json.stream().map(OpeningHoursJson::toInputDto).toList();
    }
}
