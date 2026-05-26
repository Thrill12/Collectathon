package com.thrill12.collectathon;

import java.util.ArrayList;
import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Record used to represent data as a data component on the item stack. This is parsed inside
 * `CardItem.java` for display name and lore. `dropChance` is decimal percentage chance of dropping
 * a random card, on every fish and chest loot roll.
 */
public record CardData(String id, String displayName, List<String> lore, Float dropChance) {
    public static final Codec<CardData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.STRING.fieldOf("id").forGetter(CardData::id),
                    Codec.STRING.fieldOf("displayName").forGetter(CardData::displayName),
                    Codec.list(Codec.STRING).fieldOf("lore").forGetter(CardData::lore),
                    Codec.FLOAT.fieldOf("dropChance").forGetter(CardData::dropChance))
            .apply(instance, CardData::new));

    public static final StreamCodec<ByteBuf, CardData> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.stringUtf8(256), CardData::id,
                    ByteBufCodecs.stringUtf8(256), CardData::displayName,
                    ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.stringUtf8(256)),
                    CardData::lore, ByteBufCodecs.FLOAT, CardData::dropChance, CardData::new);
}
