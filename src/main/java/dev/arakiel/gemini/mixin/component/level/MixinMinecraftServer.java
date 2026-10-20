package dev.arakiel.gemini.mixin.component.level;

import java.util.function.BooleanSupplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftServer.class})
public abstract class MixinMinecraftServer {
   @Shadow
   public abstract WorldData getWorldData();

   @Inject(
      at = {@At("TAIL")},
      method = {"tickServer"}
   )
   private void onEndTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
      this.getWorldData().overworldData().asComponentProvider().getComponentContainer().tickServerComponents();
   }
}
