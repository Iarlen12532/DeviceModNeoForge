package com.tos.tosmod.computer;

/** Funções Lua da tabela "cluster" (Fase 10) - só faz sentido em servidores conectados. */
public interface ClusterApi {
    String status();
    String setResourceManagerActive(boolean active);
}
