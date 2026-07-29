package com.tos.tosmod.item;

import com.tos.tosmod.block.entity.CaseBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Pendrive instalador do TOS (Fase 6). Diferente do download por rede, esse é
 * instantâneo - igual instalar de um pendrive de verdade, sem depender de roteador
 * nenhum. Uso: clicar com ele numa Case ligada.
 */
public class TosInstallerUsbItem extends Item {

    public TosInstallerUsbItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(context.getClickedPos()) instanceof CaseBlockEntity caseEntity)) {
            return InteractionResult.PASS;
        }

        if (!caseEntity.getPowerState().isOn()) {
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(
                        Component.literal("A máquina precisa estar ligada pra instalar."), true);
            }
            return InteractionResult.FAIL;
        }

        boolean installed = caseEntity.installFromUsb();
        if (installed && context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(Component.literal("TOS instalado com sucesso!"), true);
        }
        return InteractionResult.SUCCESS;
    }
}
