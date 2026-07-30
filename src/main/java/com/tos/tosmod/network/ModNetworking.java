package com.tos.tosmod.network;

import com.tos.tosmod.TOSMod;
import com.tos.tosmod.block.entity.CaseBlockEntity;
import com.tos.tosmod.block.entity.RouterBlockEntity;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    // Limite de segurança - evita que um cliente malicioso mande comandos gigantes.
    private static final int MAX_COMMAND_LENGTH = 4096;
    private static final int MAX_ROUTER_FIELD_LENGTH = 64;

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(TOSMod.MOD_ID).versioned("1");

        registrar.playToServer(
                RunLuaCommandPayload.TYPE,
                RunLuaCommandPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (payload.command().length() > MAX_COMMAND_LENGTH) {
                        return;
                    }
                    if (context.player().level().getBlockEntity(payload.pos()) instanceof CaseBlockEntity caseEntity) {
                        // Só executa se a máquina estiver realmente ligada - checagem dupla,
                        // o cliente já não deveria deixar mandar nesse caso, mas nunca confie só no cliente.
                        if (caseEntity.getPowerState().isOn()) {
                            caseEntity.runLuaCommand(payload.command());
                        }
                    }
                })
        );

        registrar.playToServer(
                SetRouterConfigPayload.TYPE,
                SetRouterConfigPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (payload.name().length() > MAX_ROUTER_FIELD_LENGTH || payload.password().length() > MAX_ROUTER_FIELD_LENGTH) {
                        return;
                    }
                    if (context.player().level().getBlockEntity(payload.pos()) instanceof RouterBlockEntity router) {
                        router.setRouterConfig(payload.name(), payload.password());
                    }
                })
        );

        registrar.playToServer(
                SetPowerSwitchPayload.TYPE,
                SetPowerSwitchPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player().level().getBlockEntity(payload.pos()) instanceof CaseBlockEntity caseEntity) {
                        caseEntity.setPowerSwitch(payload.on());
                    }
                })
        );
    }
}
