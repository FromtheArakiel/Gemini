package dev.arakiel.gemini.mixin.component.scoreboard;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ServerScoreboard.class})
public interface ServerScoreboardAccessor {
   @Accessor("server")
   MinecraftServer getServer();
}
