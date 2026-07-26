package com.tos.tosmod.item;

import com.tos.tosmod.component.ComponentStats;
import com.tos.tosmod.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Item que representa qualquer componente de hardware (CPU, APU, GPU, RAM, Storage, PSU, Bateria).
 * Os atributos reais (consumo, calor, performance, etc.) ficam no Data Component ComponentStats,
 * não fixos na classe — assim um único ComponentItem serve pra dezenas de variações/tiers,
 * cada uma criada só com um ItemStack.Builder diferente no registro (ModItems).
 */
public class ComponentItem extends Item {

    public ComponentItem(Properties properties) {
        super(properties);
    }

    /** Cria um ItemStack de 1 unidade já com os atributos técnicos anexados. */
    public static ItemStack createStack(Item item, ComponentStats stats) {
        ItemStack stack = new ItemStack(item);
        stack.set(ModDataComponents.COMPONENT_STATS, stats);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ComponentStats stats = stack.get(ModDataComponents.COMPONENT_STATS);
        if (stats == null) return;

        tooltip.add(Component.literal("Tier " + stats.tier() + (stats.series().isEmpty() ? "" : " - " + stats.series()))
                .withStyle(ChatFormatting.GRAY));

        if (stats.performance() > 0) {
            tooltip.add(Component.literal("Desempenho: " + stats.performance()).withStyle(ChatFormatting.AQUA));
        }
        if (stats.capacity() > 0) {
            tooltip.add(Component.literal("Capacidade: " + stats.capacity()).withStyle(ChatFormatting.AQUA));
        }
        if (stats.wattDraw() > 0) {
            tooltip.add(Component.literal("Consumo: " + stats.wattDraw() + "W").withStyle(ChatFormatting.YELLOW));
        }
        if (stats.wattSupply() > 0) {
            tooltip.add(Component.literal("Fornece: " + stats.wattSupply() + "W").withStyle(ChatFormatting.GREEN));
        }
        if (stats.heatOutput() > 0) {
            tooltip.add(Component.literal("Calor gerado: " + stats.heatOutput()).withStyle(ChatFormatting.RED));
        }
    }
}
