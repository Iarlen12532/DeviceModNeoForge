package com.tos.tosmod.component;

/**
 * Catálogo central das CaseDefinitions. Quando você tiver os modelos 3D prontos,
 * cada modelo vira um CaseBlock novo apontando pra uma dessas definições
 * (ou pra uma nova, se o layout de slots for diferente) - o código de lógica
 * (CaseBlockEntity) nunca muda.
 */
public final class CaseDefinitions {

    private CaseDefinitions() {}

    public static final CaseDefinition NOTEBOOK_GAMER = new CaseDefinition.Builder("notebook_gamer")
            .slot(SlotType.CPU_SOCKET, 1)
            .slot(SlotType.GPU_SLOT, 1)
            .slot(SlotType.RAM_SLOT, 2)
            .slot(SlotType.STORAGE_SLOT, 2)
            .slot(SlotType.BATTERY_SLOT, 1)
            .portable(true)
            .cooling(90) // notebook gamer tem mais espaço pra cooler que um fino, mas menos que desktop
            .build();

    public static final CaseDefinition NOTEBOOK_THIN = new CaseDefinition.Builder("notebook_thin")
            .slot(SlotType.CPU_SOCKET, 1) // pensado pra ocupar com APU, não CPU+GPU separados
            .slot(SlotType.RAM_SLOT, 1)
            .slot(SlotType.STORAGE_SLOT, 1)
            .slot(SlotType.BATTERY_SLOT, 1)
            .portable(true)
            .cooling(60) // pouco espaço pra dissipar calor - combina com APU, não com GPU dedicada
            .build();

    public static final CaseDefinition TOWER_DESKTOP = new CaseDefinition.Builder("tower_desktop")
            .slot(SlotType.CPU_SOCKET, 1)
            .slot(SlotType.GPU_SLOT, 2)
            .slot(SlotType.RAM_SLOT, 4)
            .slot(SlotType.STORAGE_SLOT, 4)
            .slot(SlotType.PSU_SLOT, 1)
            .slot(SlotType.KEYBOARD_SLOT, 1)
            .slot(SlotType.MOUSE_SLOT, 1)
            .portable(false)
            .cooling(140)
            .hasIntegratedScreen(false)
            .build();

    public static final CaseDefinition ALL_IN_ONE = new CaseDefinition.Builder("all_in_one")
            .slot(SlotType.CPU_SOCKET, 1)
            .slot(SlotType.RAM_SLOT, 2)
            .slot(SlotType.STORAGE_SLOT, 2)
            .slot(SlotType.PSU_SLOT, 1)
            .slot(SlotType.KEYBOARD_SLOT, 1)
            .slot(SlotType.MOUSE_SLOT, 1)
            .portable(false)
            .cooling(70) // parecido com o notebook: pouco espaço interno, pensado pra APU
            .build();

    public static final CaseDefinition SERVER_RACK = new CaseDefinition.Builder("server_rack")
            .slot(SlotType.CPU_SOCKET, 4)
            .slot(SlotType.GPU_SLOT, 4)
            .slot(SlotType.RAM_SLOT, 8)
            .slot(SlotType.STORAGE_SLOT, 8)
            .slot(SlotType.PSU_SLOT, 4)
            .portable(false)
            .cooling(280)
            .hasIntegratedScreen(false)
            .build();
}
