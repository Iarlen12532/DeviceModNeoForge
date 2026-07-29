package com.tos.tosmod.component;

/**
 * Motivo de um crash (estado CRASHED). Guardado separado do PowerState porque o
 * PowerState só tem UM valor "CRASHED" - isso aqui diferencia a causa real,
 * útil pra mensagem no jogo e futuramente pra tela de "erro" do TOS (Fase 4).
 */
public enum CrashCause {
    NONE("Nenhum"),
    PSU_OVERLOAD("Fonte de alimentação sobrecarregada"),
    OVERHEAT("Superaquecimento");

    private final String description;

    CrashCause(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
