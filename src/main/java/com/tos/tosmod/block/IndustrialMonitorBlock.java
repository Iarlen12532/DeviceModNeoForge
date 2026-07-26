package com.tos.tosmod.block;

import com.tos.tosmod.block.entity.IndustrialMonitorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class IndustrialMonitorBlock extends BaseEntityBlock {

    public IndustrialMonitorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialMonitorBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof IndustrialMonitorBlockEntity monitor) || monitor.getLinkedComputerPos() == null) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.literal("Monitor industrial sem cabo de vídeo conectado."), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (level.isClientSide()) {
            com.tos.tosmod.client.screen.TosScreens.open(monitor.getLinkedComputerPos());
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
