package com.tos.tosmod.component;

/**
 * Resultado da tentativa de ligar uma Case. Cada valor vira uma mensagem
 * diferente pro jogador (e depois, uma tela diferente no boot do TOS).
 */
public enum PowerState {
    ON("Ligado"),
    NO_CPU("Falta CPU ou APU"),
    NO_RAM("Falta memória RAM"),
    NO_STORAGE("Falta armazenamento (HD/SSD/NVMe)"),
    NO_BATTERY("Falta bateria"),
    NO_PSU("Falta fonte de alimentação"),
    UNSTABLE_PSU("Ligado, mas a fonte não aguenta os componentes - vai travar em breve"),
    OVERHEATING("Ligado, mas superaquecendo - vai travar se não esfriar"),
    CRASHED("Travou - desligue e ligue de novo pra tentar reiniciar");

    private final String description;

    PowerState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /** ON, UNSTABLE_PSU e OVERHEATING contam como "ligado" pro resto do sistema (Lua, tela, etc.) -
     *  só que os dois últimos estão numa contagem regressiva até crashar. */
    public boolean isOn() {
        return this == ON || this == UNSTABLE_PSU || this == OVERHEATING;
    }
}
