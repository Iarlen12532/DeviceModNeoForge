package com.tos.tosmod.component;

/**
 * Tipos de slot físico dentro de uma Case.
 * Uma CaseDefinition diz quantos slots de cada tipo ela tem;
 * o modelo 3D/textura da case é totalmente independente disso.
 */
public enum SlotType {
    CPU_SOCKET,
    GPU_SLOT,
    RAM_SLOT,
    STORAGE_SLOT,
    PSU_SLOT,      // só existe em cases tipo desktop/torre/servidor
    BATTERY_SLOT,  // só existe em cases tipo notebook
    KEYBOARD_SLOT, // só existe em cases fixas - notebook já tem teclado embutido
    MOUSE_SLOT,    // só existe em cases fixas - notebook já tem trackpad embutido
    NETWORK_PROCESSOR_SLOT // só existe no roteador
}
