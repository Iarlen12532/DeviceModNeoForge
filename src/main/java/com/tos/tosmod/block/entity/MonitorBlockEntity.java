package com.tos.tosmod.block.entity;

import com.tos.tosmod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Monitor externo (Fase 8) - pra cases sem tela integrada (torre, servidor).
 * Só guarda a posição do computador ligado por cabo de vídeo; clicar nele abre a
 * MESMA TerminalScreen do computador linkado (ver MonitorBlock/TerminalScreen).
 */
public class MonitorBlockEntity extends BlockEntity {

    private BlockPos linkedComputerPos = null;

    public MonitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MONITOR_BLOCK_ENTITY.get(), pos, state);
    }

    public void setLinkedComputerPos(BlockPos pos) {
        this.linkedComputerPos = pos;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public BlockPos getLinkedComputerPos() {
        return linkedComputerPos;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (linkedComputerPos != null) {
            tag.putLong("linked_computer_pos", linkedComputerPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        linkedComputerPos = tag.contains("linked_computer_pos") ? BlockPos.of(tag.getLong("linked_computer_pos")) : null;
    }

    // Sincroniza a posição linkada pro cliente - precisa saber onde abrir a TerminalScreen.
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
