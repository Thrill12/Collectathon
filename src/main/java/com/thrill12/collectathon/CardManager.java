package com.thrill12.collectathon;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

public class CardManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static List<CardData> cards = new ArrayList<>();

    public static void load() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("collectathon/cards.json");

        // Create default file if it doesn't exist
        if (!Files.exists(configPath)) {
            createDefault(configPath);
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            CardData[] loaded = GSON.fromJson(reader, CardData[].class);
            cards = Arrays.asList(loaded);
            Collectathon.LOGGER.info("Loaded {} cards", cards.size());
        } catch (IOException e) {
            Collectathon.LOGGER.error("Failed to load cards config", e);
        }
    }

    private static void createDefault(Path path) {
        try {
            Files.createDirectories(path.getParent());
            List<CardData> defaults =
                    List.of(new CardData("card_steve", "§6Steve", List.of("§7Season 1 MVP")));
            Files.writeString(path, GSON.toJson(defaults));
        } catch (IOException e) {
            Collectathon.LOGGER.error("Failed to create default cards config", e);
        }
    }

    public static List<CardData> getCards() {
        return cards;
    }
}
