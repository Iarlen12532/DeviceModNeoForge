package com.tos.tosmod.block;

import com.tos.tosmod.block.entity.RedstoneLinkBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bloco da antena de redstone (Fase 8). Funciona como fonte de sinal (isSignalSource)
 * - a força emitida vem do RedstoneLinkBlockEntity, controlada remotamente por um
 * computador via WIFI (ver CaseBlockEntity.redstoneApi / computer/RedstoneApi).
 */
public class RedstoneLinkBlock extends BaseEntityBlock {

    public RedstoneLinkBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends RedstoneLinkBlock> codec() {
        return simpleCodec(RedstoneLinkBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneLinkBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof RedstoneLinkBlockEntity link) {
                link.tick();
            }
        };
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof RedstoneLinkBlockEntity link) {
            return link.getOutputStrength();
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }
}
