package com.thrill12.collectathon;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CardObtainedTrigger
        extends SimpleCriterionTrigger<CardObtainedTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player, instance -> instance.matches(stack));
    }

    public record TriggerInstance(Optional<String> cardId)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC =
                RecordCodecBuilder.create(instance -> instance
                        .group(Codec.STRING.optionalFieldOf("card_id")
                                .forGetter(TriggerInstance::cardId))
                        .apply(instance, TriggerInstance::new));

        public boolean matches(ItemStack stack) {
            CardData data = stack.get(ModComponents.CARD_DATA.get());
            if (data == null)
                return false;
            return cardId.map(id -> id.equals(data.id())).orElse(true);
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }
}
