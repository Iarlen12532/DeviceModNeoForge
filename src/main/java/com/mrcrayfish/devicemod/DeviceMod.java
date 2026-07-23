package com.mrcrayfish.devicemod;

import com.mrcrayfish.devicemod.init.ModBlocks;
import com.mrcrayfish.devicemod.init.ModItems;
import com.mrcrayfish.devicemod.init.ModCreativeTabs;
import com.mrcrayfish.devicemod.wifi.WifiNetworkManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(DeviceMod.MOD_ID)
public class DeviceMod {
    public static final String MOD_ID = "devicemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final WifiNetworkManager WIFI_MANAGER = new WifiNetworkManager();

    public DeviceMod(IEventBus modEventBus) {
        LOGGER.info("Initializing MrCrayfish's Device Mod for NeoForge 1.21.1");
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItems.COLORED_ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Device Mod setup complete!");
    }
}
