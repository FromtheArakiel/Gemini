package dev.xkmc.l2core.init.reg.ench;

import dev.xkmc.l2core.init.reg.simple.Val;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.mutable.MutableFloat;

public interface EECVal<T> extends Val<DataComponentType<List<ConditionalEffect<T>>>> {
   static <T> void apply(List<ConditionalEffect<T>> list, LootContext ctx, Consumer<T> cons) {
      for (ConditionalEffect<T> e : list) {
         if (e.matches(ctx)) {
            cons.accept((T)e.effect());
         }
      }
   }

   static void iterateItem(ItemStack stack, BiConsumer<Holder<Enchantment>, Integer> cons) {
      ItemEnchantments enchs = (ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      RegistryLookup<Enchantment> lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
      if (lookup != null) {
         enchs = stack.getAllEnchantments(lookup);
      }

      for (Entry<Holder<Enchantment>> entry : enchs.entrySet()) {
         cons.accept((Holder<Enchantment>)entry.getKey(), entry.getIntValue());
      }
   }

   static LootContext itemContext(ServerLevel level, int lv, ItemStack stack) {
      LootParams lootparams = new Builder(level)
         .withParameter(LootContextParams.TOOL, stack)
         .withParameter(LootContextParams.ENCHANTMENT_LEVEL, lv)
         .create(LootContextParamSets.ENCHANTED_ITEM);
      return new net.minecraft.world.level.storage.loot.LootContext.Builder(lootparams).create(Optional.empty());
   }

   interface Flag extends Val<DataComponentType<Unit>> {
      default boolean isOn(ItemStack stack) {
         return EnchantmentHelper.has(stack, (DataComponentType)this.get());
      }

      record Impl<T>(DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> val) implements EECVal.Flag {
         public DataComponentType<Unit> get() {
            return (DataComponentType<Unit>)this.val.get();
         }

         @Override
         public ResourceLocation id() {
            return this.val.getId();
         }
      }
   }

   record Impl<T>(DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<T>>>> val) implements EECVal<T> {
      public DataComponentType<List<ConditionalEffect<T>>> get() {
         return (DataComponentType<List<ConditionalEffect<T>>>)this.val.get();
      }

      @Override
      public ResourceLocation id() {
         return this.val.getId();
      }
   }

   interface Num extends EECVal<EnchantmentValueEffect> {
      default float modify(ServerLevel level, ItemStack stack, float f) {
         MutableFloat val = new MutableFloat(f);
         EECVal.iterateItem(
            stack,
            (ench, lv) -> EECVal.apply(
               ((Enchantment)ench.value()).getEffects((DataComponentType)this.get()),
               EECVal.itemContext(level, lv, stack),
               eff -> val.setValue(eff.process(lv, level.getRandom(), val.getValue()))
            )
         );
         return val.getValue();
      }

      record Impl(DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> val) implements EECVal.Num {
         public DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> get() {
            return (DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>)this.val.get();
         }

         @Override
         public ResourceLocation id() {
            return this.val.getId();
         }
      }
   }

   interface Special<T> extends Val<DataComponentType<List<T>>> {
      record Impl<T>(DeferredHolder<DataComponentType<?>, DataComponentType<List<T>>> val) implements EECVal.Special<T> {
         public DataComponentType<List<T>> get() {
            return (DataComponentType<List<T>>)this.val.get();
         }

         @Override
         public ResourceLocation id() {
            return this.val.getId();
         }
      }
   }
}
