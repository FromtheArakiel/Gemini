package dev.arakiel.gemini.mixin.component.item;

import com.mojang.serialization.Dynamic;
import dev.arakiel.gemini.component.internal.item.StaticItemComponentPlugin;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix.ItemStackData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemStackComponentizationFix.class})
public abstract class MixinItemStackComponentizationFix {
   @Inject(
      method = {"fixItemStack"},
      at = {@At("RETURN")}
   )
   private static void fixStack(ItemStackData data, Dynamic<?> dynamic, CallbackInfo ci) {
      StaticItemComponentPlugin.INSTANCE.migrate(data);
   }
}
