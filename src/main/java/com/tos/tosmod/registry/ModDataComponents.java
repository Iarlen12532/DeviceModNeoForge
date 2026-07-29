package com.tos.tosmod.registry;

import com.tos.tosmod.TOSMod;
import com.tos.tosmod.component.ComponentStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TOSMod.MOD_ID);

    // Guarda os atributos técnicos (ComponentStats) em qualquer item de componente de hardware.
    public static final net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<ComponentStats>> COMPONENT_STATS =
            DATA_COMPONENTS.register("component_stats", () -> DataComponentType.<ComponentStats>builder()
                    .persistent(ComponentStats.CODEC)
                    .networkSynchronized(ComponentStats.STREAM_CODEC)
                    .build());

    // Fase 7: guarda a posição do roteador já "clicado" num item de Cabo de Rede, enquanto
    // o jogador ainda não clicou no segundo ponto (computador/impressora) pra fechar a ligação.
    public static final net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> CABLE_LINK_POS =
            DATA_COMPONENTS.register("cable_link_pos", () -> DataComponentType.<BlockPos>builder()
                    .persistent(BlockPos.CODEC)
                    .networkSynchronized(BlockPos.STREAM_CODEC)
                    .build());

    // Fase 7: texto impresso guardado numa folha de papel (item PrintedPaperItem).
    public static final net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<String>> PRINTED_TEXT =
            DATA_COMPONENTS.register("printed_text", () -> DataComponentType.<String>builder()
                    .persistent(com.mojang.serialization.Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());

    // Fase 7: conteúdo guardado num pen drive (item PenDriveItem) - "arquivo" simples de texto.
    public static final net.neoforged.neoforge.registries.DeferredHolder<DataComponentType<?>, DataComponentType<String>> USB_CONTENT =
            DATA_COMPONENTS.register("usb_content", () -> DataComponentType.<String>builder()
                    .persistent(com.mojang.serialization.Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());
}
