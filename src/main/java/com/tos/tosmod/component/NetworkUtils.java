package com.tos.tosmod.component;

/**
 * Fórmulas centrais do sistema de rede. Tudo num lugar só, fácil de rebalancear
 * (igual ComponentStatsDefaults) sem precisar mexer no roteador ou no computador.
 *
 * Regras que você pediu: quanto mais forte o roteador, mais dispositivos consegue
 * atender e mais rápido fica pra todo mundo; quanto mais perto do roteador (ou ligado
 * por cabo), mais rápido; apps/sistemas maiores (mais "peso" = mais KB) demoram mais.
 */
public final class NetworkUtils {

    private NetworkUtils() {}

    // Tamanho de referência do próprio TOS pra download - ajustável aqui.
    public static final int TOS_SYSTEM_SIZE_KB = 500;

    /** Quantos dispositivos um roteador com essa força consegue atender ao mesmo tempo. */
    public static int maxDevices(int routerPower) {
        return 1 + routerPower / 20;
    }

    /** Alcance em blocos do sinal sem fio - roteador mais forte cobre uma área maior. */
    public static int wirelessRange(int routerPower) {
        return Math.min(48, 8 + routerPower / 10);
    }

    /**
     * Velocidade em KB por tick. Cabo ignora completamente a distância (por isso é sempre
     * a opção mais rápida e estável pra longas distâncias - assim como na vida real).
     * Sem fio perde força com a distância; passar do alcance = sem conexão (0).
     */
    public static double speedKbPerTick(int routerPower, double distance, boolean viaCable) {
        if (routerPower <= 0) {
            return 0;
        }
        double effectiveDistance = viaCable ? 1.0 : Math.max(1.0, distance);
        if (!viaCable && distance > wirelessRange(routerPower)) {
            return 0; // fora de alcance
        }
        // /20.0 converte de "por segundo" pra "por tick" (20 ticks = 1 segundo)
        double kbPerSecond = (routerPower * 2.0) / effectiveDistance;
        return Math.max(0.05, kbPerSecond / 20.0);
    }

    /** Quantos ticks um download de determinado tamanho vai levar, na velocidade dada. */
    public static int estimateTicks(int sizeKb, double kbPerTick) {
        if (kbPerTick <= 0) {
            return Integer.MAX_VALUE; // nunca termina - sem conexão
        }
        return (int) Math.ceil(sizeKb / kbPerTick);
    }
}
