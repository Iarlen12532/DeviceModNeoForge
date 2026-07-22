package com.mrcrayfish.devicemod.block.entity;

import com.mrcrayfish.devicemod.DeviceMod;
import com.mrcrayfish.devicemod.menu.LaptopMenu;
import com.mrcrayfish.devicemod.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LaptopBlockEntity extends BlockEntity implements MenuProvider {

    private String deviceName = "Laptop";
    private int color = 0x3366CC; // Azul padrão
    private boolean installed = false;

    public LaptopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.LAPTOP_BE.get(), pos, state);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
        setChanged();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("DeviceName")) {
            this.deviceName = tag.getString("DeviceName");
        }
        if (tag.contains("Color")) {
            this.color = tag.getInt("Color");
        }
        if (tag.contains("Installed")) {
            this.installed = tag.getBoolean("Installed");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("DeviceName", deviceName);
        tag.putInt("Color", color);
        tag.putBoolean("Installed", installed);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(deviceName);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new LaptopMenu(containerId, playerInventory, this);
    }
}
