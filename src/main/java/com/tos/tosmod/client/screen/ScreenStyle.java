package com.tos.tosmod.client.screen;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Paleta e helpers visuais do TOS (Fase 9) - inspirado no macOS anterior ao 26 (Sonoma/
 * Sequoia): cantos arredondados, fosco, sem "vidro líquido" de verdade (isso pesaria
 * demais no seu ambiente Android/Mali/gl4es). Os "cantos arredondados" aqui são uma
 * aproximação barata (corta pixels no canto em vez de um blur real) - funciona bem em
 * baixa resolução e não usa shader nenhum.
 */
public final class ScreenStyle {

    private ScreenStyle() {}

    public static final int WINDOW_BG = 0xF0E8E8EC;      // cinza claro fosco, tipo macOS claro
    public static final int PANEL_BG = 0xF0303034;        // painel escuro (terminal/console)
    public static final int MENU_BAR_BG = 0xF0D8D8DC;
    public static final int DOCK_BG = 0xD0D8D8DC;
    public static final int ACCENT = 0xFF4A90D9;           // azul discreto de destaque
    public static final int TEXT_DARK = 0xFF202020;
    public static final int TEXT_LIGHT = 0xFFE0E0E0;
    public static final int TEXT_MUTED = 0xFF808080;

    /** Retângulo com cantos "arredondados" baratos (corte de 2px nos 4 cantos, sem shader). */
    public static void fillRounded(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1 + 2, y1, x2 - 2, y2, color);
        graphics.fill(x1, y1 + 2, x2, y2 - 2, color);
        graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, color);
    }
}
