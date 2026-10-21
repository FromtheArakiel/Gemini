package dev.arakiel.gemini.mixin.component.chunk;

import dev.arakiel.gemini.component.api.ComponentContainer;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.internal.chunk.StaticChunkComponentPlugin;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChunkAccess.class})
public abstract class MixinChunk implements ComponentProvider {
   @Unique
   private ComponentContainer components;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void initComponents(
      ChunkPos pos,
      UpgradeData upgradeData,
      LevelHeightAccessor heightLimitView,
      Registry<Biome> biome,
      long inhabitedTime,
      LevelChunkSection[] sectionArrayInitializer,
      BlendingData blendingData,
      CallbackInfo ci
   ) {
      this.components = StaticChunkComponentPlugin.createContainer((ChunkAccess)(Object)this);
   }

   @Override
   public ComponentContainer getComponentContainer() {
      return this.components;
   }
}
