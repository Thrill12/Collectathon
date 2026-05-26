package com.thrill12.collectathon;

import java.util.ArrayList;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Collectathon.MODID)
public class Collectathon {
    public static final String MODID = "collectathon";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<CardItem> CARD_ITEM =
            ITEMS.registerItem("card", props -> new CardItem(props.stacksTo(1)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CARDS_TAB =
            CREATIVE_MODE_TABS.register("collectathon_cards",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.collectathon"))
                            .icon(() -> CARD_ITEM.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(CARD_ITEM.get());
                            }).build());

    public static final Codec<CardData> CARD_DATA_CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("id").forGetter(CardData::id),
                            Codec.STRING.fieldOf("displayName").forGetter(CardData::displayName),
                            Codec.list(Codec.STRING).fieldOf("lore").forGetter(CardData::lore))
                    .apply(instance, CardData::new));

    public static final StreamCodec<ByteBuf, CardData> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.stringUtf8(256), CardData::id,
                    ByteBufCodecs.stringUtf8(256), CardData::displayName,
                    ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.stringUtf8(256)),
                    CardData::lore, CardData::new);

    public static final DeferredRegister.DataComponents REGISTRAR =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "collectathon");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CardData>> BASIC_EXAMPLE =
            REGISTRAR.registerComponentType("card-data", builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(CARD_DATA_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(STREAM_CODEC));

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in
    // automatically.
    public Collectathon(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Collectathon) to respond
        // directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class,
        // like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        CardManager.load();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    private void RegisterItem(String name) {
        ITEMS.registerItem(name, props -> new CardItem(props.stacksTo(1)));
    }
}
