package com.tos.tosmod.client.screen;

import com.tos.tosmod.menu.RouterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RouterScreenMenu extends AbstractContainerScreen<RouterMenu> {

    private static final int SLOT_BG = 0xFFC6C6C6;
    private static final int SLOT_BORDER_DARK = 0xFF373737;
    private static final int SLOT_BORDER_LIGHT = 0xFFFFFFFF;

    public RouterScreenMenu(RouterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 150;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        ScreenStyle.fillRounded(graphics, x, y, x + imageWidth, y + imageHeight, ScreenStyle.WINDOW_BG);
        for (Slot slot : menu.slots) {
            int slotX = x + slot.x - 1;
            int slotY = y + slot.y - 1;
            graphics.fill(slotX, slotY, slotX + 18, slotY + 18, SLOT_BORDER_DARK);
            graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, SLOT_BORDER_LIGHT);
            graphics.fill(slotX + 1, slotY + 1, slotX + 16, slotY + 16, SLOT_BG);
        }
        String status = "Força do roteador: " + menu.getRouter().getPower();
        graphics.drawString(font, status, x + 8, y + 54, 0x404040, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false);
    }
}
