package dev.arakiel.gemini.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class GeminiAttributes {
    private GeminiAttributes() {
    }

    public static void set(
            LivingEntity entity,
            Holder<Attribute> attribute,
            ResourceLocation id,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier existing = instance.getModifier(id);
        if (existing != null
                && existing.operation() == operation
                && Math.abs(existing.amount() - amount) < 1.0E-7D) {
            return;
        }
        instance.removeModifier(id);
        if (Math.abs(amount) >= 1.0E-7D) {
            instance.addTransientModifier(new AttributeModifier(id, amount, operation));
        }
    }

    public static void add(
            LivingEntity entity,
            Holder<Attribute> attribute,
            ResourceLocation id,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier existing = instance.getModifier(id);
        set(entity, attribute, id, (existing == null ? 0.0D : existing.amount()) + amount, operation);
    }

    public static void remove(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(id);
        }
    }

    public static double value(LivingEntity entity, Holder<Attribute> attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance == null ? 0.0D : instance.getValue();
    }
}
