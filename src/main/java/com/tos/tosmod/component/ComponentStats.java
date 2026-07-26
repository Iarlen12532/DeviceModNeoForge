package com.tos.tosmod.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    // StreamCodec.composite() só tem overload até 6 campos - com 8 campos aqui, precisa
    // escrever a codificação/decodificação manualmente em vez de usar o builder.
    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ComponentStats> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeUtf(value.category().name());
                buf.writeVarInt(value.tier());
                buf.writeVarInt(value.performance());
                buf.writeVarInt(value.wattDraw());
                buf.writeVarInt(value.heatOutput());
                buf.writeVarInt(value.capacity());
                buf.writeVarInt(value.wattSupply());
                buf.writeUtf(value.series());
            },
            buf -> new ComponentStats(
                    ComponentCategory.valueOf(buf.readUtf()),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readUtf()
            )
    );
}
