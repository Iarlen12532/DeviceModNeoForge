package com.mrcrayfish.devicemod.registry;

import com.mrcrayfish.devicemod.DeviceMod;
import com.mrcrayfish.devicemod.block.LaptopBlock;
import com.mrcrayfish.devicemod.block.PrinterBlock;
import com.mrcrayfish.devicemod.block.RouterBlock;
import com.mrcrayfish.devicemod.block.entity.LaptopBlockEntity;
import com.mrcrayfish.devicemod.block.entity.PrinterBlockEntity;
import com.mrcrayfish.devicemod.block.entity.RouterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DeviceMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DeviceMod.MOD_ID);

    // Blocos
    public static final DeferredBlock<LaptopBlock> LAPTOP = BLOCKS.registerBlock("laptop",
            props -> new LaptopBlock(props.noOcclusion()),
            BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion());

    public static final DeferredBlock<PrinterBlock> PRINTER = BLOCKS.registerBlock("printer",
            props -> new PrinterBlock(props.noOcclusion()),
            BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion());

    public static final DeferredBlock<RouterBlock> ROUTER = BLOCKS.registerBlock("router",
            props -> new RouterBlock(props.noOcclusion()),
            BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion());

    // Block Entities - FORMA CORRETA para NeoForge 1.21.1
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaptopBlockEntity>> LAPTOP_BE =
            BLOCK_ENTITY_TYPES.register("laptop",
                    () -> BlockEntityType.Builder.of(LaptopBlockEntity::new, LAPTOP.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PrinterBlockEntity>> PRINTER_BE =
            BLOCK_ENTITY_TYPES.register("printer",
                    () -> BlockEntityType.Builder.of(PrinterBlockEntity::new, PRINTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RouterBlockEntity>> ROUTER_BE =
            BLOCK_ENTITY_TYPES.register("router",
                    () -> BlockEntityType.Builder.of(RouterBlockEntity::new, ROUTER.get()).build(null));
}
