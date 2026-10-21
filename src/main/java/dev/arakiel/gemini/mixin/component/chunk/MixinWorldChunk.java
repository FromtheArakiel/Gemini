package dev.arakiel.gemini.mixin.component.chunk;

import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.api.ComponentSync;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.chunk.GeminiChunkComponents;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.LevelChunk.PostLoadProcessor;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelChunk.class})
@SuppressWarnings({"unchecked", "deprecation"})
public abstract class MixinWorldChunk extends ChunkAccess implements ComponentProvider {
   public MixinWorldChunk(
      ChunkPos pos,
      UpgradeData upgradeData,
      LevelHeightAccessor heightLimitView,
      Registry<Biome> biome,
      long inhabitedTime,
      @Nullable LevelChunkSection[] sectionArrayInitializer,
      @Nullable BlendingData blendingData
   ) {
      super(pos, upgradeData, heightLimitView, biome, inhabitedTime, sectionArrayInitializer, blendingData);
   }

   @Shadow
   public abstract Level getLevel();

   @Override
   public Iterable<ServerPlayer> getRecipientsForComponentSync() {
      return (Iterable<ServerPlayer>)(!this.getLevel().isClientSide() ? PlayerLookup.tracking((ServerLevel)(Object)this.getLevel(), this.getPos()) : List.of());
   }

   @Nullable
   public <C extends ComponentSync.AutoSyncedComponent> ComponentUpdatePayload<?> toComponentPacket(
      ComponentKey<? super C> key, boolean required, RegistryFriendlyByteBuf data
   ) {
      return new ComponentUpdatePayload<>(GeminiChunkComponents.PACKET_ID, this.getPos(), required, key.getId(), data);
   }

   @Inject(
      method = {"<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V"},
      at = {@At("RETURN")}
   )
   private void copyFromProto(ServerLevel world, ProtoChunk proto, PostLoadProcessor entityLoader, CallbackInfo ci) {
      this.getComponentContainer().copyFrom(proto.asComponentProvider().getComponentContainer(), world.registryAccess());
   }
}
