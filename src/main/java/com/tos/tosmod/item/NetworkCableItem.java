package com.tos.tosmod.item;

import com.tos.tosmod.block.entity.CaseBlockEntity;
import com.tos.tosmod.block.entity.PrinterBlockEntity;
import com.tos.tosmod.block.entity.RouterBlockEntity;
import com.tos.tosmod.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Cabo de rede (Fase 7/10): conecta um roteador a um computador ou impressora por cabo
 * físico, OU conecta dois servidores entre si formando um CLUSTER (Fase 10) - mesmo item
 * pros dois usos, pra não precisar de um tipo de cabo pra cada coisa. O que ele faz
 * depende do que você clica primeiro: roteador = modo rede; servidor = modo cluster.
 *
 * Cabo IGNORA distância (ver NetworkUtils.speedKbPerTick) e não precisa de senha (é uma
 * conexão física direta - diferente do sem fio, que pode ter senha).
 *
 * Uso: clique com o cabo no primeiro ponto, depois no segundo. Consumido (1 unidade) só
 * quando a ligação fecha dos dois lados.
 */
public class NetworkCableItem extends Item {

    public NetworkCableItem(Properties properties) {
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
            BlockEntity clickedEntity = level.getBlockEntity(clickedPos);
            if (clickedEntity instanceof RouterBlockEntity || isServerRack(clickedEntity)) {
                stack.set(ModDataComponents.CABLE_LINK_POS, clickedPos.immutable());
                message(player, clickedEntity instanceof RouterBlockEntity
                        ? "Cabo preso no roteador - agora clique num computador ou impressora."
                        : "Cabo preso no servidor - agora clique em outro servidor pra formar um cluster.");
                return InteractionResult.SUCCESS;
            }
            message(player, "Clique primeiro num roteador (rede) ou num servidor (cluster).");
            return InteractionResult.FAIL;
        }

        BlockEntity fromEntity = level.getBlockEntity(linkedFrom);

        // Modo cluster: o primeiro clique foi num servidor.
        if (isServerRack(fromEntity)) {
            BlockEntity toEntity = level.getBlockEntity(clickedPos);
            if (isServerRack(toEntity) && toEntity != fromEntity) {
                ((CaseBlockEntity) fromEntity).addClusterLink(clickedPos.immutable());
                ((CaseBlockEntity) toEntity).addClusterLink(linkedFrom);
                stack.shrink(1);
                message(player, "Servidores conectados em cluster!");
                return InteractionResult.SUCCESS;
            }
            message(player, "Clique em outro servidor pra fechar o cluster.");
            return InteractionResult.FAIL;
        }

        // Modo rede: o primeiro clique foi num roteador.
        boolean linked = false;
        if (level.getBlockEntity(clickedPos) instanceof CaseBlockEntity caseEntity) {
            caseEntity.setCableLinkedRouterPos(linkedFrom);
            linked = true;
        } else if (level.getBlockEntity(clickedPos) instanceof PrinterBlockEntity printerEntity) {
            printerEntity.setCableLinkedRouterPos(linkedFrom);
            linked = true;
        }

        if (linked) {
            stack.shrink(1);
            message(player, "Cabo conectado!");
            return InteractionResult.SUCCESS;
        }
        message(player, "Isso não é um computador nem uma impressora.");
        return InteractionResult.FAIL;
    }

    private boolean isServerRack(BlockEntity entity) {
        return entity instanceof CaseBlockEntity caseEntity
                && "server_rack".equals(caseEntity.getDefinition().id());
    }

    private void message(Player player, String text) {
        if (player != null) {
            player.displayClientMessage(Component.literal(text), true);
        }
    }
}
