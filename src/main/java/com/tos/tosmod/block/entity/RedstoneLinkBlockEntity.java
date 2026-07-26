package com.tos.tosmod.block.entity;

import com.tos.tosmod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Antena de redstone (Fase 8) - conecta ao sistema via WIFI (diferente da impressora,
 * que exige cabo). Qualquer computador que alcance o mesmo roteador (sem fio) consegue
 * ler o sinal que chega nela e mandar sinal pra ela (pulso ou contínuo, força 0-15).
 */
public class RedstoneLinkBlockEntity extends BlockEntity {

    private int outputStrength = 0;
    private int pulseTicksRemaining = 0;

    public RedstoneLinkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REDSTONE_LINK_BLOCK_ENTITY.get(), pos, state);
    }

    public int getOutputStrength() {
        return outputStrength;
    }

    /** Sinal contínuo - fica nesse valor até alguém mandar outro (ou um pulso zerar). */
    public void setOutputStrength(int strength) {
        int clamped = Math.max(0, Math.min(15, strength));
        if (clamped == outputStrength) {
            return;
        }
        outputStrength = clamped;
        pulseTicksRemaining = 0; // um comando contínuo cancela qualquer pulso em andamento
        notifyNeighbors();
    }

    /** Pulso: sobe a força por N ticks, depois volta pra 0 sozinho. */
    public void pulse(int strength, int ticks) {
        outputStrength = Math.max(0, Math.min(15, strength));
        pulseTicksRemaining = Math.max(1, ticks);
        notifyNeighbors();
    }

    /** Sinal que ESTA antena está recebendo de blocos de redstone vizinhos no mundo. */
    public int getIncomingSignal() {
        return level != null ? level.getBestNeighborSignal(getBlockPos()) : 0;
    }

    public void tick() {
        if (pulseTicksRemaining > 0) {
            pulseTicksRemaining--;
            if (pulseTicksRemaining == 0) {
                outputStrength = 0;
                notifyNeighbors();
            }
        }
    }

    private void notifyNeighbors() {
        setChanged();
        if (level instanceof ServerLevel) {
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("output_strength", outputStrength);
        tag.putInt("pulse_ticks_remaining", pulseTicksRemaining);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        outputStrength = tag.getInt("output_strength");
        pulseTicksRemaining = tag.getInt("pulse_ticks_remaining");
    }
}
