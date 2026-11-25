package dev.arakiel.gemini.combat;

import dev.arakiel.gemini.combat.internal.GeminiCombatImpl;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class GeminiCombat {
    private GeminiCombat() {
    }

    public static AttackEffect attackEffect(Supplier<MobEffectInstance> effect) {
        return new AttackEffect(effect);
    }

    public static AttackEffect attackEffect(Item weapon, Holder<MobEffect> effect, int duration, int amplifier) {
        return attackEffect(() -> new MobEffectInstance(effect, duration, amplifier)).withWeapon(weapon);
    }

    public static AttackEffect attackSelf(Item weapon, Holder<MobEffect> effect, int duration, int amplifier) {
        return attackEffect(weapon, effect, duration, amplifier).toAttacker();
    }

    public static final class AttackEffect {
        private final Supplier<MobEffectInstance> effect;
        private Predicate<LivingEntity> attackerFilter = attacker -> true;
        private boolean applyToAttacker;

        private AttackEffect(Supplier<MobEffectInstance> effect) {
            this.effect = Objects.requireNonNull(effect, "effect");
        }

        public AttackEffect withWeapon(Item weapon) {
            Objects.requireNonNull(weapon, "weapon");
            attackerFilter = attacker -> attacker.getMainHandItem().is(weapon);
            return this;
        }

        public AttackEffect when(Predicate<LivingEntity> filter) {
            attackerFilter = attackerFilter.and(Objects.requireNonNull(filter, "filter"));
            return this;
        }

        public AttackEffect toAttacker() {
            applyToAttacker = true;
            return this;
        }

        public AttackEffect toTarget() {
            applyToAttacker = false;
            return this;
        }

        public void register() {
            GeminiCombatImpl.register(attackerFilter, effect, applyToAttacker);
        }
    }
}
