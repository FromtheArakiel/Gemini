package dev.arakiel.gemini.event.entity;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

import java.util.Objects;

@Cancelable
public class EntityStruckByLightningEvent extends EntityEvent {
    private final LightningBolt lightning;

    public EntityStruckByLightningEvent(Entity entity, LightningBolt lightning) {
        super(entity);
        this.lightning = Objects.requireNonNull(lightning, "lightning");
    }

    public LightningBolt getLightning() {
        return this.lightning;
    }
}
