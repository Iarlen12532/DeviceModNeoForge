package com.mrcrayfish.devicemod.item;

import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;

public class FlashDriveItem extends Item {
    public static final int STORAGE_CAPACITY = 64;

    public FlashDriveItem(Properties properties) {
        super(properties);
    }

    public int getStorageCapacity() {
        return STORAGE_CAPACITY;
    }
}
