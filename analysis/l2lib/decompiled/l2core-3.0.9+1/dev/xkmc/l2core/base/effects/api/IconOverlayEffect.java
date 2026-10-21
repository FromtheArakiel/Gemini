package dev.xkmc.l2core.base.effects.api;

import java.util.function.Consumer;
import net.minecraft.world.entity.LivingEntity;

public interface IconOverlayEffect extends ClientRenderEffect {
   @Override
   default void render(LivingEntity entity, int lv, Consumer<DelayedEntityRender> adder) {
      adder.accept(this.getIcon(entity, lv));
   }

   DelayedEntityRender getIcon(LivingEntity var1, int var2);
}
