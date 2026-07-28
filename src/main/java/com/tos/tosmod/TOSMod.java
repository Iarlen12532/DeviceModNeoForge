package com.tos.tosmod;

import com.tos.tosmod.network.ModNetworking;
import com.tos.tosmod.registry.ModBlockEntities;
import com.tos.tosmod.registry.ModBlocks;
import com.tos.tosmod.registry.ModCreativeTabs;
import com.tos.tosmod.registry.ModDataComponents;
import com.tos.tosmod.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Classe principal do mod TOS.
 *
 * Fase 1 (atual): registra os blocos de Case, os itens de componente
 * (CPU, APU, GPU, RAM, Storage, PSU, Bateria) e a BlockEntity que
 * guarda os slots e calcula se o computador liga ou não.
 *
 * As fases seguintes (temperatura, terminal Lua, TOS gráfico, rede,
 * periféricos, automação, servidores) vão se conectar em cima desta base
 * sem precisar reescrever o que já está aqui.
 */
@Mod(TOSMod.MOD_ID)
public class TOSMod {

    public static final String MOD_ID = "tosmod";

    public TOSMod(IEventBus modEventBus) {
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(ModNetworking::register);
    }
}
