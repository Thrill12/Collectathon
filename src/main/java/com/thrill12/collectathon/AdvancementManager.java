package com.thrill12.collectathon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = Collectathon.MODID)
public class AdvancementManager {
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        generateDatapack(event.getServer());
        event.getServer().execute(() -> event.getServer().getPlayerList().reloadResources());
    }

    private static void generateDatapack(MinecraftServer server) {
        Path datapackPath = server.getWorldPath(LevelResource.ROOT)
                .resolve("datapacks/collectathon_cards/data/collectathon/advancement/cards");

        try {
            Files.createDirectories(datapackPath);
            writePackMeta(datapackPath.getParent().getParent().getParent().getParent());

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Write root advancement
            JsonObject root = buildRootAdvancement();
            Files.writeString(datapackPath.resolve("root.json"), gson.toJson(root));

            Map<String, Path> setPaths = new HashMap<>();

            for (CardData card : CardManager.getCards()) {

                String rawSet = card.set(); // "&4Swamp"
                String setId = toSafeId(rawSet); // "swamp"

                Path setPath = setPaths.get(setId);

                // 1. create set folder once
                if (setPath == null) {
                    Collectathon.LOGGER.info("Creating advancement set: {}", setId);
                    setPath = datapackPath.resolve(setId);
                    Files.createDirectories(setPath);

                    JsonObject setAdv = buildSetAdvancement(rawSet, setId);
                    Files.writeString(setPath.resolve("set.json"), gson.toJson(setAdv));

                    setPaths.put(setId, setPath);
                }

                // 2. write card under set
                JsonObject advancement = buildCardAdvancement(card, setId);

                Collectathon.LOGGER.info("Creating advancement for card: {}", card.id());

                Files.writeString(setPath.resolve(card.id() + ".json"), gson.toJson(advancement));
            }

            Collectathon.LOGGER.info("Generated advancements for {} cards",
                    CardManager.getCards().size());

        } catch (IOException e) {
            Collectathon.LOGGER.error("Failed to generate card advancements", e);
        }
    }

    private static JsonObject buildSetAdvancement(String setDisplayName, String setId) {
        JsonObject obj = new JsonObject();

        obj.addProperty("parent", "collectathon:cards/root");

        JsonObject display = new JsonObject();
        JsonObject icon = new JsonObject();

        icon.addProperty("id", "minecraft:book");

        display.add("icon", icon);
        display.addProperty("title", setDisplayName);
        display.addProperty("description", "Card collection: " + setId);

        display.addProperty("frame", "challenge");
        display.addProperty("show_toast", false);
        display.addProperty("announce_to_chat", false);

        obj.add("display", display);

        JsonObject criteria = new JsonObject();
        JsonObject c = new JsonObject();
        c.addProperty("trigger", "minecraft:tick");
        criteria.add("unlock", c);

        obj.add("criteria", criteria);

        return obj;
    }

    private static void writePackMeta(Path datapackRoot) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject meta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("description", "Collectathon card advancements");
        pack.addProperty("pack_format", 48);
        meta.add("pack", pack);
        Files.writeString(datapackRoot.resolve("pack.mcmeta"), gson.toJson(meta));
    }

    private static JsonObject buildRootAdvancement() {
        JsonObject obj = new JsonObject();

        JsonObject display = new JsonObject();
        JsonObject icon = new JsonObject();
        icon.addProperty("id", "collectathon:card");

        display.add("icon", icon);
        display.addProperty("title", "Card Collector");
        display.addProperty("description", "Start collecting cards!");
        display.addProperty("background", "minecraft:gui/advancements/backgrounds/stone");
        display.addProperty("frame", "task");
        display.addProperty("show_toast", false);
        display.addProperty("announce_to_chat", false);
        obj.add("display", display);

        JsonObject criteria = new JsonObject();
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", "collectathon:card_obtained");
        criteria.add("has_any_card", criterion);
        obj.add("criteria", criteria);

        return obj;
    }

    private static JsonObject buildCardAdvancement(CardData card, String set) {
        JsonObject obj = new JsonObject();

        // Parent
        obj.addProperty("parent", "collectathon:cards/" + set + "/set");

        // Display
        JsonObject display = new JsonObject();
        JsonObject icon = new JsonObject();
        icon.addProperty("id", "collectathon:card");

        display.add("icon", icon);
        display.addProperty("title",
                card.shiny() ? "Shiny " + card.displayName() : card.displayName());
        display.addProperty("description",
                "Obtained " + (card.shiny() ? "Shiny " + card.displayName() : card.displayName()));
        display.addProperty("frame", "task");
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", true);
        obj.add("display", display);

        // Criteria using your custom trigger
        JsonObject criteria = new JsonObject();
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", "collectathon:card_obtained");
        JsonObject conditions = new JsonObject();
        conditions.addProperty("card_id", card.id());
        criterion.add("conditions", conditions);
        criteria.add("obtained_" + card.id(), criterion);
        obj.add("criteria", criteria);

        return obj;
    }

    private static String toSafeId(String input) {
        return input.replaceAll("&[0-9a-fk-or]", "") // remove color codes
                .toLowerCase().replace(" ", "_").replaceAll("[^a-z0-9_/]", "");
    }
}
