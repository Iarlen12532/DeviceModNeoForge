package com.tos.tosmod.block;

import com.tos.tosmod.block.entity.PrinterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class PrinterBlock extends BaseEntityBlock {

    public PrinterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends PrinterBlock> codec() {
        return simpleCodec(PrinterBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PrinterBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof PrinterBlockEntity printer) {
            if (printer.hasPendingPaper()) {
                ItemStack paper = printer.takePaper();
                if (!player.getInventory().add(paper)) {
                    player.drop(paper, false);
                }
            } else {
                player.displayClientMessage(Component.literal("Nenhuma folha pronta."), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
