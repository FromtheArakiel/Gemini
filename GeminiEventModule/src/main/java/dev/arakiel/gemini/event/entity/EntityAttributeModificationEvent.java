package dev.arakiel.gemini.event.entity;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.Map;
import java.util.Objects;

public class EntityAttributeModificationEvent extends GeminiEvent {
    private final Map<EntityType<?>, AttributeSupplier.Builder> builders;

    public EntityAttributeModificationEvent(Map<EntityType<?>, AttributeSupplier.Builder> builders) {
        this.builders = Objects.requireNonNull(builders, "builders");
    }

    public void add(EntityType<?> type, Holder<Attribute> attribute, double value) {
        Objects.requireNonNull(type, "type");
        AttributeSupplier.Builder builder = this.builders.get(type);
        if (builder == null) {
            throw new IllegalArgumentException("No attribute builder registered for " + type);
        }
        builder.add(attribute, value);
    }

    public boolean has(EntityType<?> type) {
        return this.builders.containsKey(type);
    }
}
