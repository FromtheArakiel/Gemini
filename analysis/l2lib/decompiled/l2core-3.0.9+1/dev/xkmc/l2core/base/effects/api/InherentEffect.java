package dev.xkmc.l2core.base.effects.api;

import java.util.Set;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

public class InherentEffect extends MobEffect {
   protected InherentEffect(MobEffectCategory category, int color) {
      super(category, color);
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
   }
}
