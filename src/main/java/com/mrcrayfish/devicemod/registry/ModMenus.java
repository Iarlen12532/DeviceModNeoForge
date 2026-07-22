package com.mrcrayfish.devicemod.registry;

import com.mrcrayfish.devicemod.DeviceMod;
import com.mrcrayfish.devicemod.menu.LaptopMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, DeviceMod.MOD_ID);

    public static final Supplier<MenuType<LaptopMenu>> LAPTOP = MENUS.register("laptop",
            () -> IMenuTypeExtension.create(LaptopMenu::new));
}
