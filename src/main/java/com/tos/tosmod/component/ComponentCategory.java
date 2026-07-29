package com.tos.tosmod.component;

/**
 * Categoria de um componente de hardware.
 * Cada categoria sabe em qual tipo de slot ela se encaixa dentro de uma Case.
 */
public enum ComponentCategory {
    CPU(SlotType.CPU_SOCKET),
    APU(SlotType.CPU_SOCKET), // CPU com GPU integrada, ocupa o mesmo socket que uma CPU comum
    GPU(SlotType.GPU_SLOT),
    RAM(SlotType.RAM_SLOT),
    STORAGE(SlotType.STORAGE_SLOT),
    PSU(SlotType.PSU_SLOT),
    BATTERY(SlotType.BATTERY_SLOT),
    KEYBOARD(SlotType.KEYBOARD_SLOT),
    MOUSE(SlotType.MOUSE_SLOT),
    NETWORK_PROCESSOR(SlotType.NETWORK_PROCESSOR_SLOT);

    private final SlotType slotType;

    ComponentCategory(SlotType slotType) {
        this.slotType = slotType;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    /** Uma APU já entrega GPU integrada, então uma case com APU não exige GPU dedicada pra funcionar. */
    public boolean providesIntegratedGraphics() {
        return this == APU;
    }
}
