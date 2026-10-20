package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.api.Cancelable;
import dev.arakiel.gemini.event.entity.LivingEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

@Cancelable
public class LivingDeathEvent extends LivingEvent {
    private final DamageSource source;

    public LivingDeathEvent(LivingEntity entity, DamageSource source) {
        super(entity);
        this.source = Objects.requireNonNull(source, "source");
    }

    public DamageSource getSource() {
        return source;
    }
}
