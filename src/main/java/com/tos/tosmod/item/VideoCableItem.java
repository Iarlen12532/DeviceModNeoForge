package com.tos.tosmod.item;

import com.tos.tosmod.block.entity.CaseBlockEntity;
import com.tos.tosmod.block.entity.IndustrialMonitorBlockEntity;
import com.tos.tosmod.block.entity.MonitorBlockEntity;
import com.tos.tosmod.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Cabo de vídeo (Fase 8) - tipo um "HDMI/DisplayPort" separado do cabo de rede.
 * Conecta uma case sem tela integrada (torre, servidor) a um bloco de Monitor.
 * Uso: clique primeiro no computador, depois no monitor. Consumido ao fechar a ligação.
 */
public class VideoCableItem extends Item {

    public VideoCableItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockPos linkedFrom = stack.get(ModDataComponents.CABLE_LINK_POS);

        if (linkedFrom == null) {
            if (level.getBlockEntity(clickedPos) instanceof CaseBlockEntity) {
                stack.set(ModDataComponents.CABLE_LINK_POS, clickedPos.immutable());
                message(player, "Cabo de vídeo preso no computador - agora clique num monitor.");
                return InteractionResult.SUCCESS;
            }
            message(player, "Clique primeiro num computador com o cabo de vídeo.");
            return InteractionResult.FAIL;
        }

        if (level.getBlockEntity(clickedPos) instanceof MonitorBlockEntity monitor) {
            monitor.setLinkedComputerPos(linkedFrom);
            stack.shrink(1);
            message(player, "Monitor conectado!");
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(clickedPos) instanceof IndustrialMonitorBlockEntity industrialMonitor) {
            industrialMonitor.setLinkedComputerPos(linkedFrom);
            if (level.getBlockEntity(linkedFrom) instanceof CaseBlockEntity caseEntity) {
                // Vantagem do industrial: também funciona como teclado+mouse embutidos,
                // resolve o caso do servidor (headless, sem slot próprio de periférico).
                caseEntity.setIndustrialMonitorLinked(true);
            }
            stack.shrink(1);
            message(player, "Monitor industrial conectado (também serve de teclado+mouse)!");
            return InteractionResult.SUCCESS;
        }
        message(player, "Isso não é um monitor.");
        return InteractionResult.FAIL;
    }

    private void message(Player player, String text) {
        if (player != null) {
            player.displayClientMessage(Component.literal(text), true);
        }
    }
}
