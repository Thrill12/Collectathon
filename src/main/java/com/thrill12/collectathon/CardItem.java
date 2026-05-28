package com.thrill12.collectathon;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CardItem extends Item {
    public CardItem(Properties props) {
        super(props);
    }

    @Override
    public Component getName(ItemStack stack) {
        CardData data = stack.get(ModComponents.CARD_DATA.get());

        if (data != null) {
            return Component.literal(data.displayName());
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
            List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        CardData data = stack.get(ModComponents.CARD_DATA.get());
        if (data != null) {
            tooltipComponents
                    .add(Component.literal(data.set() + " Set").withStyle(ChatFormatting.AQUA));

            // Empty line
            tooltipComponents.add(Component.literal(""));

            for (String line : data.lore()) {
                tooltipComponents.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CardData data = stack.get(ModComponents.CARD_DATA.get());
        return data != null && data.shiny();
    }
}
