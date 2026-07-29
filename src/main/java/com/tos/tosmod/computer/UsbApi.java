package com.tos.tosmod.computer;

/** Funções Lua da tabela "usb" (Fase 7) - lê/escreve no pen drive inserido, se houver. */
public interface UsbApi {
    String read();
    String write(String text);
}
