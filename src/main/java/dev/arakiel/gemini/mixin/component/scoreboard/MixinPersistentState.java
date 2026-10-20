package dev.arakiel.gemini.mixin.component.scoreboard;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.ScoreboardSaveData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SavedData.class})
public abstract class MixinPersistentState {
   @Inject(
      method = {"isDirty"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void forceDirty(CallbackInfoReturnable<Boolean> cir) {
      if (!cir.getReturnValueZ() && (Object)this instanceof ScoreboardSaveData) {
         cir.setReturnValue(true);
      }
   }
}
