package com.tos.tosmod.menu;

import com.tos.tosmod.block.entity.RouterBlockEntity;
import com.tos.tosmod.component.ComponentCategory;
import com.tos.tosmod.component.ComponentStats;
import com.tos.tosmod.registry.ModDataComponents;
import com.tos.tosmod.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

/** Menu do roteador - só 1 slot (processador de rede). Mesmo padrão do HardwareMenu. */
public class RouterMenu extends AbstractContainerMenu {

    private final RouterBlockEntity router;

    public RouterMenu(int windowId, Inventory playerInventory, BlockPos pos) {
        super(ModMenus.ROUTER_MENU.get(), windowId);
        this.router = (RouterBlockEntity) playerInventory.player.level().getBlockEntity(pos);

        addSlot(new SlotItemHandler(router.getInventory(), 0, 80, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                ComponentStats stats = stack.get(ModDataComponents.COMPONENT_STATS);
                return stats != null && stats.category() == ComponentCategory.NETWORK_PROCESSOR;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 65 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 123));
        }
    }

    public RouterBlockEntity getRouter() {
        return router;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index == 0) {
            if (!moveItemStackTo(original, 1, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(original, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (original.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return router != null && !router.isRemoved();
    }
}
