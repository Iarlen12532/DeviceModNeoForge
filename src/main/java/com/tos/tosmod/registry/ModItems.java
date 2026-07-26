package com.tos.tosmod.registry;

import com.tos.tosmod.TOSMod;
import com.tos.tosmod.component.ComponentCategory;
import com.tos.tosmod.item.ComponentItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TOSMod.MOD_ID);

    // --- CPUs "Risen" (linha AMD-like: mais barata, mais quente) ---
    public static final DeferredHolder<Item, Item> CPU_RISEN_T1 = ITEMS.registerItem("cpu_risen_t1",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> CPU_RISEN_T2 = ITEMS.registerItem("cpu_risen_t2",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> CPU_RISEN_T3 = ITEMS.registerItem("cpu_risen_t3",
            props -> new ComponentItem(props));

    // --- CPUs "Xarm" (linha Apple Silicon-like: mais cara, mais eficiente/menos calor) ---
    public static final DeferredHolder<Item, Item> CPU_XARM_T1 = ITEMS.registerItem("cpu_xarm_t1",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> CPU_XARM_T2 = ITEMS.registerItem("cpu_xarm_t2",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> CPU_XARM_T3 = ITEMS.registerItem("cpu_xarm_t3",
            props -> new ComponentItem(props));

    // --- APUs (CPU com GPU integrada, ideais pra notebooks finos) ---
    public static final DeferredHolder<Item, Item> APU_RISEN_T1 = ITEMS.registerItem("apu_risen_t1",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> APU_XARM_T1 = ITEMS.registerItem("apu_xarm_t1",
            props -> new ComponentItem(props));

    // --- GPUs dedicadas ---
    public static final DeferredHolder<Item, Item> GPU_T1 = ITEMS.registerItem("gpu_t1",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> GPU_T2 = ITEMS.registerItem("gpu_t2",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> GPU_T3 = ITEMS.registerItem("gpu_t3",
            props -> new ComponentItem(props));

    // --- RAM ---
    public static final DeferredHolder<Item, Item> RAM_STICK_T1 = ITEMS.registerItem("ram_stick_t1",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> RAM_STICK_T2 = ITEMS.registerItem("ram_stick_t2",
            props -> new ComponentItem(props));

    // --- Armazenamento ---
    public static final DeferredHolder<Item, Item> STORAGE_HDD = ITEMS.registerItem("storage_hdd",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> STORAGE_SSD = ITEMS.registerItem("storage_ssd",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> STORAGE_NVME = ITEMS.registerItem("storage_nvme",
            props -> new ComponentItem(props));

    // --- Fonte de alimentação (PSU) ---
    public static final DeferredHolder<Item, Item> PSU_500W = ITEMS.registerItem("psu_500w",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> PSU_1000W = ITEMS.registerItem("psu_1000w",
            props -> new ComponentItem(props));

    // --- Bateria (notebooks) ---
    public static final DeferredHolder<Item, Item> BATTERY_BASIC = ITEMS.registerItem("battery_basic",
            props -> new ComponentItem(props));

    // --- Teclado e mouse (só cases fixas - notebook já vem com os dois embutidos) ---
    public static final DeferredHolder<Item, Item> KEYBOARD_BASIC = ITEMS.registerItem("keyboard_basic",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> MOUSE_BASIC = ITEMS.registerItem("mouse_basic",
            props -> new ComponentItem(props));

    // --- Processador de rede (vai dentro do roteador - quanto mais forte, mais dispositivos
    // e mais rápido pra todo mundo conectado) ---
    public static final DeferredHolder<Item, Item> NETWORK_PROCESSOR_T1 = ITEMS.registerItem("network_processor_t1",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> NETWORK_PROCESSOR_T2 = ITEMS.registerItem("network_processor_t2",
            props -> new ComponentItem(props));
    public static final DeferredHolder<Item, Item> NETWORK_PROCESSOR_T3 = ITEMS.registerItem("network_processor_t3",
            props -> new ComponentItem(props));

    // --- Pendrive instalador do TOS (Fase 6) ---
    public static final DeferredHolder<Item, Item> TOS_INSTALLER_USB = ITEMS.registerItem("tos_installer_usb",
            com.tos.tosmod.item.TosInstallerUsbItem::new);

    // --- Fase 7: cabo de rede, papel impresso, pen drive de dados ---
    public static final DeferredHolder<Item, Item> NETWORK_CABLE = ITEMS.registerItem("network_cable",
            com.tos.tosmod.item.NetworkCableItem::new);
    public static final DeferredHolder<Item, Item> PRINTED_PAPER = ITEMS.registerItem("printed_paper",
            com.tos.tosmod.item.PrintedPaperItem::new);
    public static final DeferredHolder<Item, Item> PEN_DRIVE = ITEMS.registerItem("pen_drive",
            com.tos.tosmod.item.PenDriveItem::new);

    // --- Fase 8: cabo de vídeo (monitor externo pra torre/servidor) e antena de redstone ---
    public static final DeferredHolder<Item, Item> VIDEO_CABLE = ITEMS.registerItem("video_cable",
            com.tos.tosmod.item.VideoCableItem::new);

    // --- BlockItems: sem isso nenhum bloco do mod pode ser pego/colocado pelo jogador.
    // (bug de todas as fases anteriores - corrigido aqui, junto com o bloco novo da Fase 8)
    // Tipo é DeferredItem<BlockItem>, não DeferredHolder<Item,Item> - registerSimpleBlockItem
    // devolve um tipo mais específico (BlockItem), e generics em Java são invariantes. ---
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> NOTEBOOK_GAMER_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.NOTEBOOK_GAMER_CASE);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> NOTEBOOK_THIN_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.NOTEBOOK_THIN_CASE);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> TOWER_DESKTOP_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.TOWER_DESKTOP_CASE);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> TOWER_DESKTOP_CASE_MACPRO_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.TOWER_DESKTOP_CASE_MACPRO);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> ALL_IN_ONE_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.ALL_IN_ONE_CASE);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> SERVER_RACK_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.SERVER_RACK_CASE);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> ROUTER_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.ROUTER);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> PRINTER_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.PRINTER);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> MONITOR_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.MONITOR);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> REDSTONE_LINK_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.REDSTONE_LINK);
    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.BlockItem> INDUSTRIAL_MONITOR_ITEM =
            ITEMS.registerSimpleBlockItem(com.tos.tosmod.registry.ModBlocks.INDUSTRIAL_MONITOR);
}
