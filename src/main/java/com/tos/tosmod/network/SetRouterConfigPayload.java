package com.tos.tosmod.network;

import com.tos.tosmod.TOSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Enviado pelo cliente quando o jogador salva o nome/senha na RouterScreen (Fase 7). */
public record SetRouterConfigPayload(BlockPos pos, String name, String password) implements CustomPacketPayload {

    public static final Type<SetRouterConfigPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TOSMod.MOD_ID, "set_router_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetRouterConfigPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetRouterConfigPayload::pos,
            ByteBufCodecs.STRING_UTF8, SetRouterConfigPayload::name,
            ByteBufCodecs.STRING_UTF8, SetRouterConfigPayload::password,
            SetRouterConfigPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
