package dev.xkmc.l2core.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
   @Override
   public String toString() {
      CreativeModeTab tab = (CreativeModeTab)this;
      ResourceLocation key = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
      return key == null ? "[Unregistered]" : key.toString();
   }
}
