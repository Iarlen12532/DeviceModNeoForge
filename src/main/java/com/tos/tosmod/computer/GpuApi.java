package com.tos.tosmod.computer;

/**
 * Funções Lua da tabela "gpu" - a ponte de desenho de verdade. Igual a API real do
 * OpenComputers (component.gpu), só que fala com o ScreenBuffer da própria case em vez
 * de um componente de tela separado. Isso é o alicerce pra qualquer coisa gráfica no TOS
 * (janelas, dock, apps) ser construída em cima depois.
 */
public interface GpuApi {
    String setResolution(int width, int height);
    String getResolution();
    String set(int x, int y, String text);
    String fill(int x, int y, int width, int height, String character);
    String setForeground(int color);
    String setBackground(int color);
    String getForeground();
    String getBackground();
}
