package com.tos.tosmod.computer;

/** Funções Lua da tabela "redstone" (Fase 8) - controla antenas via wifi. */
public interface RedstoneApi {
    String read(int x, int y, int z);
    String send(int x, int y, int z, int strength);
    String pulse(int x, int y, int z, int strength, int ticks);
}
