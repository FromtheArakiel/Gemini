package dev.arakiel.gemini.attribute;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.attribute.internal.GeminiFactorAttribute;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public final class GeminiAttributes {
    public static final Holder<Attribute> CRIT_RATE = factor("crit_rate", 0.0D, 0.0D, 1.0D);
    public static final Holder<Attribute> CRIT_DAMAGE = factor("crit_damage", 0.5D, 0.0D, 1000.0D);
    public static final Holder<Attribute> PROJECTILE_DAMAGE = factor("projectile_damage", 1.0D, 0.0D, 1000.0D);
    public static final Holder<Attribute> EXPLOSION_DAMAGE = factor("explosion_damage", 1.0D, 0.0D, 1000.0D);
    public static final Holder<Attribute> FIRE_DAMAGE = factor("fire_damage", 1.0D, 0.0D, 1000.0D);
    public static final Holder<Attribute> FREEZING_DAMAGE = factor("freezing_damage", 1.0D, 0.0D, 1000.0D);
    public static final Holder<Attribute> LIGHTNING_DAMAGE = factor("lightning_damage", 1.0D, 0.0D, 1000.0D);
    public static final Holder<Attribute> MAGIC_DAMAGE = factor("magic_damage", 1.0D, 0.0D, 1000.0D);
    public static final Holder<Attribute> REGENERATION_RATE = factor("regeneration_rate", 1.0D, 0.0D, 1000.0D);
    public static final Holder<Attribute> DAMAGE_ABSORPTION = ranged("damage_absorption", 0.0D, 0.0D, 10000.0D);
    public static final Holder<Attribute> DAMAGE_REDUCTION = register(
            "damage_reduction",
            new GeminiFactorAttribute(translation("damage_reduction"), 1.0D, -10000.0D, 10000.0D)
                    .setSentiment(Attribute.Sentiment.NEGATIVE)
                    .setSyncable(true)
    );

    private GeminiAttributes() {
    }

    public static void init() {
    }

    public static void addLivingAttributes(AttributeSupplier.Builder builder) {
        builder.add(PROJECTILE_DAMAGE);
        builder.add(EXPLOSION_DAMAGE);
        builder.add(FIRE_DAMAGE);
        builder.add(FREEZING_DAMAGE);
        builder.add(LIGHTNING_DAMAGE);
        builder.add(MAGIC_DAMAGE);
        builder.add(REGENERATION_RATE);
        builder.add(DAMAGE_ABSORPTION);
        builder.add(DAMAGE_REDUCTION);
    }

    public static void addPlayerAttributes(AttributeSupplier.Builder builder) {
        builder.add(CRIT_RATE);
        builder.add(CRIT_DAMAGE);
    }

    private static Holder<Attribute> factor(String path, double defaultValue, double min, double max) {
        return register(path, new GeminiFactorAttribute(translation(path), defaultValue, min, max)
                .setSyncable(true));
    }

    private static Holder<Attribute> ranged(String path, double defaultValue, double min, double max) {
        return register(path, new RangedAttribute(translation(path), defaultValue, min, max)
                .setSyncable(true));
    }

    private static Holder<Attribute> register(String path, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Gemini.id(path), attribute);
    }

    private static String translation(String path) {
        return "attribute." + Gemini.MOD_ID + "." + path;
    }
}
