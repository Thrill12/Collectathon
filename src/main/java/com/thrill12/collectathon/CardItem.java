package com.thrill12.collectathon;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

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
            TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {

        CardData data = stack.get(ModComponents.CARD_DATA.get());
        if (data != null) {
            for (String line : data.lore()) {
                builder.accept(Component.literal(line));
            }
        }

        super.appendHoverText(stack, context, display, builder, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CardData data = stack.get(ModComponents.CARD_DATA.get());
        return data != null && data.shiny();
    }
}
