package com.tos.tosmod.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Atributos técnicos de um componente de hardware (CPU, APU, GPU, RAM, Storage, PSU, Bateria).
 * Isso é anexado ao ItemStack via Data Component (API padrão do NeoForge 1.21.1).
 *
 * Nem todo campo se aplica a toda categoria — cada categoria só olha os campos que importam
 * pra ela (ex: RAM não olha wattSupply, PSU não olha performance).
 *
 * @param category      categoria do componente (define o slot em que encaixa)
 * @param tier          nível geral 1-3+, usado como atalho de progressão/crafting
 * @param performance   poder de processamento "genérico" (usado por CPU/APU/GPU/RAM/Storage
 *                      de formas diferentes nas fases seguintes: multitarefa, velocidade de boot, etc.)
 * @param wattDraw      quanto esse componente consome (relevante pra PSU e pra bateria descarregar)
 * @param heatOutput    quanto calor esse componente gera (usado na Fase 2 - temperatura)
 * @param capacity      capacidade em unidades genéricas (GB de RAM, GB de storage, Wh de bateria)
 * @param wattSupply    só relevante pra PSU: quantos watts ela consegue fornecer no total
 * @param series        linha do componente (ex: "risen", "xarm") - só relevante pra CPU/APU, usado em crafting/lore
 */
public record ComponentStats(
        ComponentCategory category,
        int tier,
        int performance,
        int wattDraw,
        int heatOutput,
        int capacity,
        int wattSupply,
        String series
) {

    public static final Codec<ComponentStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(ComponentCategory::valueOf, Enum::name).fieldOf("category").forGetter(ComponentStats::category),
            Codec.INT.fieldOf("tier").forGetter(ComponentStats::tier),
            Codec.INT.fieldOf("performance").forGetter(ComponentStats::performance),
            Codec.INT.fieldOf("watt_draw").forGetter(ComponentStats::wattDraw),
            Codec.INT.fieldOf("heat_output").forGetter(ComponentStats::heatOutput),
            Codec.INT.fieldOf("capacity").forGetter(ComponentStats::capacity),
            Codec.INT.fieldOf("watt_supply").forGetter(ComponentStats::wattSupply),
            Codec.STRING.fieldOf("series").forGetter(ComponentStats::series)
    ).apply(instance, ComponentStats::new));

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ComponentStats> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(ComponentCategory::valueOf, Enum::name), ComponentStats::category,
            ByteBufCodecs.VAR_INT, ComponentStats::tier,
            ByteBufCodecs.VAR_INT, ComponentStats::performance,
            ByteBufCodecs.VAR_INT, ComponentStats::wattDraw,
            ByteBufCodecs.VAR_INT, ComponentStats::heatOutput,
            ByteBufCodecs.VAR_INT, ComponentStats::capacity,
            ByteBufCodecs.VAR_INT, ComponentStats::wattSupply,
            ByteBufCodecs.STRING_UTF8, ComponentStats::series,
            ComponentStats::new
    );
}
