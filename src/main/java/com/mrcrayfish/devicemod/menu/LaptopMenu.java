package com.mrcrayfish.devicemod.menu;

import com.mrcrayfish.devicemod.block.entity.LaptopBlockEntity;
import com.mrcrayfish.devicemod.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LaptopMenu extends AbstractContainerMenu {
    private final LaptopBlockEntity blockEntity;
    private final BlockPos pos;

    // Client-side constructor (called from IMenuTypeExtension)
    public LaptopMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, BlockPos.ZERO, null);
    }

    // Server-side constructor
    public LaptopMenu(int containerId, Inventory playerInventory, BlockPos pos, LaptopBlockEntity blockEntity) {
        super(ModMenus.LAPTOP.get(), containerId);
        this.blockEntity = blockEntity;
        this.pos = pos;
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
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return ContainerLevelAccess.create(player.level(), pos)
                .evaluate((level, p) -> level.getBlockEntity(p) instanceof LaptopBlockEntity 
                    && player.distanceToSqr(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) <= 64.0, true);
        }
        return ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos())
            .evaluate((level, p) -> level.getBlockEntity(p) == blockEntity 
                && player.distanceToSqr(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) <= 64.0, true);
    }
}
