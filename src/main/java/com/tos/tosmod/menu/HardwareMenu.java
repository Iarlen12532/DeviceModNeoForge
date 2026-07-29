package com.tos.tosmod.menu;

import com.tos.tosmod.block.entity.CaseBlockEntity;
import com.tos.tosmod.component.ComponentStats;
import com.tos.tosmod.component.SlotType;
import com.tos.tosmod.registry.ModDataComponents;
import com.tos.tosmod.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.List;

/**
 * Menu (container) de hardware de uma case - um slot por posição em CaseDefinition,
 * cada um só aceitando o tipo certo de componente (CPU só no socket de CPU, etc).
 * Como é um AbstractContainerMenu de verdade, o Minecraft já sincroniza os slots entre
 * cliente e servidor sozinho - não precisou criar nenhum pacote de rede novo pra isso.
 */
public class HardwareMenu extends AbstractContainerMenu {

    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS = 9;

    private final CaseBlockEntity caseEntity;
    private final int hardwareSlotCount;

    public HardwareMenu(int windowId, Inventory playerInventory, BlockPos pos) {
        super(ModMenus.HARDWARE_MENU.get(), windowId);
        this.caseEntity = (CaseBlockEntity) playerInventory.player.level().getBlockEntity(pos);
        List<SlotType> layout = caseEntity.getSlotLayout();
        this.hardwareSlotCount = layout.size();

        for (int i = 0; i < layout.size(); i++) {
            SlotType type = layout.get(i);
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            addSlot(new SlotItemHandler(caseEntity.getInventory(), i, 8 + col * SLOT_SIZE, 18 + row * SLOT_SIZE) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    ComponentStats stats = stack.get(ModDataComponents.COMPONENT_STATS);
                    return stats != null && stats.category().getSlotType() == type;
                }
            });
        }

        int hardwareRows = (layout.size() + COLUMNS - 1) / COLUMNS;
        int invTop = 18 + hardwareRows * SLOT_SIZE + 26; // +26 = espaço pra linha de status

        // Inventário do jogador (3 linhas + hotbar), padrão vanilla.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * SLOT_SIZE, invTop + row * SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * SLOT_SIZE, invTop + 58));
        }
    }

    public CaseBlockEntity getCaseEntity() {
        return caseEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index < hardwareSlotCount) {
            // Shift-click de dentro da case pra fora - vai pro inventário do jogador.
            if (!moveItemStackTo(original, hardwareSlotCount, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Shift-click do inventário do jogador - tenta achar um slot de hardware compatível.
            if (!moveItemStackTo(original, 0, hardwareSlotCount, false)) {
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
        return caseEntity != null && !caseEntity.isRemoved();
    }
}
