package com.mrcrayfish.devicemod.registry;

import com.mrcrayfish.devicemod.DeviceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DeviceMod.MOD_ID);

    public static final Supplier<CreativeModeTab> DEVICE_TAB = CREATIVE_MODE_TABS.register("device_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.LAPTOP.get()))
                    .title(Component.translatable("itemGroup.devicemod"))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.LAPTOP.get());
                        output.accept(ModItems.PRINTER.get());
                        output.accept(ModItems.ROUTER.get());
                        output.accept(ModItems.FLASH_DRIVE.get());
                        output.accept(ModItems.CIRCUIT_BOARD.get());
                        output.accept(ModItems.PLASTIC.get());
                    })
                    .build());
}
