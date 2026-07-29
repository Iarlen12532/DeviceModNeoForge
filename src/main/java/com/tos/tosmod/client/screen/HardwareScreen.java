package com.tos.tosmod.client.screen;

import com.tos.tosmod.component.SlotType;
import com.tos.tosmod.menu.HardwareMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Tela do menu de hardware (corrige um buraco real: não existia NENHUMA forma do
 * jogador inserir CPU/RAM/GPU/etc numa case antes disso). Visual simples (retângulos
 * foscos, sem textura de fundo customizada) - mesma linha das outras telas do mod, leve
 * pro ambiente Android/gl4es.
 */
@OnlyIn(Dist.CLIENT)
public class HardwareScreen extends AbstractContainerScreen<HardwareMenu> {

    public HardwareScreen(HardwareMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 18 + ((menu.getCaseEntity().getSlotLayout().size() + 8) / 9) * 18 + 96;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        ScreenStyle.fillRounded(graphics, x, y, x + imageWidth, y + imageHeight, ScreenStyle.WINDOW_BG);

        // Desenha um quadrado atrás de cada slot pra ficar visível sem precisar de textura -
        // cor muda um pouco conforme o tipo, só pra ajudar a diferenciar visualmente.
        for (Slot slot : menu.slots) {
            int slotX = x + slot.x - 1;
            int slotY = y + slot.y - 1;
            graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF8B8B8B);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        // Nome do tipo de slot embaixo do cursor, quando ele estiver sobre um slot de hardware vazio.
        var layout = menu.getCaseEntity().getSlotLayout();
        for (int i = 0; i < layout.size(); i++) {
            Slot slot = menu.slots.get(i);
            int slotX = leftPos + slot.x;
            int slotY = topPos + slot.y;
            if (!slot.hasItem() && mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                graphics.renderTooltip(font, Component.literal(slotName(layout.get(i))), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false);
    }

    private String slotName(SlotType type) {
        return switch (type) {
            case CPU_SOCKET -> "Socket de CPU/APU";
            case GPU_SLOT -> "Slot de GPU";
            case RAM_SLOT -> "Slot de RAM";
            case STORAGE_SLOT -> "Slot de Armazenamento";
            case PSU_SLOT -> "Slot de Fonte";
            case BATTERY_SLOT -> "Slot de Bateria";
            case KEYBOARD_SLOT -> "Slot de Teclado";
            case MOUSE_SLOT -> "Slot de Mouse";
            case NETWORK_PROCESSOR_SLOT -> "Slot de Processador de Rede";
        };
    }
}
