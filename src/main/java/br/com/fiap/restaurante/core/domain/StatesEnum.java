package br.com.fiap.restaurante.core.domain;

import lombok.Getter;

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

    public static StatesEnum fromValue(String value) {
        for (StatesEnum state : StatesEnum.values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Estado brasileiro inválido. Recebido o valor " + value);
    }
}
