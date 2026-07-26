package com.tos.tosmod.block;

import com.tos.tosmod.block.entity.RouterBlockEntity;
import com.tos.tosmod.component.ComponentCategory;
import com.tos.tosmod.component.ComponentStats;
import com.tos.tosmod.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Bloco de roteador. Interação simples, sem GUI própria ainda (igual um "furnace" de 1
 * slot só): clica com um processador de rede na mão pra instalar, clica com a mão vazia
 * pra retirar. Isso é suficiente pra Fase 6 - uma tela dedicada pode vir depois se fizer falta.
 */
public class RouterBlock extends BaseEntityBlock {

    public RouterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RouterBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof RouterBlockEntity router) {
                router.tick();
            }
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof RouterBlockEntity router)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        ComponentStats stats = stack.get(ModDataComponents.COMPONENT_STATS);
        boolean isNetworkProcessor = stats != null && stats.category() == ComponentCategory.NETWORK_PROCESSOR;
        if (!isNetworkProcessor || !router.getInventory().getStackInSlot(0).isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        ItemStack toInsert = stack.copyWithCount(1);
        router.getInventory().setStackInSlot(0, toInsert);
        stack.shrink(1);
        player.displayClientMessage(Component.literal("Processador de rede instalado. Força do roteador: "
                + router.getPower()), true);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isCrouching()) {
            if (level.isClientSide()) {
                com.tos.tosmod.client.screen.RouterScreen.openFor(pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof RouterBlockEntity router) {
            ItemStack existing = router.getInventory().getStackInSlot(0);
            if (!existing.isEmpty()) {
                if (!player.getInventory().add(existing.copy())) {
                    player.drop(existing.copy(), false);
                }
                router.getInventory().setStackInSlot(0, ItemStack.EMPTY);
                player.displayClientMessage(Component.literal("Processador retirado. Roteador sem força agora."), true);
            } else {
                player.displayClientMessage(Component.literal("Roteador vazio - força 0, alcance 0."), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
