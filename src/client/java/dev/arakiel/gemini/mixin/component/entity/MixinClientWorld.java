package dev.arakiel.gemini.mixin.component.entity;

import dev.arakiel.gemini.component.api.ComponentProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public abstract class MixinClientWorld {
   @Inject(
      method = {"tickNonPassenger"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;tick()V",
         shift = Shift.AFTER
      )}
   )
   private void tick(Entity entity, CallbackInfo ci) {
      ((ComponentProvider)entity).getComponentContainer().tickClientComponents();
   }

   @Inject(
      method = {"tickPassenger"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;rideTick()V",
         shift = Shift.AFTER
      )}
   )
   private void tickRiding(Entity vehicle, Entity passenger, CallbackInfo ci) {
      ((ComponentProvider)passenger).getComponentContainer().tickClientComponents();
   }
}
