package com.tos.tosmod.item;

import com.tos.tosmod.block.entity.CaseBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Pen drive (Fase 7 - mídia externa): item físico que carrega um texto simples entre
 * computadores. Clique numa case pra inserir (se o slot USB dela estiver vazio) ou,
 * agachado, pra retirar. O conteúdo é lido/escrito pelo Lua via a tabela "usb"
 * (usb.read()/usb.write(texto)) enquanto ele estiver inserido.
 */
public class PenDriveItem extends Item {

    public PenDriveItem(Properties properties) {
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
        Player player = context.getPlayer();

        if (player != null && player.isCrouching()) {
            ItemStack removed = caseEntity.removeUsbDrive();
            if (!removed.isEmpty() && !player.getInventory().add(removed)) {
                player.drop(removed, false);
            }
            return InteractionResult.SUCCESS;
        }

        ItemStack toInsert = context.getItemInHand().copyWithCount(1);
        if (caseEntity.insertUsbDrive(toInsert)) {
            context.getItemInHand().shrink(1);
            if (player != null) {
                player.displayClientMessage(Component.literal("Pen drive inserido."), true);
            }
        } else if (player != null) {
            player.displayClientMessage(Component.literal("Já tem um pen drive inserido (agache + clique pra retirar)."), true);
        }
        return InteractionResult.SUCCESS;
    }
}
