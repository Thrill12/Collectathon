package com.thrill12.collectathon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = Collectathon.MODID)
public class CardEventHandler {
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player))
            return;

        ItemStack stack = event.getOriginalStack();

        if (stack.getItem() instanceof CardItem) {
            ModTriggers.CARD_OBTAINED.get().trigger(player, stack);
        }
    }
}
