package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;


@Getter
public class OpeningHours {

    private static final int SECONDS_PER_DAY = 24 * 60 * 60;
    private static final int SECONDS_PER_WEEK = 7 * SECONDS_PER_DAY;
    private static final Locale PT_BR = Locale.of("pt", "BR");

    private final Long id;
    private final DayOfWeek dayOfWeek;
    private final LocalTime openingTime;
    private final LocalTime closingTime;

    @Builder
    public OpeningHours(Long id, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.openingTime = openingTime;
        this.closingTime = closingTime;

        if (openingTime.isBefore(closingTime)) {
            throw new InvalidFieldException("O horário de abertura deve ser anterior ao horário de fechamento.");
        }
    }

    private static boolean intersects(int start, int end, int otherStart, int otherEnd) {
        return start < otherEnd && otherStart < end;
    }

    public boolean closesNextDay() {
        return closingTime.isBefore(openingTime);
    }

    /**
     * Indica se dois turnos têm algum momento em comum, considerando a virada da semana.
     */
    public boolean overlaps(OpeningHours other) {
        int start = startSecondOfWeek();
        int end = start + durationInSeconds();
        int otherStart = other.startSecondOfWeek();
        int otherEnd = otherStart + other.durationInSeconds();

        return intersects(start, end, otherStart, otherEnd)
               || intersects(start, end, otherStart + SECONDS_PER_WEEK, otherEnd + SECONDS_PER_WEEK)
               || intersects(start + SECONDS_PER_WEEK, end + SECONDS_PER_WEEK, otherStart, otherEnd);
    }

    private int startSecondOfWeek() {
        return (dayOfWeek.getValue() - 1) * SECONDS_PER_DAY + openingTime.toSecondOfDay();
    }

    private int durationInSeconds() {
        int duration = closingTime.toSecondOfDay() - openingTime.toSecondOfDay();
        return duration > 0 ? duration : duration + SECONDS_PER_DAY;
    }

    public String describe() {
        return dayOfWeek.getDisplayName(TextStyle.FULL, PT_BR) + " " + openingTime + "-" + closingTime;
    }

}
