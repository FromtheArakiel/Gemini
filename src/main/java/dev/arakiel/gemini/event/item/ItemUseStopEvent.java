package dev.arakiel.gemini.event.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ItemUseStopEvent extends ItemEvent {
    private final LivingEntity user;

    public ItemUseStopEvent(LivingEntity user, ItemStack stack) {
        super(stack);
        this.user = Objects.requireNonNull(user, "user");
    }

    public LivingEntity getUser() {
        return user;
    }
}
