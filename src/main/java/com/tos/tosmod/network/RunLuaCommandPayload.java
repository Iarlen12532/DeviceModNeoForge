package com.tos.tosmod.network;

import com.tos.tosmod.TOSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Enviado do cliente pro servidor quando o jogador digita um comando no terminal
 * (tela do TOS) e aperta enter. O servidor é quem manda de verdade o código pro
 * LuaComputer daquele computador - o cliente nunca executa Lua, só mostra a saída.
 */
public record RunLuaCommandPayload(BlockPos pos, String command) implements CustomPacketPayload {

    public static final Type<RunLuaCommandPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TOSMod.MOD_ID, "run_lua_command"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RunLuaCommandPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RunLuaCommandPayload::pos,
            ByteBufCodecs.STRING_UTF8, RunLuaCommandPayload::command,
            RunLuaCommandPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
