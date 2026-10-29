package br.com.fiap.restaurante.infra.database.jpa.entity;

import br.com.fiap.restaurante.infra.database.jpa.converter.DayOfWeekConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "opening_hours")
public class OpeningHoursEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    // SMALLINT 1..7 (1 = segunda, 7 = domingo)
    @Convert(converter = DayOfWeekConverter.class)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    protected OpeningHoursEntity() {
    }
}
