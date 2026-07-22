package com.mrcrayfish.devicemod.block.entity;

import com.mrcrayfish.devicemod.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PrinterBlockEntity extends BlockEntity {

    private int paperCount = 0;
    private int inkLevel = 100;

    public PrinterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.PRINTER_BE.get(), pos, state);
    }

    public int getPaperCount() {
        return paperCount;
    }

    public void addPaper(int amount) {
        this.paperCount = Math.min(this.paperCount + amount, 64);
        setChanged();
    }

    public int getInkLevel() {
        return inkLevel;
    }

    public boolean consumeInk(int amount) {
        if (inkLevel >= amount) {
            inkLevel -= amount;
            setChanged();
            return true;
        }
        return false;
    }

    public boolean canPrint() {
        return paperCount > 0 && inkLevel > 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.paperCount = tag.getInt("PaperCount");
        this.inkLevel = tag.getInt("InkLevel");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("PaperCount", paperCount);
        tag.putInt("InkLevel", inkLevel);
    }
}
