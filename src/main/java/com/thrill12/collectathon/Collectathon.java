package com.thrill12.collectathon;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Collectathon.MODID)
public class Collectathon {
    public static final String MODID = "collectathon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ModItems ITEMS = null;
    public static ModComponents COMPONENTS = null;

    public Collectathon(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ITEMS = new ModItems(modEventBus);
        COMPONENTS = new ModComponents(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModTriggers.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CardManager.load();
    }
}
