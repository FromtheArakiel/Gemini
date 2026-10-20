package dev.arakiel.gemini.event.item;

import dev.arakiel.gemini.event.api.Cancelable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class ItemUseFinishEvent extends ItemEvent {
    private final LivingEntity user;

    public ItemUseFinishEvent(LivingEntity user, ItemStack stack) {
        super(stack);
        this.user = Objects.requireNonNull(user, "user");
    }

    public LivingEntity getUser() {
        return user;
    }
}
