package dev.arakiel.gemini.mixin.component.scoreboard;

import dev.arakiel.gemini.component.scoreboard.ScoreboardSyncCallback;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerList.class})
public abstract class MixinPlayerManager {
   @Inject(
      method = {"updateEntireScoreboard"},
      at = {@At("RETURN")}
   )
   private void syncTeamComponents(ServerScoreboard scoreboard, ServerPlayer player, CallbackInfo ci) {
      ((ScoreboardSyncCallback)ScoreboardSyncCallback.EVENT.invoker()).onScoreboardSync(player, scoreboard);
   }
}
