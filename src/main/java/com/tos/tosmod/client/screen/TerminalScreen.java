package com.tos.tosmod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tos.tosmod.block.entity.CaseBlockEntity;
import com.tos.tosmod.network.RunLuaCommandPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
 * Sem caixa de texto separada - você digita DIRETO no corpo do terminal, igual um
 * terminal de verdade (a linha atual aparece com um "_" piscando no final).
 *
 * Desenho propositalmente simples (retângulos sólidos, sem blur/sombra real) - o ambiente
 * alvo é Android via PojavLauncher/Mojo com gl4es, então nada de shader customizado aqui.
 */
@OnlyIn(Dist.CLIENT)
public class TerminalScreen extends Screen {

    private final BlockPos pos;
    private List<String> cachedHistory = new ArrayList<>();
    private final StringBuilder currentLine = new StringBuilder();
    private long lastBlinkTime = 0;
    private boolean cursorVisible = true;

    public TerminalScreen(BlockPos pos) {
        super(Component.literal("Terminal"));
        this.pos = pos;
    }

    /** Ponto de entrada usado pelo CaseBlock ao ser clicado - mantém a classe Minecraft/Screen
     *  isolada aqui dentro, sem vazar referência de classe cliente pro CaseBlock em si. */
    public static void openFor(BlockPos pos) {
        Minecraft.getInstance().setScreen(new TerminalScreen(pos));
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
        int panelBottom = height - 20;
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

        boolean canType = caseEntity.getPowerState().isOn() && hasKeyboard;
        if (!canType && currentLine.length() > 0) {
            currentLine.setLength(0);
        }

        // Corpo: histórico do terminal + a linha sendo digitada agora, com cursor piscando -
        // sem caixa de texto separada, digita direto igual um terminal de verdade.
        cachedHistory = caseEntity.getTerminalHistory();
        int lineHeight = 10;
        int historyTop = panelTop + 30;
        int bodyBottom = panelBottom - 8;
        int maxLines = (bodyBottom - historyTop) / lineHeight;

        List<String> displayLines = new ArrayList<>(cachedHistory);
        if (canType) {
            long now = System.currentTimeMillis();
            if (now - lastBlinkTime > 500) {
                cursorVisible = !cursorVisible;
                lastBlinkTime = now;
            }
            displayLines.add("> " + currentLine + (cursorVisible ? "_" : ""));
        }

        int startIndex = Math.max(0, displayLines.size() - maxLines);
        int y = historyTop;
        for (int i = startIndex; i < displayLines.size(); i++) {
            graphics.drawString(font, displayLines.get(i), panelLeft + 6, y, 0xCCCCCC, false);
            y += lineHeight;
        }

        super.render(graphics, mouseX, mouseY, partialTick);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        CaseBlockEntity caseEntity = getCaseEntity();
        if (caseEntity != null && !caseEntity.hasMouse()) {
            // Sem mouse instalado, o jogador não consegue clicar em nada dentro da tela -
            // só teclado funciona (digitar não depende de clicar em lugar nenhum agora).
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        CaseBlockEntity caseEntity = getCaseEntity();
        boolean canType = caseEntity != null && caseEntity.getPowerState().isOn() && caseEntity.hasKeyboard();
        if (canType) {
            if (keyCode == 257 || keyCode == 335) { // Enter / Numpad Enter
                submitCommand();
                return true;
            }
            if (keyCode == 259) { // Backspace
                if (currentLine.length() > 0) {
                    currentLine.deleteCharAt(currentLine.length() - 1);
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        CaseBlockEntity caseEntity = getCaseEntity();
        boolean canType = caseEntity != null && caseEntity.getPowerState().isOn() && caseEntity.hasKeyboard();
        if (canType && currentLine.length() < 4096) {
            currentLine.append(codePoint);
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private void submitCommand() {
        String command = currentLine.toString();
        currentLine.setLength(0);
        if (command.isBlank()) {
            return;
        }
        CaseBlockEntity caseEntity = getCaseEntity();
        if (caseEntity == null || !caseEntity.getPowerState().isOn() || !caseEntity.hasKeyboard()) {
            return;
        }
        PacketDistributor.sendToServer(new RunLuaCommandPayload(pos, command));
    }

    @Override
    public boolean isPauseScreen() {
        return false; // não pausa o jogo em singleplayer - é só uma tela de bloco, tipo um forno
    }
}
