package com.tos.tosmod.item;

import com.tos.tosmod.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Folha impressa (Fase 7) - carrega o texto que saiu da impressora. */
public class PrintedPaperItem extends Item {

    public PrintedPaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String text = stack.get(ModDataComponents.PRINTED_TEXT);
        if (text != null && !text.isBlank()) {
            tooltip.add(Component.literal(text).withStyle(ChatFormatting.GRAY));
        }
    }
}
