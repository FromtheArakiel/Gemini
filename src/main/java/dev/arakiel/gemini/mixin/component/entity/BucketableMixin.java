package dev.arakiel.gemini.mixin.component.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bucketable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Bucketable.class})
public interface BucketableMixin {
   @Inject(
      method = {"method_57302"},
      at = {@At("RETURN")}
   )
   private static void writeComponentsToStack(Mob mobEntity, CompoundTag nbtCompound, CallbackInfo ci) {
      mobEntity.asComponentProvider().getComponentContainer().toTag(nbtCompound, mobEntity.registryAccess());
   }

   @Inject(
      method = {"loadDefaultDataFromBucketTag(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/nbt/CompoundTag;)V"},
      at = {@At("RETURN")}
   )
   private static void readComponentsFromStack(Mob entity, CompoundTag nbt, CallbackInfo ci) {
      entity.asComponentProvider().getComponentContainer().fromTag(nbt, entity.registryAccess());
   }
}
