package com.tos.tosmod.registry;

import com.tos.tosmod.TOSMod;
import com.tos.tosmod.block.CaseBlock;
import com.tos.tosmod.block.RouterBlock;
import com.tos.tosmod.component.CaseDefinitions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TOSMod.MOD_ID);

    // Cada linha aqui = um tipo de case jogável. Quando você tiver o modelo 3D de um novo
    // formato (ex: outro estilo de torre), é só adicionar uma linha nova apontando pra
    // uma CaseDefinition (existente ou nova) - CaseBlock e CaseBlockEntity não mudam.

    public static final DeferredBlock<Block> NOTEBOOK_GAMER_CASE = BLOCKS.register("notebook_gamer_case",
            () -> new CaseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0f)
                    .sound(SoundType.METAL),
                    CaseDefinitions.NOTEBOOK_GAMER));

    public static final DeferredBlock<Block> NOTEBOOK_THIN_CASE = BLOCKS.register("notebook_thin_case",
            () -> new CaseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(1.5f)
                    .sound(SoundType.METAL),
                    CaseDefinitions.NOTEBOOK_THIN));

    public static final DeferredBlock<Block> TOWER_DESKTOP_CASE = BLOCKS.register("tower_desktop_case",
            () -> new CaseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0f)
                    .sound(SoundType.METAL),
                    CaseDefinitions.TOWER_DESKTOP));

    // Mac Pro agora tem a própria CaseDefinition (TOWER_DESKTOP_PRO) - antes usava a mesma
    // da torre comum (TOWER_DESKTOP), por isso tinha o mesmo resfriamento fraco e
    // superaquecia igual a qualquer outra case só de ligar.
    public static final DeferredBlock<Block> TOWER_DESKTOP_CASE_MACPRO = BLOCKS.register("tower_desktop_case_macpro",
            () -> new CaseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0f)
                    .sound(SoundType.METAL),
                    CaseDefinitions.TOWER_DESKTOP_PRO));

    public static final DeferredBlock<Block> ALL_IN_ONE_CASE = BLOCKS.register("all_in_one_case",
            () -> new CaseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.5f)
                    .sound(SoundType.METAL),
                    CaseDefinitions.ALL_IN_ONE));

    public static final DeferredBlock<Block> SERVER_RACK_CASE = BLOCKS.register("server_rack_case",
            () -> new CaseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0f)
                    .sound(SoundType.METAL),
                    CaseDefinitions.SERVER_RACK));

    public static final DeferredBlock<Block> ROUTER = BLOCKS.register("router",
            () -> new RouterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(1.5f)
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> PRINTER = BLOCKS.register("printer",
            () -> new com.tos.tosmod.block.PrinterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(1.5f)
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> MONITOR = BLOCKS.register("monitor",
            () -> new com.tos.tosmod.block.MonitorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(1.5f)
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> REDSTONE_LINK = BLOCKS.register("redstone_link",
            () -> new com.tos.tosmod.block.RedstoneLinkBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(1.0f)
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> INDUSTRIAL_MONITOR = BLOCKS.register("industrial_monitor",
            () -> new com.tos.tosmod.block.IndustrialMonitorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.5f)
                    .sound(SoundType.METAL)));
}
