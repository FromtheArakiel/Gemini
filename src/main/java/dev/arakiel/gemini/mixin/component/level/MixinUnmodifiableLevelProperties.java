package dev.arakiel.gemini.mixin.component.level;

import dev.arakiel.gemini.component.api.ComponentContainer;
import dev.arakiel.gemini.component.api.ComponentProvider;
import javax.annotation.Nonnull;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({DerivedLevelData.class})
public abstract class MixinUnmodifiableLevelProperties implements ComponentProvider {
   @Shadow
   @Final
   private ServerLevelData wrapped;

   @Nonnull
   @Override
   public ComponentContainer getComponentContainer() {
      return this.wrapped.asComponentProvider().getComponentContainer();
   }
}
