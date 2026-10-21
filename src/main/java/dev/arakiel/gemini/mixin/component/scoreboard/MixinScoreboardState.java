package dev.arakiel.gemini.mixin.component.scoreboard;

import com.llamalad7.mixinextras.sugar.Local;
import dev.arakiel.gemini.component.api.ComponentProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardSaveData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ScoreboardSaveData.class})
public abstract class MixinScoreboardState {
   @Final
   @Shadow
   private Scoreboard scoreboard;

   @Inject(
      method = {"save"},
      at = {@At("RETURN")}
   )
   private void saveComponents(CompoundTag tag, Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
      ((ComponentProvider)(Object)this.scoreboard).getComponentContainer().toTag(tag, registries);
   }

   @Inject(
      method = {"load"},
      at = {@At("RETURN")}
   )
   private void loadComponents(CompoundTag tag, Provider registries, CallbackInfoReturnable<ScoreboardSaveData> cir) {
      ((ComponentProvider)(Object)this.scoreboard).getComponentContainer().fromTag(tag, registries);
   }

   @Inject(
      method = {"loadTeams"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/scores/ScoreboardSaveData;loadTeamPlayers(Lnet/minecraft/world/scores/PlayerTeam;Lnet/minecraft/nbt/ListTag;)V",
         shift = Shift.AFTER
      )}
   )
   private void loadTeamComponents(ListTag nbt, Provider registries, CallbackInfo ci, @Local CompoundTag teamData, @Local PlayerTeam team) {
      ((ComponentProvider)team).getComponentContainer().fromTag(teamData, registries);
   }

   @Inject(
      method = {"saveTeams"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/scores/PlayerTeam;getPlayers()Ljava/util/Collection;"
      )}
   )
   private void saveTeamComponents(Provider registries, CallbackInfoReturnable<ListTag> cir, @Local PlayerTeam team, @Local CompoundTag teamData) {
      ((ComponentProvider)team).getComponentContainer().toTag(teamData, registries);
   }
}
