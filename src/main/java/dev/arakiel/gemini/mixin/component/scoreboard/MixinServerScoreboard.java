package dev.arakiel.gemini.mixin.component.scoreboard;

import com.mojang.datafixers.util.Unit;
import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.api.ComponentSync;
import dev.arakiel.gemini.component.api.ComponentCallbacks;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.scoreboard.GeminiScoreboardComponents;
import dev.arakiel.gemini.component.internal.scoreboard.ScoreboardComponentContainerFactory;
import dev.arakiel.gemini.component.internal.scoreboard.StaticScoreboardComponentPlugin;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerScoreboard.class})
public abstract class MixinServerScoreboard extends MixinScoreboard {
   @Unique
   private static final Supplier<ScoreboardComponentContainerFactory> componentsContainerFactory = StaticScoreboardComponentPlugin.scoreboardComponentsContainerFactory;
   @Shadow
   @Final
   private MinecraftServer server;

   @Override
   public Iterable<ServerPlayer> getRecipientsForComponentSync() {
      MinecraftServer server = this.server;
      return server.getPlayerList() != null ? server.getPlayerList().getPlayers() : List.of();
   }

   public <C extends ComponentSync.AutoSyncedComponent> ComponentUpdatePayload<?> toComponentPacket(
      ComponentKey<? super C> key, boolean required, RegistryFriendlyByteBuf data
   ) {
      return new ComponentUpdatePayload<>(GeminiScoreboardComponents.SCOREBOARD_PACKET_ID, Unit.INSTANCE, required, key.getId(), data);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void initComponents(CallbackInfo ci) {
      this.components = componentsContainerFactory.get().create((Scoreboard)(Object)this, this.server);
   }

   @Inject(
      method = {"onTeamAdded"},
      at = {@At("RETURN")}
   )
   private void syncTeamComponents(PlayerTeam team, CallbackInfo ci) {
      ((ComponentCallbacks.TeamAdd)ComponentCallbacks.TeamAdd.EVENT.invoker()).onTeamAdded(team);
   }
}
