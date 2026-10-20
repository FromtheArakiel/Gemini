package dev.arakiel.gemini.mixin.component.block;

import dev.arakiel.gemini.component.internal.block.StaticBlockComponentPlugin;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({LevelChunk.class})
public abstract class MixinWorldChunk {
   @Shadow
   @Final
   Level level;

   @ModifyVariable(
      method = {"updateBlockEntityTicker"},
      at = {@At(
         value = "INVOKE_ASSIGN",
         target = "Lnet/minecraft/world/level/block/state/BlockState;getTicker(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/BlockEntityType;)Lnet/minecraft/world/level/block/entity/BlockEntityTicker;"
      )}
   )
   @Nullable
   private <T extends BlockEntity> BlockEntityTicker<T> getBlockEntityTicker(BlockEntityTicker<T> base, T blockEntity) {
      return StaticBlockComponentPlugin.INSTANCE.getComponentTicker(this.level, blockEntity, base);
   }
}
