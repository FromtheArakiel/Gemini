package dev.arakiel.gemini.component.scoreboard;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.internal.base.asm.CalledByAsm;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ScoreboardComponentFactoryV2<C extends Component> {
   @CalledByAsm
   @Contract(
      pure = true
   )
   C createForScoreboard(Scoreboard var1, @Nullable MinecraftServer var2);
}
