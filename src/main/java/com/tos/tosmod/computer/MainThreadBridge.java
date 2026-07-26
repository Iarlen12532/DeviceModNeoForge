package com.tos.tosmod.computer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

/**
 * Ponte seguro entre a thread do Lua (LuaComputer, ver Fase 3) e a main thread do jogo.
 * Funções Lua que precisam mexer no mundo real (instalar o TOS, ler/mandar redstone na
 * Fase 8, etc.) chamam call(), que enfileira o pedido e ESPERA (bloqueando só a thread
 * do Lua daquele computador, nunca a thread do jogo) até a main thread processar no
 * próximo tick (CaseBlockEntity.tick() -> processPendingTasks()) e devolver o resultado.
 *
 * Esse é o mesmo padrão usado por mods reais desse tipo (OpenComputers, CC:Tweaked):
 * a lógica do jogo em si SEMPRE roda na main thread; scripts do jogador rodam fora dela
 * e "pedem" pra main thread fazer a parte que mexe no mundo.
 */
public class MainThreadBridge {

    private final BlockingQueue<Runnable> pendingTasks = new ArrayBlockingQueue<>(16);

    /** Chamado pela thread do Lua. Espera até 1 segundo pela resposta antes de desistir. */
    public <T> T call(Supplier<T> task, T defaultValue) {
        Object lock = new Object();
        Object[] result = new Object[1];
        boolean[] done = new boolean[1];

        boolean offered = pendingTasks.offer(() -> {
            T value = task.get();
            synchronized (lock) {
                result[0] = value;
                done[0] = true;
                lock.notifyAll();
            }
        });
        if (!offered) {
            return defaultValue; // fila cheia - main thread está sobrecarregada, desiste rápido
        }

        synchronized (lock) {
            long deadline = System.currentTimeMillis() + 1000;
            while (!done[0]) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    return defaultValue; // timeout - evita travar o script pra sempre
                }
                try {
                    lock.wait(remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return defaultValue;
                }
            }
        }

        @SuppressWarnings("unchecked")
        T value = (T) result[0];
        return value;
    }

    /** Chamado pela main thread (dentro de BlockEntity.tick()) - processa pedidos pendentes. */
    public void processPendingTasks() {
        Runnable task;
        while ((task = pendingTasks.poll()) != null) {
            task.run();
        }
    }
}
