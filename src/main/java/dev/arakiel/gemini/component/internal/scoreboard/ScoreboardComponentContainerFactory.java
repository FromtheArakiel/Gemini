package dev.arakiel.gemini.component.internal.scoreboard;

import dev.arakiel.gemini.component.ComponentContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ScoreboardComponentContainerFactory {
   ComponentContainer create(Scoreboard var1, @Nullable MinecraftServer var2);
}
