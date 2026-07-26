package com.tos.tosmod.client.screen;

import com.tos.tosmod.block.entity.RouterBlockEntity;
import com.tos.tosmod.network.SetRouterConfigPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Tela mínima (Fase 7) pra dar nome e senha a um roteador - aberta agachando + clicando
 * nele. Visual simples, sem blur/shader, igual a TerminalScreen (mesma cautela do
 * ambiente Android/Mali/gl4es).
 */
@OnlyIn(Dist.CLIENT)
public class RouterScreen extends Screen {

    private final BlockPos pos;
    private EditBox nameBox;
    private EditBox passwordBox;

    public RouterScreen(BlockPos pos) {
        super(Component.literal("Configurar Roteador"));
        this.pos = pos;
    }

    public static void openFor(BlockPos pos) {
        Minecraft.getInstance().setScreen(new RouterScreen(pos));
    }

    private RouterBlockEntity getRouterEntity() {
        if (minecraft == null || minecraft.level == null) {
            return null;
        }
        if (minecraft.level.getBlockEntity(pos) instanceof RouterBlockEntity router) {
            return router;
        }
        return null;
    }

    @Override
    protected void init() {
        int boxWidth = 200;
        int centerX = width / 2;
        RouterBlockEntity router = getRouterEntity();

        nameBox = new EditBox(font, centerX - boxWidth / 2, height / 2 - 30, boxWidth, 18, Component.literal("nome"));
        nameBox.setMaxLength(32);
        nameBox.setValue(router != null ? router.getRouterName() : "");
        addRenderableWidget(nameBox);

        passwordBox = new EditBox(font, centerX - boxWidth / 2, height / 2, boxWidth, 18, Component.literal("senha"));
        passwordBox.setMaxLength(32);
        passwordBox.setValue(router != null ? router.getPassword() : "");
        passwordBox.setHint(Component.literal("vazio = sem senha"));
        addRenderableWidget(passwordBox);

        addRenderableWidget(Button.builder(Component.literal("Salvar"), button -> save())
                .bounds(centerX - boxWidth / 2, height / 2 + 30, boxWidth, 20)
                .build());

        setInitialFocus(nameBox);
    }

    private void save() {
        PacketDistributor.sendToServer(new SetRouterConfigPayload(pos, nameBox.getValue(), passwordBox.getValue()));
        onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xC0101010);
        graphics.drawCenteredString(font, "Nome do roteador", width / 2, height / 2 - 42, 0xFFFFFF);
        graphics.drawCenteredString(font, "Senha (sem fio - cabo não precisa)", width / 2, height / 2 - 12, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
