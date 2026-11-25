package dev.arakiel.gemini.combat.internal;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.LivingHurtEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class GeminiCombatImpl {
    private static final List<AttackEffectRule> RULES = new ArrayList<>();
    private static boolean initialized;

    private GeminiCombatImpl() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        GeminiEvents.listen(LivingHurtEvent.class, GeminiCombatImpl::applyRules);
    }

    public static void register(
            Predicate<LivingEntity> attackerFilter,
            Supplier<MobEffectInstance> effect,
            boolean applyToAttacker
    ) {
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
}
