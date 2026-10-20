package dev.arakiel.gemini.mixin.component.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import dev.arakiel.gemini.component.internal.world.ComponentPersistentState;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DataFixTypes.class})
public class DataFixTypesMixin {
   @Inject(
      method = {"update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <T> void cancelIfCca(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion, int newVersion, CallbackInfoReturnable<Dynamic<T>> cir) {
      if (ComponentPersistentState.LOADING.get()) {
         cir.setReturnValue(dynamic);
      }
   }
}
