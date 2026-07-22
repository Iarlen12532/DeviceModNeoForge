package com.mrcrayfish.devicemod.block.entity;

import com.mrcrayfish.devicemod.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RouterBlockEntity extends BlockEntity {

    private String networkName = "HomeNetwork";
    private String password = "";
    private Set<UUID> connectedDevices = new HashSet<>();
    private int range = 32;

    public RouterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ROUTER_BE.get(), pos, state);
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String name) {
        this.networkName = name;
        setChanged();
    }

    public boolean connectDevice(UUID deviceId, String passwordAttempt) {
        if (!this.password.isEmpty() && !this.password.equals(passwordAttempt)) {
            return false;
        }
        connectedDevices.add(deviceId);
        setChanged();
        return true;
    }

    public void disconnectDevice(UUID deviceId) {
        connectedDevices.remove(deviceId);
        setChanged();
    }

    public Set<UUID> getConnectedDevices() {
        return new HashSet<>(connectedDevices);
    }

    public int getRange() {
        return range;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.networkName = tag.getString("NetworkName");
        this.password = tag.getString("Password");
        this.range = tag.getInt("Range");
        // Connected devices
        connectedDevices.clear();
        if (tag.contains("ConnectedDevices")) {
            CompoundTag devicesTag = tag.getCompound("ConnectedDevices");
            for (String key : devicesTag.getAllKeys()) {
                connectedDevices.add(UUID.fromString(devicesTag.getString(key)));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("NetworkName", networkName);
        tag.putString("Password", password);
        tag.putInt("Range", range);
        // Connected devices
        CompoundTag devicesTag = new CompoundTag();
        int i = 0;
        for (UUID id : connectedDevices) {
            devicesTag.putString(String.valueOf(i++), id.toString());
        }
        tag.put("ConnectedDevices", devicesTag);
    }
}
