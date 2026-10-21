package dev.xkmc.l2core.base.menu.base;

import dev.xkmc.l2core.init.L2LibReg;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public record SpriteManager(ResourceLocation id) {
   public SpriteManager(String modid, String path) {
      this(ResourceLocation.fromNamespaceAndPath(modid, path));
   }

   public MenuLayoutConfig get(RegistryAccess access) {
      return (MenuLayoutConfig)L2LibReg.MENU_LAYOUT.get(access, this.id).value();
   }
}
