package com.tos.tosmod.computer;

/**
 * Funções que o script Lua do jogador pode chamar (tabela "network" no terminal).
 * Implementada pela CaseBlockEntity e executada sempre na main thread, via MainThreadBridge -
 * o objeto em si é só uma interface simples, sem preocupação de thread aqui dentro.
 */
public interface NetworkApi {

    String status();

    String installTos();

    String setPassword(String password);

    String sendOsTo(int x, int y, int z);
}
