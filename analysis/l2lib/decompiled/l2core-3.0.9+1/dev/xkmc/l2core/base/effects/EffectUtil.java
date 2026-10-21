package dev.xkmc.l2core.base.effects;

import dev.xkmc.l2core.base.effects.api.ForceEffect;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;

public class EffectUtil {
   private static final Set<EntityType<?>> INVALID = new HashSet<>();

   private static synchronized void forceAddEffect(LivingEntity e, MobEffectInstance ins, @Nullable Entity source) {
      if (!INVALID.contains(e.getType())) {
         MobEffectInstance old = (MobEffectInstance)e.activeEffects.get(ins.getEffect());
         ForceAddEffectEvent event = new ForceAddEffectEvent(e, ins);
         NeoForge.EVENT_BUS.post(event);
         if (!event.isCanceled()) {
            NeoForge.EVENT_BUS.post(new Added(e, old, ins, source));
            if (old == null) {
               try {
                  e.activeEffects.put(ins.getEffect(), ins);
               } catch (Exception ignored) {
                  INVALID.add(e.getType());
                  return;
               }

               e.onEffectAdded(ins, source);
               ins.onEffectAdded(e);
            } else if (old.update(ins)) {
               e.onEffectUpdated(old, true, source);
            }

            ins.onEffectStarted(e);
         }
      }
   }

   public static void addEffect(LivingEntity entity, MobEffectInstance ins, @Nullable Entity source) {
      ins = new MobEffectInstance(ins.getEffect(), ins.getDuration(), ins.getAmplifier(), ins.isAmbient(), ins.isVisible(), ins.showIcon());
      if (ins.getEffect() instanceof ForceEffect) {
         forceAddEffect(entity, ins, source);
      } else if (((MobEffect)ins.getEffect().value()).isInstantenous()) {
         ((MobEffect)ins.getEffect().value()).applyInstantenousEffect(null, null, entity, ins.getAmplifier(), 1.0);
      } else {
         entity.addEffect(ins, source);
      }
   }

   public static void refreshEffect(LivingEntity entity, MobEffectInstance ins, Entity source) {
      if (ins.duration < 40) {
         ins.duration = 40;
      }

      MobEffectInstance cur = entity.getEffect(ins.getEffect());
      if (cur == null || cur.getAmplifier() < ins.getAmplifier() || cur.getAmplifier() == ins.getAmplifier() && cur.getDuration() < ins.getDuration() / 2) {
         addEffect(entity, ins, source);
      }
   }

   public static void removeEffect(LivingEntity entity, Predicate<MobEffectInstance> pred) {
      Iterator<MobEffectInstance> itr = entity.activeEffects.values().iterator();

      while (itr.hasNext()) {
         MobEffectInstance effect = itr.next();
         if (pred.test(effect) && EventHooks.onEffectRemoved(entity, effect, null)) {
            entity.onEffectRemoved(effect);
            itr.remove();
            entity.effectsDirty = true;
         }
      }
   }
}
