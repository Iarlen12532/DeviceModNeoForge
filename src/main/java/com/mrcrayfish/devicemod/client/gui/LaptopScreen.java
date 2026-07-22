package com.mrcrayfish.devicemod.client.gui;

import com.mrcrayfish.devicemod.DeviceMod;
import com.mrcrayfish.devicemod.menu.LaptopMenu;
import com.mrcrayfish.devicemod.block.entity.LaptopBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LaptopScreen extends AbstractContainerScreen<LaptopMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(DeviceMod.MOD_ID, "textures/gui/laptop.png");

    private static final int SCREEN_WIDTH = 480;
    private static final int SCREEN_HEIGHT = 256;

    public LaptopScreen(LaptopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = SCREEN_WIDTH;
        this.imageHeight = SCREEN_HEIGHT;
        this.titleLabelY = 10;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        renderDesktop(graphics);
    }

    private void renderDesktop(GuiGraphics graphics) {
        LaptopBlockEntity laptop = menu.getBlockEntity();
        if (laptop == null) return;

        int color = laptop.getColor();
        int desktopX = this.leftPos + 20;
        int desktopY = this.topPos + 30;
        int desktopWidth = this.imageWidth - 40;
        int desktopHeight = this.imageHeight - 60;

        graphics.fill(desktopX, desktopY, desktopX + desktopWidth, desktopY + desktopHeight, 0xFF000000 | (color & 0xFFFFFF));
        graphics.fill(desktopX, this.topPos + this.imageHeight - 30, desktopX + desktopWidth, this.topPos + this.imageHeight - 10, 0xFF333333);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
