package dev.arakiel.gemini.mixin.component.scoreboard;

import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.ComponentSync;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.scoreboard.GeminiScoreboardComponents;
import dev.arakiel.gemini.component.internal.scoreboard.StaticScoreboardComponentPlugin;
import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
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

@Mixin({PlayerTeam.class})
public abstract class MixinTeam implements ComponentProvider {
   @Shadow
   @Final
   private Scoreboard scoreboard;
   @Unique
   private ComponentContainer components;

   @Shadow
   public abstract String getName();

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void initComponents(CallbackInfo ci) {
      this.components = StaticScoreboardComponentPlugin.teamComponentsContainerFactory
         .get()
         .create((PlayerTeam)(Object)this, this.scoreboard, this.scoreboard instanceof ServerScoreboardAccessor acc ? acc.getServer() : null);
   }

   @Nonnull
   @Override
   public ComponentContainer getComponentContainer() {
      return this.components;
   }

   @Override
   public Iterable<ServerPlayer> getRecipientsForComponentSync() {
      return this.scoreboard.asComponentProvider().getRecipientsForComponentSync();
   }

   public <C extends ComponentSync.AutoSyncedComponent> ComponentUpdatePayload<?> toComponentPacket(
      ComponentKey<? super C> key, boolean required, RegistryFriendlyByteBuf data
   ) {
      return new ComponentUpdatePayload<>(GeminiScoreboardComponents.TEAM_PACKET_ID, this.getName(), required, key.getId(), data);
   }
}
