package com.tos.tosmod.registry;

import com.tos.tosmod.TOSMod;
import com.tos.tosmod.menu.HardwareMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TOSMod.MOD_ID);

    // Usa IMenuTypeExtension pra poder mandar a posição do bloco (BlockPos) junto na
    // abertura do menu - o menu precisa saber QUAL CaseBlockEntity ele está representando.
    public static final DeferredHolder<MenuType<?>, MenuType<HardwareMenu>> HARDWARE_MENU =
            MENUS.register("hardware_menu", () -> IMenuTypeExtension.create(
                    (windowId, inventory, buffer) -> new HardwareMenu(windowId, inventory, buffer.readBlockPos())
            ));
}
