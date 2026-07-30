package com.tos.tosmod.component;

/**
 * Resultado da tentativa de ligar uma Case. Cada valor vira uma mensagem
 * diferente pro jogador (e depois, uma tela diferente no boot do TOS).
 */
public enum PowerState {
    OFF("Desligado (aperte o botão de ligar)"),
    ON("Ligado"),
    NO_CPU("Falta CPU ou APU (agache + clique na case pra abrir o hardware)"),
    NO_RAM("Falta memória RAM (agache + clique na case pra abrir o hardware)"),
    NO_STORAGE("Falta armazenamento (agache + clique na case pra abrir o hardware)"),
    NO_BATTERY("Falta bateria (agache + clique na case pra abrir o hardware)"),
    NO_PSU("Falta fonte de alimentação (agache + clique na case pra abrir o hardware)"),
    UNSTABLE_PSU("Ligado, mas a fonte não aguenta os componentes - vai travar em breve"),
    OVERHEATING("Ligado, mas superaquecendo - vai travar se não esfriar"),
    CRASHED("Travou - aperte o botão de ligar de novo pra tentar reiniciar");

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
