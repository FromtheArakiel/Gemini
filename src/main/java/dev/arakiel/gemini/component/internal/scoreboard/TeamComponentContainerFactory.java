package dev.arakiel.gemini.component.internal.scoreboard;

import dev.arakiel.gemini.component.api.ComponentContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TeamComponentContainerFactory {
   ComponentContainer create(PlayerTeam var1, Scoreboard var2, @Nullable MinecraftServer var3);
}
