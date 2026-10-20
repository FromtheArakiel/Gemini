package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.api.Cancelable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class ItemUseStartEvent extends ItemEvent {
    private final LivingEntity user;
    private final InteractionHand hand;

    public ItemUseStartEvent(LivingEntity user, ItemStack stack, InteractionHand hand) {
        super(stack);
        this.user = Objects.requireNonNull(user, "user");
        this.hand = Objects.requireNonNull(hand, "hand");
    }

    public LivingEntity getUser() {
        return user;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
