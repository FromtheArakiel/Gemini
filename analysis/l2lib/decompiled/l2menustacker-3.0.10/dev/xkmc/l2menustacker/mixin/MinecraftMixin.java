package dev.xkmc.l2menustacker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.l2menustacker.init.MouseCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
   @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;releaseMouse()V"), method = "setScreen")
   public void l2menustacker$retainMouse(MouseHandler instance, Operation<Void> original) {
      original.call(new Object[]{instance});
      MouseCache.onReleaseMouse();
   }
}
