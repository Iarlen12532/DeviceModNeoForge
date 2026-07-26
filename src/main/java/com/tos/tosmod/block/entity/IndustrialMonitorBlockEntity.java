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
 * Monitor industrial (Fase 10) - inspirado no estilo OpenComputers/automação pura.
 * Igual ao monitor comum (Fase 8) - liga num computador via cabo de vídeo, tela
 * interativa completa - mas mais caro porque também FUNCIONA COMO teclado+mouse
 * embutidos pro computador linkado (ver CaseBlockEntity.hasKeyboard()/hasMouse()).
 * Isso resolve o caso do servidor (headless, sem slot de teclado/mouse próprio):
 * ligando um monitor industrial nele, dá pra digitar e clicar normalmente.
 */
public class IndustrialMonitorBlockEntity extends BlockEntity {

    private BlockPos linkedComputerPos = null;

    public IndustrialMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INDUSTRIAL_MONITOR_BLOCK_ENTITY.get(), pos, state);
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

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
