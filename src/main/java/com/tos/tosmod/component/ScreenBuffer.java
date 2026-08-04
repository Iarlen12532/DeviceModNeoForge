package com.tos.tosmod.component;

import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;

/**
 * Buffer de tela character-based (linhas x colunas, cada célula com caractere + cor de
 * frente/fundo) - o mesmo modelo de GPU que o OpenComputers/MineOS usa. Não é pixel puro
 * (isso pesaria muito mais pra sincronizar e renderizar); é uma grade de texto colorido,
 * simples de desenhar em Java e barata de sincronizar entre servidor e cliente.
 *
 * Vive dentro da CaseBlockEntity; Lua escreve nela através da API "gpu" (ver GpuApi),
 * sempre pela main thread (MainThreadBridge) - a tela em si (DesktopScreen) só LÊ esses
 * dados já prontos, nunca desenha nada por conta própria.
 */
public class ScreenBuffer {

    public static final int MAX_WIDTH = 80;
    public static final int MAX_HEIGHT = 30;
    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 16;
    private static final int DEFAULT_FG = 0xE0E0E0;
    private static final int DEFAULT_BG = 0x101010;

    private int width;
    private int height;
    private char[] chars;
    private int[] fg;
    private int[] bg;
    private int currentFg = DEFAULT_FG;
    private int currentBg = DEFAULT_BG;

    public ScreenBuffer() {
        resize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void resize(int newWidth, int newHeight) {
        this.width = Math.max(1, Math.min(MAX_WIDTH, newWidth));
        this.height = Math.max(1, Math.min(MAX_HEIGHT, newHeight));
        this.chars = new char[width * height];
        this.fg = new int[width * height];
        this.bg = new int[width * height];
        clear();
    }

    public void clear() {
        Arrays.fill(chars, ' ');
        Arrays.fill(fg, currentFg);
        Arrays.fill(bg, currentBg);
    }

    public void setForeground(int color) {
        currentFg = color & 0xFFFFFF;
    }

    public void setBackground(int color) {
        currentBg = color & 0xFFFFFF;
    }

    public int getForeground() {
        return currentFg;
    }

    public int getBackground() {
        return currentBg;
    }

    public void set(int x, int y, String text) {
        if (y < 0 || y >= height) return;
        for (int i = 0; i < text.length(); i++) {
            int cx = x + i;
            if (cx < 0 || cx >= width) continue;
            int idx = y * width + cx;
            chars[idx] = text.charAt(i);
            fg[idx] = currentFg;
            bg[idx] = currentBg;
        }
    }

    public void fill(int x, int y, int w, int h, char c) {
        for (int yy = Math.max(0, y); yy < Math.min(height, y + h); yy++) {
            for (int xx = Math.max(0, x); xx < Math.min(width, x + w); xx++) {
                int idx = yy * width + xx;
                chars[idx] = c;
                fg[idx] = currentFg;
                bg[idx] = currentBg;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    /** true se nenhum programa desenhou nada de verdade ainda (só espaços). */
    public boolean isBlank() {
        for (char c : chars) {
            if (c != ' ') return false;
        }
        return true;
    }

    public int getHeight() {
        return height;
    }

    public char charAt(int x, int y) {
        return chars[y * width + x];
    }

    public int fgAt(int x, int y) {
        return fg[y * width + x];
    }

    public int bgAt(int x, int y) {
        return bg[y * width + x];
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("w", width);
        tag.putInt("h", height);
        tag.putString("chars", new String(chars));
        tag.putIntArray("fg", fg);
        tag.putIntArray("bg", bg);
        tag.putInt("cur_fg", currentFg);
        tag.putInt("cur_bg", currentBg);
        return tag;
    }

    public void load(CompoundTag tag) {
        int w = tag.contains("w") ? tag.getInt("w") : DEFAULT_WIDTH;
        int h = tag.contains("h") ? tag.getInt("h") : DEFAULT_HEIGHT;
        currentFg = tag.contains("cur_fg") ? tag.getInt("cur_fg") : DEFAULT_FG;
        currentBg = tag.contains("cur_bg") ? tag.getInt("cur_bg") : DEFAULT_BG;
        resize(w, h);
        String s = tag.getString("chars");
        for (int i = 0; i < Math.min(s.length(), chars.length); i++) {
            chars[i] = s.charAt(i);
        }
        int[] fgArr = tag.getIntArray("fg");
        int[] bgArr = tag.getIntArray("bg");
        System.arraycopy(fgArr, 0, fg, 0, Math.min(fgArr.length, fg.length));
        System.arraycopy(bgArr, 0, bg, 0, Math.min(bgArr.length, bg.length));
    }
}
