package dev.arakiel.gemini.mixin.component.scoreboard;

import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.internal.scoreboard.StaticScoreboardComponentPlugin;
import javax.annotation.Nonnull;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Scoreboard.class})
public abstract class MixinScoreboard implements ComponentProvider {
   @Unique
   protected ComponentContainer components;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void initComponents(CallbackInfo ci) {
      if (!((Object)this instanceof ServerScoreboard)) {
         this.components = StaticScoreboardComponentPlugin.scoreboardComponentsContainerFactory.get().create((Scoreboard)(Object)this, null);
      }
   }

   @Nonnull
   @Override
   public ComponentContainer getComponentContainer() {
      return this.components;
   }
}
