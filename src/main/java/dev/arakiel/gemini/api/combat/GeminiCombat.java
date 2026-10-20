package dev.arakiel.gemini.api.combat;

import dev.arakiel.gemini.api.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingHurtEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class GeminiCombat {
    private static final List<AttackEffectRule> RULES = new ArrayList<>();
    private static boolean initialized;

    private GeminiCombat() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        GeminiEvents.listen(LivingHurtEvent.class, event -> applyRules(event));
    }

    public static AttackEffectBuilder attackEffect(Supplier<MobEffectInstance> effect) {
        return new AttackEffectBuilder(effect);
    }

    public static AttackEffectBuilder attackEffect(Item weapon, Holder<MobEffect> effect, int duration, int amplifier) {
        return attackEffect(() -> new MobEffectInstance(effect, duration, amplifier)).withWeapon(weapon);
    }

    public static AttackEffectBuilder attackSelf(Item weapon, Holder<MobEffect> effect, int duration, int amplifier) {
        return attackEffect(weapon, effect, duration, amplifier).toAttacker();
    }

    public static void register(Predicate<LivingEntity> attackerFilter, Supplier<MobEffectInstance> effect, boolean applyToAttacker) {
        RULES.add(new AttackEffectRule(attackerFilter, effect, applyToAttacker));
    }

    private static void applyRules(LivingHurtEvent event) {
        LivingEntity target = event.getLivingEntity();
        if (target.level().isClientSide || event.getAmount() <= 0.0F || target.level().getServer() == null) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker) || attacker == target) {
            return;
        }
        for (AttackEffectRule rule : RULES) {
            if (!rule.attackerFilter().test(attacker)) {
                continue;
            }
            LivingEntity receiver = rule.applyToAttacker() ? attacker : target;
            MobEffectInstance effect = rule.effect().get();
            if (effect != null) {
                receiver.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    private record AttackEffectRule(
            Predicate<LivingEntity> attackerFilter,
            Supplier<MobEffectInstance> effect,
            boolean applyToAttacker
    ) {
    }

    public static final class AttackEffectBuilder {
        private final Supplier<MobEffectInstance> effect;
        private Predicate<LivingEntity> attackerFilter = attacker -> true;
        private boolean applyToAttacker;

        private AttackEffectBuilder(Supplier<MobEffectInstance> effect) {
            this.effect = effect;
        }

        public AttackEffectBuilder withWeapon(Item weapon) {
            this.attackerFilter = attacker -> attacker.getMainHandItem().is(weapon);
            return this;
        }

        public AttackEffectBuilder when(Predicate<LivingEntity> filter) {
            this.attackerFilter = this.attackerFilter.and(filter);
            return this;
        }

        public AttackEffectBuilder toAttacker() {
            this.applyToAttacker = true;
            return this;
        }

        public AttackEffectBuilder toTarget() {
            this.applyToAttacker = false;
            return this;
        }

        public void register() {
            GeminiCombat.register(this.attackerFilter, this.effect, this.applyToAttacker);
        }
    }
}
