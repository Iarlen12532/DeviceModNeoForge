package com.tos.tosmod.registry;

import com.tos.tosmod.TOSMod;
import com.tos.tosmod.component.ComponentCategory;
import com.tos.tosmod.component.ComponentStats;
import com.tos.tosmod.item.ComponentItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * IMPORTANTE: os itens de componente de hardware (CPU, GPU, RAM, etc) levam o
 * ComponentStats já embutido nas Properties do item, via `.component(...)`, em vez de
 * numa tabela separada (ComponentStatsDefaults, agora removida). Isso é essencial: um
 * Data Component só definido "depois" (via um método tipo createStack() que nada nesse
 * mod chamava de verdade) NUNCA aparece em itens pegos pelo criativo ou `/give` - por
 * isso NADA conseguia ser inserido em slot nenhum antes dessa correção (mayPlace()
 * sempre falhava, já que stack.get(COMPONENT_STATS) sempre vinha nulo). Colocando o
 * componente direto nas Properties, ele vira parte do item de verdade - qualquer
 * ItemStack dele, de qualquer jeito que apareça no jogo, já nasce com o dado certo.
 */
public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TOSMod.MOD_ID);

    private static Item.Properties withStats(ComponentStats stats) {
        return new Item.Properties().component(ModDataComponents.COMPONENT_STATS.get(), stats);
    }

    // --- CPUs "Risen" (linha AMD-like: mais barata, mais quente) ---
    public static final DeferredHolder<Item, Item> CPU_RISEN_T1 = ITEMS.registerItem("cpu_risen_t1",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.CPU, 1, 40, 65, 40, 0, 0, "risen"))));
    public static final DeferredHolder<Item, Item> CPU_RISEN_T2 = ITEMS.registerItem("cpu_risen_t2",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.CPU, 2, 65, 95, 65, 0, 0, "risen"))));
    public static final DeferredHolder<Item, Item> CPU_RISEN_T3 = ITEMS.registerItem("cpu_risen_t3",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.CPU, 3, 90, 130, 95, 0, 0, "risen"))));

    // --- CPUs "Xarm" (linha Apple Silicon-like: mais cara, mais eficiente/menos calor) ---
    public static final DeferredHolder<Item, Item> CPU_XARM_T1 = ITEMS.registerItem("cpu_xarm_t1",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.CPU, 1, 50, 30, 20, 0, 0, "xarm"))));
    public static final DeferredHolder<Item, Item> CPU_XARM_T2 = ITEMS.registerItem("cpu_xarm_t2",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.CPU, 2, 75, 45, 30, 0, 0, "xarm"))));
    public static final DeferredHolder<Item, Item> CPU_XARM_T3 = ITEMS.registerItem("cpu_xarm_t3",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.CPU, 3, 100, 60, 40, 0, 0, "xarm"))));

    // --- APUs (CPU com GPU integrada, ideais pra notebooks finos) ---
    public static final DeferredHolder<Item, Item> APU_RISEN_T1 = ITEMS.registerItem("apu_risen_t1",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.APU, 1, 35, 55, 45, 0, 0, "risen"))));
    public static final DeferredHolder<Item, Item> APU_XARM_T1 = ITEMS.registerItem("apu_xarm_t1",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.APU, 1, 45, 35, 25, 0, 0, "xarm"))));

    // --- GPUs dedicadas ---
    public static final DeferredHolder<Item, Item> GPU_T1 = ITEMS.registerItem("gpu_t1",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.GPU, 1, 50, 120, 70, 0, 0, ""))));
    public static final DeferredHolder<Item, Item> GPU_T2 = ITEMS.registerItem("gpu_t2",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.GPU, 2, 90, 220, 120, 0, 0, ""))));
    public static final DeferredHolder<Item, Item> GPU_T3 = ITEMS.registerItem("gpu_t3",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.GPU, 3, 140, 350, 180, 0, 0, ""))));

    // --- RAM ---
    public static final DeferredHolder<Item, Item> RAM_STICK_T1 = ITEMS.registerItem("ram_stick_t1",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.RAM, 1, 0, 5, 5, 8, 0, ""))));
    public static final DeferredHolder<Item, Item> RAM_STICK_T2 = ITEMS.registerItem("ram_stick_t2",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.RAM, 2, 0, 8, 8, 16, 0, ""))));

    // --- Armazenamento ---
    public static final DeferredHolder<Item, Item> STORAGE_HDD = ITEMS.registerItem("storage_hdd",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.STORAGE, 1, 15, 10, 15, 500, 0, ""))));
    public static final DeferredHolder<Item, Item> STORAGE_SSD = ITEMS.registerItem("storage_ssd",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.STORAGE, 2, 45, 8, 10, 500, 0, ""))));
    public static final DeferredHolder<Item, Item> STORAGE_NVME = ITEMS.registerItem("storage_nvme",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.STORAGE, 3, 90, 12, 8, 1000, 0, ""))));

    // --- Fonte de alimentação (PSU) ---
    public static final DeferredHolder<Item, Item> PSU_500W = ITEMS.registerItem("psu_500w",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.PSU, 1, 0, 0, 5, 0, 500, ""))));
    public static final DeferredHolder<Item, Item> PSU_1000W = ITEMS.registerItem("psu_1000w",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.PSU, 2, 0, 0, 8, 0, 1000, ""))));

    // --- Bateria (notebooks) ---
    public static final DeferredHolder<Item, Item> BATTERY_BASIC = ITEMS.registerItem("battery_basic",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.BATTERY, 1, 0, 0, 0, 200, 0, ""))));

    // --- Teclado e mouse (só cases fixas - notebook já vem com os dois embutidos) ---
    public static final DeferredHolder<Item, Item> KEYBOARD_BASIC = ITEMS.registerItem("keyboard_basic",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.KEYBOARD, 1, 0, 1, 0, 0, 0, ""))));
    public static final DeferredHolder<Item, Item> MOUSE_BASIC = ITEMS.registerItem("mouse_basic",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.MOUSE, 1, 0, 1, 0, 0, 0, ""))));

    // --- Processador de rede (vai dentro do roteador - quanto mais forte, mais dispositivos
    // e mais rápido pra todo mundo conectado) ---
    public static final DeferredHolder<Item, Item> NETWORK_PROCESSOR_T1 = ITEMS.registerItem("network_processor_t1",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.NETWORK_PROCESSOR, 1, 40, 15, 10, 0, 0, ""))));
    public static final DeferredHolder<Item, Item> NETWORK_PROCESSOR_T2 = ITEMS.registerItem("network_processor_t2",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.NETWORK_PROCESSOR, 2, 90, 25, 18, 0, 0, ""))));
    public static final DeferredHolder<Item, Item> NETWORK_PROCESSOR_T3 = ITEMS.registerItem("network_processor_t3",
            props -> new ComponentItem(withStats(new ComponentStats(ComponentCategory.NETWORK_PROCESSOR, 3, 160, 40, 28, 0, 0, ""))));

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
    // Tipo é DeferredItem<BlockItem>, não DeferredHolder<Item,Item> - registerSimpleBlockItem
    // devolve um tipo mais específico (BlockItem), e generics em Java são invariantes. ---
    public static final DeferredItem<BlockItem> NOTEBOOK_GAMER_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.NOTEBOOK_GAMER_CASE);
    public static final DeferredItem<BlockItem> NOTEBOOK_THIN_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.NOTEBOOK_THIN_CASE);
    public static final DeferredItem<BlockItem> TOWER_DESKTOP_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.TOWER_DESKTOP_CASE);
    public static final DeferredItem<BlockItem> TOWER_DESKTOP_CASE_MACPRO_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.TOWER_DESKTOP_CASE_MACPRO);
    public static final DeferredItem<BlockItem> ALL_IN_ONE_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.ALL_IN_ONE_CASE);
    public static final DeferredItem<BlockItem> SERVER_RACK_CASE_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.SERVER_RACK_CASE);
    public static final DeferredItem<BlockItem> ROUTER_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.ROUTER);
    public static final DeferredItem<BlockItem> PRINTER_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.PRINTER);
    public static final DeferredItem<BlockItem> MONITOR_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.MONITOR);
    public static final DeferredItem<BlockItem> REDSTONE_LINK_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.REDSTONE_LINK);
    public static final DeferredItem<BlockItem> INDUSTRIAL_MONITOR_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.INDUSTRIAL_MONITOR);
}
