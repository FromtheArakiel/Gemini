package dev.xkmc.l2core.init.reg.ench;

import java.util.Optional;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;

public class EnchHelper {
   public static int getLv(ItemStack stack, ResourceKey<Enchantment> key) {
      RegistryLookup<Enchantment> reg = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
      if (reg == null) {
         return 0;
      }

      Optional<Reference<Enchantment>> holder = reg.get(key);
      return holder.<Integer>map(stack::getEnchantmentLevel).orElse(0);
   }
}
