package com.thrill12.collectathon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

public class CardLootModifier extends LootModifier {
    public static final MapCodec<CardLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance
                    .group(LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(m -> m.conditions))
                    .apply(instance, CardLootModifier::new));

    public CardLootModifier(LootItemCondition[] conditions) {
        super(conditions, 0);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
            LootContext context) {
        Identifier tableId = context.getQueriedLootTableId();
        String tableStr = tableId.toString();

        boolean isChest = tableStr.startsWith("minecraft:chests/");
        boolean isFishing = tableStr.startsWith("minecraft:gameplay/fishing");

        if (!isChest && !isFishing)
            return generatedLoot;

        for (CardData card : CardManager.getCards()) {
            float chance = card.dropChance() != null ? card.dropChance() : 0.05f;
            if (context.getRandom().nextFloat() < chance) {
                generatedLoot.add(ModItems.createCardStack(card));
            }
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends LootModifier> codec() {
        return CODEC;
    }
}
