package com.tos.tosmod.network;

import com.tos.tosmod.TOSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Enviado pelo cliente quando o jogador aperta o botão de ligar/desligar numa case. */
public record SetPowerSwitchPayload(BlockPos pos, boolean on) implements CustomPacketPayload {

    public static final Type<SetPowerSwitchPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TOSMod.MOD_ID, "set_power_switch"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetPowerSwitchPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetPowerSwitchPayload::pos,
            ByteBufCodecs.BOOL, SetPowerSwitchPayload::on,
            SetPowerSwitchPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
