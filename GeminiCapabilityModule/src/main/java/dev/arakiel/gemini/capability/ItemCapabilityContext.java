package dev.arakiel.gemini.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record ItemCapabilityContext(
        ItemStack stack,
        @Nullable LivingEntity holder,
        @Nullable String slotContext
) implements CapabilityContext {
    public ItemCapabilityContext {
        Objects.requireNonNull(stack, "stack");
    }

    @Override
    public @Nullable Direction direction() {
        return null;
    }
}
