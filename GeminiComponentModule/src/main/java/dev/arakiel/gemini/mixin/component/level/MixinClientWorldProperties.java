package dev.arakiel.gemini.mixin.component.level;

import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.internal.level.StaticLevelComponentPlugin;
import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevelData.class})
public abstract class MixinClientWorldProperties implements WritableLevelData, ComponentProvider {
   @Unique
   private ComponentContainer gemini$components;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void gemini$initComponents(Difficulty difficulty, boolean hardcore, boolean flatWorld, CallbackInfo ci) {
      this.gemini$components = StaticLevelComponentPlugin.createContainer(this);
   }

   @Nonnull
   @Override
   public ComponentContainer gemini$getComponentContainer() {
      return this.gemini$components;
   }
}
