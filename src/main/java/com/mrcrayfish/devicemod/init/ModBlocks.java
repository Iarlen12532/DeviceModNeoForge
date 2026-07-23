package com.mrcrayfish.devicemod.init;

import com.mrcrayfish.devicemod.DeviceMod;
import com.mrcrayfish.devicemod.block.*;
import com.mrcrayfish.devicemod.block.entity.*;
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
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DeviceMod.MOD_ID);

    public static final DeferredBlock<LaptopBlock> LAPTOP = BLOCKS.registerBlock("laptop",
            props -> new LaptopBlock(props.noOcclusion()),
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<PrinterBlock> PRINTER = BLOCKS.registerBlock("printer",
            props -> new PrinterBlock(props.noOcclusion()),
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<RouterBlock> ROUTER = BLOCKS.registerBlock("router",
            props -> new RouterBlock(props.noOcclusion()),
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<OfficeChairBlock> OFFICE_CHAIR = BLOCKS.registerBlock("office_chair",
            props -> new OfficeChairBlock(props.noOcclusion()),
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<PaperBlock> PAPER = BLOCKS.registerBlock("paper",
            props -> new PaperBlock(props.noOcclusion()),
            BlockBehaviour.Properties.of().strength(0.1f).sound(SoundType.WOOL).noOcclusion());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaptopBlockEntity>> LAPTOP_BE =
            BLOCK_ENTITIES.register("laptop", () -> BlockEntityType.Builder.of(LaptopBlockEntity::new, LAPTOP.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PrinterBlockEntity>> PRINTER_BE =
            BLOCK_ENTITIES.register("printer", () -> BlockEntityType.Builder.of(PrinterBlockEntity::new, PRINTER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RouterBlockEntity>> ROUTER_BE =
            BLOCK_ENTITIES.register("router", () -> BlockEntityType.Builder.of(RouterBlockEntity::new, ROUTER.get()).build(null));
}
