package com.tos.tosmod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tos.tosmod.block.entity.CaseBlockEntity;
import com.tos.tosmod.network.RunLuaCommandPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Fase 4: interface gráfica MÍNIMA do TOS - por enquanto só um terminal cru (texto puro,
 * sem janelas/dock/ícones ainda - isso é Fase 9, quando os modelos/texturas entrarem).
 *
 * Desenho propositalmente simples (retângulos sólidos, sem blur/sombra real) - o ambiente
 * alvo é Android via PojavLauncher/Mojo com gl4es, então nada de shader customizado aqui.
 */
@OnlyIn(Dist.CLIENT)
public class TerminalScreen extends Screen {

    private final BlockPos pos;
    private EditBox inputBox;
    private List<String> cachedHistory = new ArrayList<>();

    public TerminalScreen(BlockPos pos) {
        super(Component.literal("Terminal"));
        this.pos = pos;
    }

    /** Ponto de entrada usado pelo CaseBlock ao ser clicado - mantém a classe Minecraft/Screen
     *  isolada aqui dentro, sem vazar referência de classe cliente pro CaseBlock em si. */
    public static void openFor(BlockPos pos) {
        Minecraft.getInstance().setScreen(new TerminalScreen(pos));
    }

    @Override
    protected void init() {
        int boxWidth = Math.min(320, width - 40);
        inputBox = new EditBox(font, (width - boxWidth) / 2, height - 30, boxWidth, 18,
                Component.literal("comando"));
        inputBox.setMaxLength(4096);
        inputBox.setHint(Component.literal("digite um comando lua e aperte enter"));
        addRenderableWidget(inputBox);
        setInitialFocus(inputBox);
    }

    private CaseBlockEntity getCaseEntity() {
        if (minecraft == null || minecraft.level == null) {
            return null;
        }
        if (minecraft.level.getBlockEntity(pos) instanceof CaseBlockEntity caseEntity) {
            return caseEntity;
        }
        return null;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Fundo escuro sólido (sem blur) - propositalmente simples, ver nota da classe.
        graphics.fill(0, 0, width, height, 0xC0101010);

        int panelLeft = width / 2 - 160;
        int panelRight = width / 2 + 160;
        int panelTop = 20;
        int panelBottom = height - 40;
        ScreenStyle.fillRounded(graphics, panelLeft, panelTop, panelRight, panelBottom, ScreenStyle.PANEL_BG);

        CaseBlockEntity caseEntity = getCaseEntity();
        if (caseEntity == null) {
            graphics.drawCenteredString(font, "Computador não encontrado.", width / 2, panelTop + 10, 0xFF5555);
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }

        // Cabeçalho com estado da máquina.
        String statusLine = caseEntity.getPowerState().getDescription() + "  |  " + caseEntity.getTemperature() + "°"
                + "  |  " + (caseEntity.isOsInstalled() ? caseEntity.getInstalledOsName() + " instalado" : "sem SO instalado");
        int statusColor = caseEntity.getPowerState().isOn() ? 0x55FF55 : 0xFF5555;
        graphics.drawString(font, statusLine, panelLeft + 6, panelTop + 6, statusColor);

        boolean hasKeyboard = caseEntity.hasKeyboard();
        boolean hasMouse = caseEntity.hasMouse();
        if (!hasKeyboard || !hasMouse) {
            StringBuilder warning = new StringBuilder("Faltando: ");
            if (!hasKeyboard) warning.append("teclado ");
            if (!hasMouse) warning.append("mouse");
            graphics.drawString(font, warning.toString(), panelLeft + 6, panelTop + 16, 0xFFAA00);
        }

        // Corpo: histórico do terminal, as últimas linhas que cabem no painel.
        cachedHistory = caseEntity.getTerminalHistory();
        int lineHeight = 10;
        int historyTop = panelTop + 30;
        int maxLines = (panelBottom - historyTop) / lineHeight;
        int startIndex = Math.max(0, cachedHistory.size() - maxLines);
        int y = historyTop;
        for (int i = startIndex; i < cachedHistory.size(); i++) {
            graphics.drawString(font, cachedHistory.get(i), panelLeft + 6, y, 0xCCCCCC, false);
            y += lineHeight;
        }

        inputBox.setEditable(caseEntity.getPowerState().isOn() && hasKeyboard);
        if (!caseEntity.getPowerState().isOn() || !hasKeyboard) {
            inputBox.setValue("");
        }

        super.render(graphics, mouseX, mouseY, partialTick);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        CaseBlockEntity caseEntity = getCaseEntity();
        if (caseEntity != null && !caseEntity.hasMouse()) {
            // Sem mouse instalado, o jogador não consegue clicar em nada dentro da tela -
            // só teclado funciona (se o foco já estiver na caixa de comando desde o início).
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Enter (257) ou Numpad Enter (335) envia o comando.
        if ((keyCode == 257 || keyCode == 335) && inputBox.isFocused()) {
            submitCommand();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void submitCommand() {
        String command = inputBox.getValue();
        if (command.isBlank()) {
            return;
        }
        CaseBlockEntity caseEntity = getCaseEntity();
        if (caseEntity == null || !caseEntity.getPowerState().isOn() || !caseEntity.hasKeyboard()) {
            return;
        }
        PacketDistributor.sendToServer(new RunLuaCommandPayload(pos, command));
        inputBox.setValue("");
    }

    @Override
    public boolean isPauseScreen() {
        return false; // não pausa o jogo em singleplayer - é só uma tela de bloco, tipo um forno
    }
}
