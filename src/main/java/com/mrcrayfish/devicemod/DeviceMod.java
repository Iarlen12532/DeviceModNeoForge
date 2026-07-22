package com.mrcrayfish.devicemod;

import com.mrcrayfish.devicemod.registry.ModBlocks;
import com.mrcrayfish.devicemod.registry.ModItems;
import com.mrcrayfish.devicemod.registry.ModMenus;
import com.mrcrayfish.devicemod.registry.ModCreativeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(DeviceMod.MOD_ID)
public class DeviceMod {
    public static final String MOD_ID = "devicemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public DeviceMod(IEventBus modEventBus) {
        LOGGER.info("Initializing MrCrayfish's Device Mod for NeoForge 1.21.1");

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
