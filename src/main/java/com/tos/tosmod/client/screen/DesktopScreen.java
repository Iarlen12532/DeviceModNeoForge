package com.tos.tosmod.client.screen;

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
 * "TOS" de verdade (Fase 9) - a camada visual que fica POR CIMA do terminal cru (Fase 3/4).
 * Barra de menu no topo, dock embaixo com os apps salvos via fs.save() (Fase 9), e uma
 * área de saída no meio que reaproveita o MESMO histórico do terminal (o app rodado pelo
 * dock nada mais é que um runLuaCommand com o conteúdo do arquivo).
 *
 * Sem caixa de texto separada - digita direto na janela do terminal embutida, igual a
 * TerminalScreen (a linha atual aparece com um "_" piscando no final).
 *
 * Visual: cantos "arredondados" baratos e cores foscas (ver ScreenStyle) - inspirado no
 * macOS anterior ao 26, sem blur real, pra não pesar no ambiente Android/Mali/gl4es.
 * Ícones dos apps são só quadrados com a primeira letra do nome - os modelos/texturas
 * de verdade entram quando você tiver os seus prontos (isso é só o esqueleto funcional).
 */
@OnlyIn(Dist.CLIENT)
public class DesktopScreen extends Screen {

    private final BlockPos pos;
    private List<String> dockApps = new ArrayList<>();
    private int hoveredDockIndex = -1;
    private final StringBuilder currentLine = new StringBuilder();
    private long lastBlinkTime = 0;
    private boolean cursorVisible = true;

    public DesktopScreen(BlockPos pos) {
        super(Component.literal("TOS"));
        this.pos = pos;
    }

    public static void openFor(BlockPos pos) {
        Minecraft.getInstance().setScreen(new DesktopScreen(pos));
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
        graphics.fill(0, 0, width, height, 0xC0101010);

        CaseBlockEntity caseEntity = getCaseEntity();
        if (caseEntity == null) {
            graphics.drawCenteredString(font, "Computador não encontrado.", width / 2, height / 2, 0xFF5555);
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }

        int panelLeft = width / 2 - 170;
        int panelRight = width / 2 + 170;
        int menuBarBottom = 24;
        int dockTop = height - 64;

        // Barra de menu (topo) - nome do SO, status, temperatura.
        ScreenStyle.fillRounded(graphics, panelLeft, 8, panelRight, menuBarBottom, ScreenStyle.MENU_BAR_BG);
        String menuText = (caseEntity.isOsInstalled() ? caseEntity.getInstalledOsName() : "sem SO")
                + "  |  " + caseEntity.getPowerState().getDescription() + "  |  " + caseEntity.getTemperature() + "°";
        graphics.drawString(font, menuText, panelLeft + 8, 14, ScreenStyle.TEXT_DARK, false);

        // Janela central (terminal embutido) - fosco escuro, cantos arredondados.
        int windowTop = menuBarBottom + 6;
        int windowBottom = dockTop - 6;
        ScreenStyle.fillRounded(graphics, panelLeft, windowTop, panelRight, windowBottom, ScreenStyle.PANEL_BG);

        boolean canType = caseEntity.getPowerState().isOn() && caseEntity.hasKeyboard();
        if (!canType && currentLine.length() > 0) {
            currentLine.setLength(0);
        }

        List<String> history = new ArrayList<>(caseEntity.getTerminalHistory());
        if (canType) {
            long now = System.currentTimeMillis();
            if (now - lastBlinkTime > 500) {
                cursorVisible = !cursorVisible;
                lastBlinkTime = now;
            }
            history.add("> " + currentLine + (cursorVisible ? "_" : ""));
        }

        int lineHeight = 10;
        int maxLines = (windowBottom - windowTop - 10) / lineHeight;
        int startIndex = Math.max(0, history.size() - maxLines);
        int y = windowTop + 6;
        for (int i = startIndex; i < history.size(); i++) {
            graphics.drawString(font, history.get(i), panelLeft + 6, y, ScreenStyle.TEXT_LIGHT, false);
            y += lineHeight;
        }

        // Dock (embaixo) - um ícone por app salvo via fs.save().
        ScreenStyle.fillRounded(graphics, panelLeft, dockTop, panelRight, height - 24, ScreenStyle.DOCK_BG);
        dockApps = new ArrayList<>(caseEntity.getVirtualFiles().keySet());
        hoveredDockIndex = -1;
        int iconSize = 20;
        int iconGap = 6;
        int iconX = panelLeft + 8;
        int iconY = dockTop + 6;
        for (int i = 0; i < dockApps.size(); i++) {
            boolean hovered = mouseX >= iconX && mouseX <= iconX + iconSize && mouseY >= iconY && mouseY <= iconY + iconSize;
            if (hovered) {
                hoveredDockIndex = i;
            }
            graphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, hovered ? ScreenStyle.ACCENT : 0xFF505050);
            String letter = dockApps.get(i).isEmpty() ? "?" : dockApps.get(i).substring(0, 1).toUpperCase();
            graphics.drawCenteredString(font, letter, iconX + iconSize / 2, iconY + 6, ScreenStyle.TEXT_LIGHT);
            iconX += iconSize + iconGap;
            if (iconX + iconSize > panelRight - 8) {
                break; // dock cheio - sem scroll ainda, mais um detalhe pra Fase 9 visual completa
            }
        }
        if (hoveredDockIndex >= 0) {
            graphics.drawString(font, dockApps.get(hoveredDockIndex), panelLeft + 8, dockTop - 10, ScreenStyle.TEXT_LIGHT);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        CaseBlockEntity caseEntity = getCaseEntity();
        if (caseEntity != null && !caseEntity.hasMouse()) {
            return false; // sem mouse instalado - só teclado funciona
        }
        if (hoveredDockIndex >= 0 && hoveredDockIndex < dockApps.size() && caseEntity != null
                && caseEntity.getPowerState().isOn() && caseEntity.hasKeyboard()) {
            String appName = dockApps.get(hoveredDockIndex);
            // Roda o app: carrega o conteúdo salvo e executa como se tivesse sido digitado
            // no terminal - simples e reaproveita 100% da infra do runLuaCommand já existente.
            PacketDistributor.sendToServer(new RunLuaCommandPayload(pos, "local __c = fs.load(\"" + appName + "\"); local __f = load(__c); if __f then __f() else print('erro ao carregar app') end"));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        CaseBlockEntity caseEntity = getCaseEntity();
        boolean canType = caseEntity != null && caseEntity.getPowerState().isOn() && caseEntity.hasKeyboard();
        if (canType) {
            if (keyCode == 257 || keyCode == 335) {
                submitCommand();
                return true;
            }
            if (keyCode == 259) {
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
        return false;
    }
}
