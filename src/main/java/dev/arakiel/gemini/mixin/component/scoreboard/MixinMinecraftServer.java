package dev.arakiel.gemini.mixin.component.scoreboard;

import java.util.function.BooleanSupplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftServer.class})
public abstract class MixinMinecraftServer {
   @Shadow
   public abstract ServerScoreboard getScoreboard();

   @Inject(
      at = {@At("TAIL")},
      method = {"tickServer"}
   )
   private void onEndTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
      ServerScoreboard scoreboard = this.getScoreboard();
      scoreboard.asComponentProvider().getComponentContainer().tickServerComponents();

      for (PlayerTeam team : scoreboard.getPlayerTeams()) {
         team.asComponentProvider().getComponentContainer().tickServerComponents();
      }
   }
}
