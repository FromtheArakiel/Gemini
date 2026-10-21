package dev.arakiel.gemini.attribute;

import dev.arakiel.gemini.Gemini;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.List;

public final class GeminiDamageTypes {
    public static final ResourceKey<DamageType> BLEED = key("bleed");
    public static final ResourceKey<DamageType> SOUL_FLAME = key("soul_flame");
    public static final ResourceKey<DamageType> FREEZE = key("freeze");
    public static final ResourceKey<DamageType> LIFE_SYNC = key("life_sync");
    public static final ResourceKey<DamageType> VOID_EYE = key("void_eye");
    public static final ResourceKey<DamageType> EMERALD = key("emerald");

    public static final List<ResourceKey<DamageType>> ATTACK_VARIANTS = List.of(
            variant("player_attack-bypass_armor"),
            variant("player_attack-bypass_magic"),
            variant("player_attack-bypass_cooldown"),
            variant("player_attack-bypass_armor-bypass_magic"),
            variant("player_attack-bypass_armor-bypass_cooldown"),
            variant("player_attack-bypass_cooldown-bypass_magic"),
            variant("player_attack-bypass_armor-bypass_cooldown-bypass_magic"),
            variant("mob_attack-bypass_armor"),
            variant("mob_attack-bypass_magic"),
            variant("mob_attack-bypass_cooldown"),
            variant("mob_attack-bypass_armor-bypass_magic"),
            variant("mob_attack-bypass_armor-bypass_cooldown"),
            variant("mob_attack-bypass_cooldown-bypass_magic"),
            variant("mob_attack-bypass_armor-bypass_cooldown-bypass_magic")
    );

    public static final TagKey<DamageType> DIRECT = tag("direct");
    public static final TagKey<DamageType> MATERIAL_MUX = tag("material_mux");
    public static final TagKey<DamageType> IGNORES_SCALING = tag("ignores_scaling");
    public static final TagKey<DamageType> MAGIC = tag("magic");

    private GeminiDamageTypes() {
    }

    public static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Gemini.id(path));
    }

    public static ResourceKey<DamageType> variant(String path) {
        return key(path);
    }

    public static TagKey<DamageType> tag(String path) {
        return TagKey.create(Registries.DAMAGE_TYPE, Gemini.id(path));
    }
}
