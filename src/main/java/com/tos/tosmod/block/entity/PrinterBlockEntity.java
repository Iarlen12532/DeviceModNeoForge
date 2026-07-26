package com.tos.tosmod.block.entity;

import com.tos.tosmod.registry.ModBlockEntities;
import com.tos.tosmod.registry.ModDataComponents;
import com.tos.tosmod.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockEntity da impressora (Fase 7). Só é "encontrada" por um computador via CABO -
 * a impressora não tem WiFi, então precisa estar fisicamente conectada num roteador
 * (mesmo roteador que o computador também alcança, por cabo ou sem fio).
 */
public class PrinterBlockEntity extends BlockEntity {

    private BlockPos cableLinkedRouterPos = null;
    private ItemStack pendingPaper = ItemStack.EMPTY;

    public PrinterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRINTER_BLOCK_ENTITY.get(), pos, state);
    }

    public void setCableLinkedRouterPos(BlockPos pos) {
        this.cableLinkedRouterPos = pos;
        setChanged();
    }

    public BlockPos getCableLinkedRouterPos() {
        return cableLinkedRouterPos;
    }

    /** Cria a folha impressa e a mantém pronta - o jogador retira clicando na impressora. */
    public void printText(String text) {
        ItemStack paper = new ItemStack(ModItems.PRINTED_PAPER.get());
        paper.set(ModDataComponents.PRINTED_TEXT, text);
        pendingPaper = paper;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    /** Chamado quando o jogador clica na impressora pra pegar a folha pronta. */
    public ItemStack takePaper() {
        ItemStack result = pendingPaper;
        pendingPaper = ItemStack.EMPTY;
        setChanged();
        return result;
    }

    public boolean hasPendingPaper() {
        return !pendingPaper.isEmpty();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (cableLinkedRouterPos != null) {
            tag.putLong("cable_router_pos", cableLinkedRouterPos.asLong());
        }
        if (!pendingPaper.isEmpty()) {
            tag.put("pending_paper", pendingPaper.save(registries, new CompoundTag()));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        cableLinkedRouterPos = tag.contains("cable_router_pos") ? BlockPos.of(tag.getLong("cable_router_pos")) : null;
        pendingPaper = tag.contains("pending_paper")
                ? ItemStack.parse(registries, tag.getCompound("pending_paper")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
    }
}
