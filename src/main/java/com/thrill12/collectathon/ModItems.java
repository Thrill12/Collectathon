package com.thrill12.collectathon;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Collectathon.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Collectathon.MODID);

    public static final DeferredItem<CardItem> CARD_ITEM = RegisterItem("card");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CARDS_TAB =
            CREATIVE_MODE_TABS.register("collectathon_cards",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.collectathon"))
                            .icon(() -> CARD_ITEM.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                for (CardData card : CardManager.getCards()) {
                                    output.accept(createCardStack(card));
                                }
                            }).build());

    public ModItems(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static ItemStack createCardStack(CardData data) {
        ItemStack stack = new ItemStack(CARD_ITEM.get());
        stack.set(ModComponents.CARD_DATA.get(), data);

        return stack;
    }

    private static DeferredItem<CardItem> RegisterItem(String itemName) {
        return ITEMS.registerItem(itemName, props -> new CardItem(props.stacksTo(1)));
    }
}
