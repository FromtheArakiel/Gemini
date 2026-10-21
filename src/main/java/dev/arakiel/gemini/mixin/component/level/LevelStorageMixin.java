package dev.arakiel.gemini.mixin.component.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import dev.arakiel.gemini.component.ComponentProvider;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.PrimaryLevelData.SpecialWorldProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LevelStorageSource.class})
@SuppressWarnings({"unchecked", "deprecation"})
public class LevelStorageMixin {
   @WrapOperation(
      method = {"getLevelDataAndDimensions"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/storage/PrimaryLevelData;parse(Lcom/mojang/serialization/Dynamic;Lnet/minecraft/world/level/LevelSettings;Lnet/minecraft/world/level/storage/PrimaryLevelData$SpecialWorldProperty;Lnet/minecraft/world/level/levelgen/WorldOptions;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/world/level/storage/PrimaryLevelData;"
      )}
   )
   private static PrimaryLevelData readComponents(
      Dynamic<?> dynamic,
      LevelSettings info,
      SpecialWorldProperty specialProperty,
      WorldOptions generatorOptions,
      Lifecycle lifecycle,
      Operation<PrimaryLevelData> original,
      @Local(argsOnly = true) Frozen registryManager
   ) {
      PrimaryLevelData props = (PrimaryLevelData)original.call(new Object[]{dynamic, info, specialProperty, generatorOptions, lifecycle});
      ((ComponentProvider)props).getComponentContainer().fromDynamic(dynamic, registryManager);
      return props;
   }
}
