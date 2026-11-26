package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class LivingUseTotemEvent extends LivingEvent {
    private final DamageSource source;
    private final ItemStack totem;
    private final InteractionHand handHolding;

    public LivingUseTotemEvent(LivingEntity entity, DamageSource source, ItemStack totem) {
        this(entity, source, totem, InteractionHand.MAIN_HAND);
    }

    public LivingUseTotemEvent(
            LivingEntity entity,
            DamageSource source,
            ItemStack totem,
            InteractionHand handHolding
    ) {
        super(entity);
        this.source = Objects.requireNonNull(source, "source");
        this.totem = Objects.requireNonNull(totem, "totem");
        this.handHolding = Objects.requireNonNull(handHolding, "handHolding");
    }

    public DamageSource getSource() {
        return this.source;
    }

    public ItemStack getTotem() {
        return this.totem;
    }

    public InteractionHand getHandHolding() {
        return this.handHolding;
    }
}
