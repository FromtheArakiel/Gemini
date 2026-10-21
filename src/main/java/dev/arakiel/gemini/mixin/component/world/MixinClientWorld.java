package dev.arakiel.gemini.mixin.component.world;

import dev.arakiel.gemini.component.ComponentProvider;
import java.util.function.BooleanSupplier;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public abstract class MixinClientWorld {
   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
      ((ComponentProvider)this).getComponentContainer().tickClientComponents();
   }
}
