package dev.arakiel.gemini.mixin.component.world;

import dev.arakiel.gemini.component.api.ComponentContainer;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.internal.world.GeminiWorldComponents;
import javax.annotation.Nonnull;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Level.class})
public abstract class MixinWorld implements ComponentProvider {
   @Unique
   protected ComponentContainer components;

   @Inject(
      method = {"<init>*"},
      at = {@At("RETURN")}
   )
   private void initComponents(CallbackInfo ci) {
      this.components = GeminiWorldComponents.createComponents((Level)(Object)this);
   }

   @Nonnull
   @Override
   public ComponentContainer getComponentContainer() {
      return this.components;
   }
}
