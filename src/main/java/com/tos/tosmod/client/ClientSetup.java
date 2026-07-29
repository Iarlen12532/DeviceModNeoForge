package com.tos.tosmod.client;

import com.tos.tosmod.TOSMod;
import com.tos.tosmod.client.screen.HardwareScreen;
import com.tos.tosmod.client.screen.RouterScreenMenu;
import com.tos.tosmod.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Registra as telas (Screen) associadas a cada MenuType - só existe/roda no cliente. */
@EventBusSubscriber(modid = TOSMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.HARDWARE_MENU.get(), HardwareScreen::new);
        event.register(ModMenus.ROUTER_MENU.get(), RouterScreenMenu::new);
    }
}
