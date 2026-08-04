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
            // TIER 2: aguenta CPU T1 + GPU T1 + RAM/storage básicos com folga (baseline ~130,
            // cooling 160 = ~30 de margem). Só CPU/GPU T2+ juntos empurram pro superaquecimento -
            // é o "custo" de um notebook gamer de verdade (potente, mas limitado por ser portátil).
            .cooling(160)
            .build();

    public static final CaseDefinition NOTEBOOK_THIN = new CaseDefinition.Builder("notebook_thin")
            .slot(SlotType.CPU_SOCKET, 1) // pensado pra ocupar com APU, não CPU+GPU separados
            .slot(SlotType.RAM_SLOT, 1)
            .slot(SlotType.STORAGE_SLOT, 1)
            .slot(SlotType.BATTERY_SLOT, 1)
            .portable(true)
            // TIER 1 (o mais baixo): só uma APU básica + RAM/storage já bate ~65 de calor -
            // cooling 90 garante que ligar do jeito mais simples possível NUNCA superaquece.
            .cooling(90)
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
            // TIER 2: CPU T1 + 1 GPU T1 + básicos = ~135 de calor, cooling 170 = margem boa.
            .cooling(170)
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
            // TIER 1/2: parecido com o notebook fino (sem GPU_SLOT, pensado pra APU), mas com
            // mais espaço interno por ser desktop - cooling 100 dá bastante folga sobre o
            // baseline (~70).
            .cooling(100)
            .build();

    /**
     * TIER 3 (premium) - o Mac Pro usava a MESMA definição da torre comum antes (mesmo
     * resfriamento!), por isso superaquecia igual a qualquer outra case só de ligar. Agora
     * tem a própria definição: mais slots de GPU/PSU e cooling bem mais alto, condizente
     * com ser "a melhor case" - aguenta até 2 GPUs T1 com folga real.
     */
    public static final CaseDefinition TOWER_DESKTOP_PRO = new CaseDefinition.Builder("tower_desktop_pro")
            .slot(SlotType.CPU_SOCKET, 1)
            .slot(SlotType.GPU_SLOT, 3)
            .slot(SlotType.RAM_SLOT, 6)
            .slot(SlotType.STORAGE_SLOT, 6)
            .slot(SlotType.PSU_SLOT, 2)
            .slot(SlotType.KEYBOARD_SLOT, 1)
            .slot(SlotType.MOUSE_SLOT, 1)
            .portable(false)
            .cooling(240)
            .hasIntegratedScreen(false)
            .build();

    public static final CaseDefinition SERVER_RACK = new CaseDefinition.Builder("server_rack")
            .slot(SlotType.CPU_SOCKET, 4)
            .slot(SlotType.GPU_SLOT, 4)
            .slot(SlotType.RAM_SLOT, 8)
            .slot(SlotType.STORAGE_SLOT, 8)
            .slot(SlotType.PSU_SLOT, 4)
            .portable(false)
            // TIER 4 (o maior bloco, aguenta o pesado de verdade): uma build robusta com CPU/GPU
            // T2 em dobro + memória/storage cheios ainda fica com boa margem. Só ENCHER tudo
            // com peça T3 (o máximo absoluto em 4 sockets de CPU e GPU) ainda seria demais até
            // pra ele - isso é intencional, um "build dos sonhos" 100% no talo deve continuar
            // sendo um desafio de engenharia, mesmo no maior servidor do mod.
            .cooling(600)
            .hasIntegratedScreen(false)
            .build();
}
