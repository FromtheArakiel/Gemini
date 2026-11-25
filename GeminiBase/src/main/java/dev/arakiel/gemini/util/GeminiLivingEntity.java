package dev.arakiel.gemini.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public final class GeminiLivingEntity {
    private GeminiLivingEntity() {
    }

    public static boolean hurt(LivingEntity entity, DamageSource source, float amount) {
        return entity.hurt(source, Math.max(0.0F, amount));
    }

    public static void heal(LivingEntity entity, float amount) {
        entity.heal(Math.max(0.0F, amount));
    }

    public static void healPercent(LivingEntity entity, float fraction) {
        heal(entity, entity.getMaxHealth() * Math.max(0.0F, fraction));
    }

    public static boolean addEffect(LivingEntity entity, MobEffectInstance effect) {
        return entity.addEffect(effect);
    }

    public static boolean addEffect(LivingEntity entity, Holder<MobEffect> effect, int duration, int amplifier) {
        return entity.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }

    public static boolean addEffect(LivingEntity entity, MobEffect effect, int duration, int amplifier) {
        return addEffect(entity, BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), duration, amplifier);
    }

    public static boolean removeEffect(LivingEntity entity, Holder<MobEffect> effect) {
        return entity.removeEffect(effect);
    }

    public static boolean removeEffect(LivingEntity entity, MobEffect effect) {
        return removeEffect(entity, BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect));
    }

    public static MobEffectInstance getEffect(LivingEntity entity, Holder<MobEffect> effect) {
        return entity.getEffect(effect);
    }

    public static MobEffectInstance getEffect(LivingEntity entity, MobEffect effect) {
        return getEffect(entity, BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect));
    }

    public static AttributeInstance attribute(LivingEntity entity, Holder<Attribute> attribute) {
        return entity.getAttribute(attribute);
    }

    public static double attributeValue(LivingEntity entity, Holder<Attribute> attribute) {
        AttributeInstance instance = attribute(entity, attribute);
        return instance == null ? 0.0D : instance.getValue();
    }

    public static double healthPercent(LivingEntity entity) {
        float maxHealth = entity.getMaxHealth();
        return maxHealth <= 0.0F ? 0.0D : entity.getHealth() / maxHealth;
    }
}
