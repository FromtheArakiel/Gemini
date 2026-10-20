package dev.arakiel.gemini.mixin.component.level;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Lifecycle;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.sync.AutoSyncedComponent;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.level.GeminiLevelComponents;
import dev.arakiel.gemini.component.internal.level.StaticLevelComponentPlugin;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.border.WorldBorder.Settings;
import net.minecraft.world.level.dimension.end.EndDragonFight.Data;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.PrimaryLevelData.SpecialWorldProperty;
import net.minecraft.world.level.timers.TimerQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PrimaryLevelData.class})
@SuppressWarnings({"unchecked", "deprecation"})
public abstract class MixinLevelProperties implements ServerLevelData, ComponentProvider {
   @Unique
   private ComponentContainer components;

   @Inject(
      method = {"<init>(Lnet/minecraft/nbt/CompoundTag;ZLnet/minecraft/core/BlockPos;FJJIIIZIZZZLnet/minecraft/world/level/border/WorldBorder$Settings;IILjava/util/UUID;Ljava/util/Set;Ljava/util/Set;Lnet/minecraft/world/level/timers/TimerQueue;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/level/dimension/end/EndDragonFight$Data;Lnet/minecraft/world/level/LevelSettings;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/storage/PrimaryLevelData$SpecialWorldProperty;Lcom/mojang/serialization/Lifecycle;)V"},
      at = {@At("RETURN")}
   )
   private void initComponents(
      CompoundTag playerData,
      boolean modded,
      BlockPos spawnPos,
      float spawnAngle,
      long time,
      long timeOfDay,
      int version,
      int clearWeatherTime,
      int rainTime,
      boolean raining,
      int thunderTime,
      boolean thundering,
      boolean initialized,
      boolean difficultyLocked,
      Settings worldBorder,
      int wanderingTraderSpawnDelay,
      int wanderingTraderSpawnChance,
      UUID wanderingTraderId,
      Set serverBrands,
      Set removedFeatures,
      TimerQueue scheduledEvents,
      CompoundTag customBossEvents,
      Data dragonFight,
      LevelSettings levelInfo,
      WorldOptions generatorOptions,
      SpecialWorldProperty specialProperty,
      Lifecycle lifecycle,
      CallbackInfo ci
   ) {
      this.components = StaticLevelComponentPlugin.createContainer(this);
   }

   @Inject(
      method = {"setTagData"},
      at = {@At("RETURN")}
   )
   private void writeComponents(RegistryAccess registryManager, CompoundTag data, CompoundTag player, CallbackInfo ci) {
      this.components.toTag(data, registryManager);
   }

   @Nonnull
   @Override
   public ComponentContainer getComponentContainer() {
      return this.components;
   }

   @Override
   public Iterable<ServerPlayer> getRecipientsForComponentSync() {
      throw new UnsupportedOperationException("Please call LevelComponents#sync(MinecraftServer) instead of ComponentKey#sync");
   }

   public <C extends AutoSyncedComponent> ComponentUpdatePayload<?> toComponentPacket(
      ComponentKey<? super C> key, boolean required, RegistryFriendlyByteBuf data
   ) {
      return new ComponentUpdatePayload<>(GeminiLevelComponents.PACKET_ID, Unit.INSTANCE, required, key.getId(), data);
   }
}
