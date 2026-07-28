package com.tos.tosmod.registry;

import com.tos.tosmod.TOSMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Aba de criativo do mod - sem isso, nenhum item/bloco aparece no inventário criativo,
 * só dá pra pegar via /give (bug desde a Fase 1, corrigido aqui).
 */
public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TOSMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOS_TAB = CREATIVE_TABS.register("tosmod_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.tosmod"))
                    .icon(() -> ModItems.CPU_RISEN_T1.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Blocos
                        output.accept(ModBlocks.NOTEBOOK_GAMER_CASE.get());
                        output.accept(ModBlocks.NOTEBOOK_THIN_CASE.get());
                        output.accept(ModBlocks.TOWER_DESKTOP_CASE.get());
                        output.accept(ModBlocks.TOWER_DESKTOP_CASE_MACPRO.get());
                        output.accept(ModBlocks.ALL_IN_ONE_CASE.get());
                        output.accept(ModBlocks.SERVER_RACK_CASE.get());
                        output.accept(ModBlocks.ROUTER.get());
                        output.accept(ModBlocks.PRINTER.get());
                        output.accept(ModBlocks.MONITOR.get());
                        output.accept(ModBlocks.INDUSTRIAL_MONITOR.get());
                        output.accept(ModBlocks.REDSTONE_LINK.get());

                        // Componentes de hardware
                        output.accept(ModItems.CPU_RISEN_T1.get());
                        output.accept(ModItems.CPU_RISEN_T2.get());
                        output.accept(ModItems.CPU_RISEN_T3.get());
                        output.accept(ModItems.CPU_XARM_T1.get());
                        output.accept(ModItems.CPU_XARM_T2.get());
                        output.accept(ModItems.CPU_XARM_T3.get());
                        output.accept(ModItems.APU_RISEN_T1.get());
                        output.accept(ModItems.APU_XARM_T1.get());
                        output.accept(ModItems.GPU_T1.get());
                        output.accept(ModItems.GPU_T2.get());
                        output.accept(ModItems.GPU_T3.get());
                        output.accept(ModItems.RAM_STICK_T1.get());
                        output.accept(ModItems.RAM_STICK_T2.get());
                        output.accept(ModItems.STORAGE_HDD.get());
                        output.accept(ModItems.STORAGE_SSD.get());
                        output.accept(ModItems.STORAGE_NVME.get());
                        output.accept(ModItems.PSU_500W.get());
                        output.accept(ModItems.PSU_1000W.get());
                        output.accept(ModItems.BATTERY_BASIC.get());
                        output.accept(ModItems.KEYBOARD_BASIC.get());
                        output.accept(ModItems.MOUSE_BASIC.get());
                        output.accept(ModItems.NETWORK_PROCESSOR_T1.get());
                        output.accept(ModItems.NETWORK_PROCESSOR_T2.get());
                        output.accept(ModItems.NETWORK_PROCESSOR_T3.get());

                        // Outros itens
                        output.accept(ModItems.TOS_INSTALLER_USB.get());
                        output.accept(ModItems.NETWORK_CABLE.get());
                        output.accept(ModItems.PRINTED_PAPER.get());
                        output.accept(ModItems.PEN_DRIVE.get());
                        output.accept(ModItems.VIDEO_CABLE.get());
                    })
                    .build());
}
