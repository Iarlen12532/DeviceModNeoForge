package com.tos.tosmod.component;

import com.tos.tosmod.registry.ModItems;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabela central com os valores de cada item de componente.
 * Ficar tudo num lugar só facilita balancear depois (é só mudar os números aqui,
 * sem mexer em Item, BlockEntity, etc.)
 *
 * performance / wattDraw / heatOutput / capacity / wattSupply são todos números
 * "de jogo" (não watts reais) - dá pra recalibrar à vontade.
 */
public final class ComponentStatsDefaults {

    private static final Map<Item, ComponentStats> DEFAULTS = new HashMap<>();

    private ComponentStatsDefaults() {}

    static {
        // CPU Risen: mais barata, mais forte em performance bruta, mas mais quente
        put(ModItems.CPU_RISEN_T1.get(), new ComponentStats(ComponentCategory.CPU, 1, 40, 65, 40, 0, 0, "risen"));
        put(ModItems.CPU_RISEN_T2.get(), new ComponentStats(ComponentCategory.CPU, 2, 65, 95, 65, 0, 0, "risen"));
        put(ModItems.CPU_RISEN_T3.get(), new ComponentStats(ComponentCategory.CPU, 3, 90, 130, 95, 0, 0, "risen"));

        // CPU Xarm: mais cara, mais eficiente (menos watt/calor pro mesmo desempenho)
        put(ModItems.CPU_XARM_T1.get(), new ComponentStats(ComponentCategory.CPU, 1, 50, 30, 20, 0, 0, "xarm"));
        put(ModItems.CPU_XARM_T2.get(), new ComponentStats(ComponentCategory.CPU, 2, 75, 45, 30, 0, 0, "xarm"));
        put(ModItems.CPU_XARM_T3.get(), new ComponentStats(ComponentCategory.CPU, 3, 100, 60, 40, 0, 0, "xarm"));

        // APU: menos consumo/calor que CPU+GPU separados, mais que CPU sozinha
        put(ModItems.APU_RISEN_T1.get(), new ComponentStats(ComponentCategory.APU, 1, 35, 55, 45, 0, 0, "risen"));
        put(ModItems.APU_XARM_T1.get(), new ComponentStats(ComponentCategory.APU, 1, 45, 35, 25, 0, 0, "xarm"));

        // GPU dedicada: quanto maior o tier, mais monitores e mais consumo/calor
        put(ModItems.GPU_T1.get(), new ComponentStats(ComponentCategory.GPU, 1, 50, 120, 70, 0, 0, ""));
        put(ModItems.GPU_T2.get(), new ComponentStats(ComponentCategory.GPU, 2, 90, 220, 120, 0, 0, ""));
        put(ModItems.GPU_T3.get(), new ComponentStats(ComponentCategory.GPU, 3, 140, 350, 180, 0, 0, ""));

        // RAM: capacity define quantos apps/threads simultâneos (usado na Fase 3+)
        put(ModItems.RAM_STICK_T1.get(), new ComponentStats(ComponentCategory.RAM, 1, 0, 5, 5, 8, 0, ""));
        put(ModItems.RAM_STICK_T2.get(), new ComponentStats(ComponentCategory.RAM, 2, 0, 8, 8, 16, 0, ""));

        // Armazenamento: capacity = espaço, performance = velocidade de leitura/escrita
        put(ModItems.STORAGE_HDD.get(), new ComponentStats(ComponentCategory.STORAGE, 1, 15, 10, 15, 500, 0, ""));
        put(ModItems.STORAGE_SSD.get(), new ComponentStats(ComponentCategory.STORAGE, 2, 45, 8, 10, 500, 0, ""));
        put(ModItems.STORAGE_NVME.get(), new ComponentStats(ComponentCategory.STORAGE, 3, 90, 12, 8, 1000, 0, ""));

        // PSU: wattSupply é o que importa (quanto ela aguenta fornecer no total)
        put(ModItems.PSU_500W.get(), new ComponentStats(ComponentCategory.PSU, 1, 0, 0, 5, 0, 500, ""));
        put(ModItems.PSU_1000W.get(), new ComponentStats(ComponentCategory.PSU, 2, 0, 0, 8, 0, 1000, ""));

        // Bateria: capacity = carga máxima (Wh de jogo)
        put(ModItems.BATTERY_BASIC.get(), new ComponentStats(ComponentCategory.BATTERY, 1, 0, 0, 0, 200, 0, ""));

        // Teclado e mouse: não consomem/geram nada relevante ainda - só precisam existir
        // (categoria certa) pra CaseBlockEntity.hasKeyboard()/hasMouse() enxergar.
        put(ModItems.KEYBOARD_BASIC.get(), new ComponentStats(ComponentCategory.KEYBOARD, 1, 0, 1, 0, 0, 0, ""));
        put(ModItems.MOUSE_BASIC.get(), new ComponentStats(ComponentCategory.MOUSE, 1, 0, 1, 0, 0, 0, ""));

        // Processador de rede: "performance" aqui representa a força do roteador - usada
        // pra calcular alcance, nº de dispositivos e velocidade (ver component/NetworkUtils).
        put(ModItems.NETWORK_PROCESSOR_T1.get(), new ComponentStats(ComponentCategory.NETWORK_PROCESSOR, 1, 40, 15, 10, 0, 0, ""));
        put(ModItems.NETWORK_PROCESSOR_T2.get(), new ComponentStats(ComponentCategory.NETWORK_PROCESSOR, 2, 90, 25, 18, 0, 0, ""));
        put(ModItems.NETWORK_PROCESSOR_T3.get(), new ComponentStats(ComponentCategory.NETWORK_PROCESSOR, 3, 160, 40, 28, 0, 0, ""));
    }

    private static void put(Item item, ComponentStats stats) {
        DEFAULTS.put(item, stats);
    }

    public static ComponentStats get(Item item) {
        return DEFAULTS.get(item);
    }
}
