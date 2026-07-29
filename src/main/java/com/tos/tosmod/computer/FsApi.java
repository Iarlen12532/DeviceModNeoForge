package com.tos.tosmod.computer;

/**
 * Funções Lua da tabela "fs" (Fase 9) - sistema de arquivos virtual simples.
 * Cada "arquivo" é só um texto (tipicamente código Lua de um app) guardado no
 * armazenamento instalado na case - quanto mais capacidade de storage, mais espaço.
 */
public interface FsApi {
    String save(String name, String content);
    String load(String name);
    String list();
    String delete(String name);
}
