package com.mrcrayfish.devicemod.init;

import com.mrcrayfish.devicemod.DeviceMod;
import com.mrcrayfish.devicemod.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DeviceMod.MOD_ID);
    public static final DeferredRegister.Items COLORED_ITEMS = DeferredRegister.createItems(DeviceMod.MOD_ID);

    public static final DeferredItem<BlockItem> LAPTOP = ITEMS.registerSimpleBlockItem("laptop", ModBlocks.LAPTOP);
    public static final DeferredItem<BlockItem> PRINTER = ITEMS.registerSimpleBlockItem("printer", ModBlocks.PRINTER);
    public static final DeferredItem<BlockItem> ROUTER = ITEMS.registerSimpleBlockItem("router", ModBlocks.ROUTER);
    public static final DeferredItem<BlockItem> OFFICE_CHAIR = ITEMS.registerSimpleBlockItem("office_chair", ModBlocks.OFFICE_CHAIR);
    public static final DeferredItem<BlockItem> PAPER = ITEMS.registerSimpleBlockItem("paper", ModBlocks.PAPER);

    public static final DeferredItem<Item> MOTHERBOARD = ITEMS.registerSimpleItem("motherboard", new Item.Properties());
    public static final DeferredItem<Item> CPU = ITEMS.registerSimpleItem("cpu", new Item.Properties());
    public static final DeferredItem<Item> GPU = ITEMS.registerSimpleItem("gpu", new Item.Properties());
    public static final DeferredItem<Item> RAM = ITEMS.registerSimpleItem("ram", new Item.Properties());
    public static final DeferredItem<Item> HARD_DRIVE = ITEMS.registerSimpleItem("hard_drive", new Item.Properties());
    public static final DeferredItem<Item> SOLID_STATE_DRIVE = ITEMS.registerSimpleItem("solid_state_drive", new Item.Properties());
    public static final DeferredItem<Item> SCREEN = ITEMS.registerSimpleItem("screen", new Item.Properties());
    public static final DeferredItem<Item> BATTERY = ITEMS.registerSimpleItem("battery", new Item.Properties());
    public static final DeferredItem<Item> WIFI_MODULE = ITEMS.registerSimpleItem("wifi", new Item.Properties());
    public static final DeferredItem<Item> CONTROLLER_UNIT = ITEMS.registerSimpleItem("controller_unit", new Item.Properties());
    public static final DeferredItem<Item> SMALL_ELECTRIC_MOTOR = ITEMS.registerSimpleItem("small_electric_motor", new Item.Properties());
    public static final DeferredItem<Item> CARRIAGE = ITEMS.registerSimpleItem("carriage", new Item.Properties());
    public static final DeferredItem<Item> WHEEL = ITEMS.registerSimpleItem("wheel", new Item.Properties());
    public static final DeferredItem<Item> FLASH_CHIP = ITEMS.registerSimpleItem("flash_chip", new Item.Properties());

    public static final DeferredItem<Item> CIRCUIT_BOARD = ITEMS.registerSimpleItem("circuit_board", new Item.Properties());
    public static final DeferredItem<Item> PLASTIC = ITEMS.registerSimpleItem("plastic", new Item.Properties());
    public static final DeferredItem<Item> PLASTIC_UNREFINED = ITEMS.registerSimpleItem("plastic_unrefined", new Item.Properties());
    public static final DeferredItem<Item> PLASTIC_FRAME = ITEMS.registerSimpleItem("plastic_frame", new Item.Properties());
    public static final DeferredItem<Item> INK_CARTRIDGE = ITEMS.registerSimpleItem("ink_cartridge", new Item.Properties());
    public static final DeferredItem<Item> PRINTED_PAPER = ITEMS.registerSimpleItem("printed_paper", new Item.Properties());

    public static final DeferredItem<FlashDriveItem> FLASH_DRIVE = ITEMS.registerItem("flash_drive",
            props -> new FlashDriveItem(props.stacksTo(1)), new Item.Properties());
    public static final DeferredItem<EthernetCableItem> ETHERNET_CABLE = ITEMS.registerItem("ethernet_cable",
            props -> new EthernetCableItem(props.stacksTo(64)), new Item.Properties());
}
