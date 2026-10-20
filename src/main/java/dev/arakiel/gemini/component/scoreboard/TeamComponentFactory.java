package dev.arakiel.gemini.component.scoreboard;

import dev.arakiel.gemini.component.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TeamComponentFactory<C extends Component> extends TeamComponentFactoryV2<C> {
   @Override
   default C createForTeam(PlayerTeam team, Scoreboard scoreboard, @Nullable MinecraftServer server) {
      return this.createForTeam(team);
   }

   @Contract(
      pure = true
   )
   C createForTeam(PlayerTeam var1);
}
