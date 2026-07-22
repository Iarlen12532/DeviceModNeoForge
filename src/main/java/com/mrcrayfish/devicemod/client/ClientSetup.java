package com.mrcrayfish.devicemod.client;

import com.mrcrayfish.devicemod.DeviceMod;
import com.mrcrayfish.devicemod.client.gui.LaptopScreen;
import com.mrcrayfish.devicemod.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = DeviceMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.LAPTOP.get(), LaptopScreen::new);
    }
}
