package dev.arakiel.gemini.event.entity;

import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class LivingEvent extends EntityEvent {
    private final LivingEntity livingEntity;

    public LivingEvent(LivingEntity livingEntity) {
        super(Objects.requireNonNull(livingEntity, "livingEntity"));
        this.livingEntity = livingEntity;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }
}
