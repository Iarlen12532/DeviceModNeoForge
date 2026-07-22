package com.mrcrayfish.devicemod.registry;

import com.mrcrayfish.devicemod.DeviceMod;
import com.mrcrayfish.devicemod.item.FlashDriveItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DeviceMod.MOD_ID);

    // Block Items
    public static final DeferredItem<BlockItem> LAPTOP = ITEMS.registerSimpleBlockItem("laptop", ModBlocks.LAPTOP);
    public static final DeferredItem<BlockItem> PRINTER = ITEMS.registerSimpleBlockItem("printer", ModBlocks.PRINTER);
    public static final DeferredItem<BlockItem> ROUTER = ITEMS.registerSimpleBlockItem("router", ModBlocks.ROUTER);

    // Items
    public static final DeferredItem<FlashDriveItem> FLASH_DRIVE = ITEMS.registerItem("flash_drive",
            props -> new FlashDriveItem(props.stacksTo(1)),
            new Item.Properties());

    public static final DeferredItem<Item> CIRCUIT_BOARD = ITEMS.registerSimpleItem("circuit_board",
            new Item.Properties());

    public static final DeferredItem<Item> PLASTIC = ITEMS.registerSimpleItem("plastic",
            new Item.Properties());
}
