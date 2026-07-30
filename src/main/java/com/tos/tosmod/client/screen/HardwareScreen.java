package com.tos.tosmod.client.screen;

import com.tos.tosmod.component.SlotType;
import com.tos.tosmod.menu.HardwareMenu;
import com.tos.tosmod.network.SetPowerSwitchPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Tela do menu de hardware. Visual simples (sem textura de fundo customizada), mas com
 * borda visível em cada slot - sem isso, slots adjacentes da mesma cor se misturam num
 * bloco só e ficam impossíveis de distinguir (bug reportado: "interface toda branca").
 * Também tem o botão de ligar/desligar - sem ele apertado, a case fica sempre desligada
 * mesmo com todo o hardware certo instalado.
 */
@OnlyIn(Dist.CLIENT)
public class HardwareScreen extends AbstractContainerScreen<HardwareMenu> {

    private static final int SLOT_BG = 0xFFC6C6C6;
    private static final int SLOT_BORDER_DARK = 0xFF373737;
    private static final int SLOT_BORDER_LIGHT = 0xFFFFFFFF;

    private Button powerButton;

    public HardwareScreen(HardwareMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        int hardwareRows = (menu.getCaseEntity().getSlotLayout().size() + 8) / 9;
        int invTop = 18 + hardwareRows * 18 + 26; // igual ao cálculo em HardwareMenu
        this.imageHeight = invTop + 82; // 3 linhas (54) + vão (4) + hotbar (18) + margem (6)
    }

    @Override
    protected void init() {
        super.init();
        int hardwareRows = (menu.getCaseEntity().getSlotLayout().size() + 8) / 9;
        int buttonY = topPos + 18 + hardwareRows * 18 + 2;
        powerButton = Button.builder(powerButtonLabel(), b -> togglePower())
                .bounds(leftPos + imageWidth - 76, buttonY, 68, 16)
                .build();
        addRenderableWidget(powerButton);
    }

    private Component powerButtonLabel() {
        return Component.literal(menu.getCaseEntity().isPowerSwitchOn() ? "Desligar" : "Ligar");
    }

    private void togglePower() {
        boolean newState = !menu.getCaseEntity().isPowerSwitchOn();
        PacketDistributor.sendToServer(new SetPowerSwitchPayload(menu.getCaseEntity().getBlockPos(), newState));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        ScreenStyle.fillRounded(graphics, x, y, x + imageWidth, y + imageHeight, ScreenStyle.WINDOW_BG);

        // Cada slot ganha um "afundado" com borda escura em cima/esquerda e clara embaixo/
        // direita (efeito bevel simples) - sem isso, slots vizinhos da mesma cor se fundem
        // visualmente num bloco só, ilegível.
        for (Slot slot : menu.slots) {
            int slotX = x + slot.x - 1;
            int slotY = y + slot.y - 1;
            graphics.fill(slotX, slotY, slotX + 18, slotY + 18, SLOT_BORDER_DARK);
            graphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, SLOT_BORDER_LIGHT);
            graphics.fill(slotX + 1, slotY + 1, slotX + 16, slotY + 16, SLOT_BG);
        }

        // Status ao vivo da máquina, embaixo dos slots de hardware.
        String status = menu.getCaseEntity().getPowerState().getDescription();
        int statusY = y + 18 + ((menu.getCaseEntity().getSlotLayout().size() + 8) / 9) * 18 + 4;
        graphics.drawString(font, status, x + 8, statusY, 0x404040, false);

        if (powerButton != null) {
            powerButton.setMessage(powerButtonLabel());
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
