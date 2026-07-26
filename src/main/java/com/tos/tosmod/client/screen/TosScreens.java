package com.tos.tosmod.client.screen;

import com.tos.tosmod.block.entity.CaseBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Ponto único de decisão: se o computador já tem um SO instalado (osInstalled),
 * abre a DesktopScreen (Fase 9 - dock, barra de menu). Senão, abre o terminal cru
 * (Fase 3/4) - continua funcionando igual antes, com ou sem SO instalado.
 * Usado tanto por CaseBlock (clique direto) quanto por MonitorBlock (clique no monitor
 * externo, que abre a tela do computador linkado por cabo de vídeo).
 */
@OnlyIn(Dist.CLIENT)
public final class TosScreens {

    private TosScreens() {}

    public static void open(BlockPos pos) {
        if (Minecraft.getInstance().level != null
                && Minecraft.getInstance().level.getBlockEntity(pos) instanceof CaseBlockEntity caseEntity
                && caseEntity.isOsInstalled()) {
            DesktopScreen.openFor(pos);
        } else {
            TerminalScreen.openFor(pos);
        }
    }
}
