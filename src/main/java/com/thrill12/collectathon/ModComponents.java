package com.thrill12.collectathon;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, Collectathon.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CardData>> CARD_DATA =
            DATA_COMPONENTS.registerComponentType("card_data", builder -> builder
                    .persistent(CardData.CODEC).networkSynchronized(CardData.STREAM_CODEC));

    public ModComponents(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
