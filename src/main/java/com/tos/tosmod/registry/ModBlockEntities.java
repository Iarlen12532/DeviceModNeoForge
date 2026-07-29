package com.tos.tosmod.registry;

import com.tos.tosmod.TOSMod;
import com.tos.tosmod.block.entity.CaseBlockEntity;
import com.tos.tosmod.block.entity.RouterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, TOSMod.MOD_ID);

    // Um único BlockEntityType serve pra TODOS os modelos de case (ele lê a CaseDefinition
    // certa a partir do CaseBlock específico, dentro do próprio CaseBlockEntity).
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CaseBlockEntity>> CASE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("case_block_entity", () -> BlockEntityType.Builder.of(
                    CaseBlockEntity::new,
                    ModBlocks.NOTEBOOK_GAMER_CASE.get(),
                    ModBlocks.NOTEBOOK_THIN_CASE.get(),
                    ModBlocks.TOWER_DESKTOP_CASE.get(),
                    ModBlocks.TOWER_DESKTOP_CASE_MACPRO.get(),
                    ModBlocks.ALL_IN_ONE_CASE.get(),
                    ModBlocks.SERVER_RACK_CASE.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RouterBlockEntity>> ROUTER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("router_block_entity", () -> BlockEntityType.Builder.of(
                    RouterBlockEntity::new,
                    ModBlocks.ROUTER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.tos.tosmod.block.entity.PrinterBlockEntity>> PRINTER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("printer_block_entity", () -> BlockEntityType.Builder.of(
                    com.tos.tosmod.block.entity.PrinterBlockEntity::new,
                    ModBlocks.PRINTER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.tos.tosmod.block.entity.MonitorBlockEntity>> MONITOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("monitor_block_entity", () -> BlockEntityType.Builder.of(
                    com.tos.tosmod.block.entity.MonitorBlockEntity::new,
                    ModBlocks.MONITOR.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.tos.tosmod.block.entity.RedstoneLinkBlockEntity>> REDSTONE_LINK_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("redstone_link_block_entity", () -> BlockEntityType.Builder.of(
                    com.tos.tosmod.block.entity.RedstoneLinkBlockEntity::new,
                    ModBlocks.REDSTONE_LINK.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.tos.tosmod.block.entity.IndustrialMonitorBlockEntity>> INDUSTRIAL_MONITOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("industrial_monitor_block_entity", () -> BlockEntityType.Builder.of(
                    com.tos.tosmod.block.entity.IndustrialMonitorBlockEntity::new,
                    ModBlocks.INDUSTRIAL_MONITOR.get()
            ).build(null));
}
