package com.thrill12.collectathon;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
            cards = new ArrayList<>(List.of(loaded));
            addShinies();
            Collectathon.LOGGER.info("Loaded {} cards", cards.size());
        } catch (IOException e) {
            Collectathon.LOGGER.error("Failed to load cards config", e);
        }
    }

    private static void addShinies() {
        List<CardData> shinyCards = new ArrayList<>();
        for (CardData card : cards) {
            if (!card.shiny()) {
                CardData shinyCard = new CardData(card.id() + "_shiny", card.displayName(),
                        card.lore(), card.dropChance() / 50f, true);
                shinyCards.add(shinyCard);
            }
        }
        cards.addAll(shinyCards);
    }

    private static void createDefault(Path path) {
        try {
            Files.createDirectories(path.getParent());
            List<CardData> defaults = List.of(
                    new CardData("card_steve", "§6Steve", List.of("§7Season 1 MVP"), 1f, false));
            Files.writeString(path, GSON.toJson(defaults));
        } catch (IOException e) {
            Collectathon.LOGGER.error("Failed to create default cards config", e);
        }
    }

    public static List<CardData> getCards() {
        return cards;
    }
}
