package dev.xkmc.l2core.init.reg.ench;

import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.util.Wrappers;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

public class LegacyEnchantment {
   @Nullable
   public static <T> T firstOf(Holder<Enchantment> ench, EECVal.Special<T> comp) {
      Iterator var2 = ((Enchantment)ench.value()).getEffects(comp.get()).iterator();
      if (var2.hasNext()) {
         T e = (T)var2.next();
         return (T)Wrappers.cast(e);
      } else {
         return null;
      }
   }

   @Nullable
   public static <T> T firstOf(Holder<Enchantment> ench, Class<T> cls) {
      for (LegacyEnchantment e : ((Enchantment)ench.value()).getEffects(L2LibReg.LEGACY.get())) {
         if (cls.isInstance(e)) {
            return (T)Wrappers.cast(e);
         }
      }

      return null;
   }

   public static <T> Optional<T> findFirst(Holder<Enchantment> ench, Class<T> cls) {
      return Optional.ofNullable(firstOf(ench, cls));
   }

   public static <T> List<EnchHolder<T>> findAll(ItemStack stack, Class<T> cls, boolean intrinsic) {
      RegistryLookup<Enchantment> reg = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
      if (reg == null) {
         return List.of();
      }

      List<EnchHolder<T>> ans = new ArrayList<>();
      ItemEnchantments enchs = intrinsic
         ? (ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
         : stack.getAllEnchantments(reg);

      for (Entry<Holder<Enchantment>> e : enchs.entrySet()) {
         T t = firstOf((Holder<Enchantment>)e.getKey(), cls);
         if (t != null) {
            ans.add(new EnchHolder<>((Holder<Enchantment>)e.getKey(), t, e.getIntValue()));
         }
      }

      return ans;
   }

   public static <T> Map<T, Integer> accumulateOnEntity(LivingEntity le, Class<T> cls, boolean intrinsic) {
      LinkedHashMap<T, Integer> map = new LinkedHashMap<>();

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = le.getItemBySlot(slot);
         if (!stack.isEmpty() && stack.isEnchanted()) {
            for (EnchHolder<T> e : findAll(stack, cls, intrinsic)) {
               if (((Enchantment)e.holder().value()).matchingSlot(slot)) {
                  map.compute(e.val(), (k, v) -> (v == null ? 0 : v) + e.lv());
               }
            }
         }
      }

      return map;
   }
}
