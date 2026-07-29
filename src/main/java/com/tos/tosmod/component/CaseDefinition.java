package com.tos.tosmod.component;

import java.util.EnumMap;
import java.util.Map;

/**
 * Define quantos slots de cada tipo uma Case tem, e se ela é portátil (notebook - usa bateria)
 * ou fixa (desktop/servidor - usa PSU).
 *
 * Isso é 100% separado do modelo 3D/textura. Um "notebook gamer" e um "notebook fino" podem
 * usar CaseDefinitions diferentes (fino com menos slots de GPU, por ex.), enquanto vários
 * modelos visuais diferentes de torre podem compartilhar a MESMA CaseDefinition.
 */
public record CaseDefinition(
        String id,
        Map<SlotType, Integer> slotCounts,
        boolean portable,      // true = notebook (usa bateria), false = desktop/servidor (usa PSU)
        int baseCoolingCapacity, // usado na Fase 2 (temperatura) - quanto de calor a case dissipa sozinha
        boolean hasIntegratedScreen // notebook/all-in-one já tem tela; torre/servidor precisam de monitor externo (Fase 8)
) {

    public int slotCount(SlotType type) {
        return slotCounts.getOrDefault(type, 0);
    }

    public int totalSlots() {
        return slotCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** Builder simples pra deixar a definição de cada case legível. */
    public static class Builder {
        private final String id;
        private final Map<SlotType, Integer> slots = new EnumMap<>(SlotType.class);
        private boolean portable = false;
        private int cooling = 20;
        private boolean hasIntegratedScreen = true; // maioria tem tela embutida por padrão

        public Builder(String id) {
            this.id = id;
        }

        public Builder slot(SlotType type, int count) {
            slots.put(type, count);
            return this;
        }

        public Builder portable(boolean value) {
            this.portable = value;
            return this;
        }

        public Builder cooling(int value) {
            this.cooling = value;
            return this;
        }

        public Builder hasIntegratedScreen(boolean value) {
            this.hasIntegratedScreen = value;
            return this;
        }

        public CaseDefinition build() {
            return new CaseDefinition(id, slots, portable, cooling, hasIntegratedScreen);
        }
    }
}
