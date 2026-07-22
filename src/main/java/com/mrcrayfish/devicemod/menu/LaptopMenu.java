package com.mrcrayfish.devicemod.menu;

import com.mrcrayfish.devicemod.block.entity.LaptopBlockEntity;
import com.mrcrayfish.devicemod.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

public class LaptopMenu extends AbstractContainerMenu {
    private final LaptopBlockEntity blockEntity;
    private final BlockPos pos;

    // Client-side constructor
    public LaptopMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null);
    }

    // Server-side constructor
    public LaptopMenu(int containerId, Inventory playerInventory, LaptopBlockEntity blockEntity) {
        super(ModMenus.LAPTOP.get(), containerId);
        this.blockEntity = blockEntity;
        this.pos = blockEntity != null ? blockEntity.getBlockPos() : BlockPos.ZERO;
    }

    public LaptopBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null || blockEntity.getLevel() == null) return false;
        return ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos())
            .evaluate((level, pos) -> level.getBlockEntity(pos) == blockEntity 
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0, true);
    }
}
