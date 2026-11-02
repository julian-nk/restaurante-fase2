package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum StatesEnum {

    AC("AC"),
    AL("AL"),
    AP("AP"),
    AM("AM"),
    BA("BA"),
    CE("CE"),
    DF("DF"),
    ES("ES"),
    GO("GO"),
    MA("MA"),
    MT("MT"),
    MS("MS"),
    MG("MG"),
    PA("PA"),
    PB("PB"),
    PR("PR"),
    PE("PE"),
    PI("PI"),
    RJ("RJ"),
    RN("RN"),
    RS("RS"),
    RO("RO"),
    RR("RR"),
    SC("SC"),
    SP("SP"),
    SE("SE"),
    TO("TO");

    private final String value;

    StatesEnum(String value) {
        this.value = value;
    }

    /** Converte a sigla da UF, sem diferenciar maiúsculas de minúsculas ("sp" e "SP" valem). */
    public static StatesEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidFieldException("O campo UF é obrigatório.");
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (StatesEnum state : StatesEnum.values()) {
            if (state.getValue().equals(normalized)) {
                return state;
            }
        }
        throw new InvalidFieldException("Estado brasileiro inválido. Recebido o valor " + value + ".");
    }
}
