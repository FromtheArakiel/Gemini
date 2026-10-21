package dev.xkmc.l2core.init.reg.ench;

import com.tterrag.registrate.providers.RegistrateProvider;
import dev.xkmc.l2core.init.L2LibReg;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment.Cost;
import net.minecraft.world.item.enchantment.Enchantment.EnchantmentDefinition;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public interface EnchVal {
   ResourceKey<Enchantment> id();

   Holder<Enchantment> datagenDirect();

   default Holder<Enchantment> datagenDirect(RegistrateProvider pvd) {
      return this.datagenDirect();
   }

   default Optional<Holder<Enchantment>> safeHolder() {
      return Optional.ofNullable(CommonHooks.resolveLookup(Registries.ENCHANTMENT)).flatMap(e -> e.get(this.id()));
   }

   default Holder<Enchantment> holder() {
      return this.safeHolder().orElseThrow();
   }

   default int getLv(ItemStack stack) {
      return this.safeHolder().<Integer>map(stack::getEnchantmentLevel).orElse(0);
   }

   default int getLvIntrinsic(ItemStack stack) {
      return this.safeHolder().map(e -> ((ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)).getLevel(e)).orElse(0);
   }

   class Builder {
      private HolderSetBuilder<Item> supported;
      private HolderSetBuilder<Item> primary;
      private Cost min;
      private Cost max;
      @Nullable
      private EquipmentSlotGroup group;
      private int weight = 1;
      private int maxLevel = 1;
      private int anvilCost = 1;
      @Nullable
      private HolderSetBuilder<Enchantment> exclude;
      private final List<Consumer<net.minecraft.world.item.enchantment.Enchantment.Builder>> effects = new ArrayList<>();
      public final ResourceLocation id;
      final List<TagKey<Enchantment>> tags = new ArrayList<>();
      private Enchantment cache;

      Builder(ResourceLocation id) {
         this.id = id;
         this.supported = this.primary = new HolderSetBuilder.Simple<>(Items.ENCHANTABLES);
         this.min = this.max = new Cost(10, 5);
         this.group = null;
      }

      public EnchVal.Builder transform(UnaryOperator<EnchVal.Builder> func) {
         return func.apply(this);
      }

      public EnchVal.Builder items(TagKey<Item> items) {
         return this.items(items, items);
      }

      public EnchVal.Builder items(TagKey<Item> supported, TagKey<Item> primary) {
         this.supported = new HolderSetBuilder.Simple<>(supported);
         this.primary = new HolderSetBuilder.Simple<>(primary);
         return this;
      }

      public EnchVal.Builder items(HolderSetBuilder<Item> items) {
         return this.items(items, items);
      }

      public EnchVal.Builder items(HolderSetBuilder<Item> supported, HolderSetBuilder<Item> primary) {
         this.supported = supported;
         this.primary = primary;
         return this;
      }

      public EnchVal.Builder cost(int base, int slope, int range) {
         this.min = new Cost(base, slope);
         this.max = new Cost(base + range, slope);
         return this;
      }

      public EnchVal.Builder weight(int weight) {
         this.weight = weight;
         return this;
      }

      public EnchVal.Builder anvilCost(int cost) {
         this.anvilCost = cost;
         return this;
      }

      public EnchVal.Builder maxLevel(int lv) {
         this.maxLevel = lv;
         return this;
      }

      public EnchVal.Builder group(EquipmentSlotGroup group) {
         this.group = group;
         return this;
      }

      public EnchVal.Builder exclusive(TagKey<Enchantment> tag) {
         this.exclude = HolderSetBuilder.tag(tag);
         return this;
      }

      public EnchVal.Builder exclusive(ResourceKey<Enchantment> key) {
         this.exclude = HolderSetBuilder.direct(key);
         return this;
      }

      public EnchVal.Builder exclusive(HolderSetBuilder<Enchantment> set) {
         this.exclude = set;
         return this;
      }

      @SafeVarargs
      public final EnchVal.Builder tags(TagKey<Enchantment>... tags) {
         this.tags.addAll(List.of(tags));
         return this;
      }

      public EnchVal.Builder effect(Consumer<net.minecraft.world.item.enchantment.Enchantment.Builder> effect) {
         this.effects.add(effect);
         return this;
      }

      public <T> EnchVal.Builder special(EECVal.Special<T> type, T val) {
         this.effects.add(e -> e.withSpecialEffect(type.get(), List.of(val)));
         return this;
      }

      public EnchVal.Builder color(EnchColor color) {
         return this.special(L2LibReg.COLOR, color);
      }

      Enchantment build(BootstrapContext<Enchantment> ctx, ResourceLocation id) {
         RegistryLookup<Item> items = (RegistryLookup<Item>)ctx.registryLookup(Registries.ITEM).orElseThrow();
         HolderGetter<Enchantment> enchs = ctx.lookup(Registries.ENCHANTMENT);
         FakeRegistryLookup<Item> fakeItem = new FakeRegistryLookup<>(Registries.ITEM);
         FakeRegistryLookup<Enchantment> fakeEnch = new FakeRegistryLookup<>(Registries.ENCHANTMENT);
         net.minecraft.world.item.enchantment.Enchantment.Builder ans = Enchantment.enchantment(
            new EnchantmentDefinition(
               this.supported.build(fakeItem, items),
               Optional.of(this.primary.build(fakeItem, items)),
               this.weight,
               this.maxLevel,
               this.min,
               this.max,
               this.anvilCost,
               this.group == null ? List.of() : List.of(this.group)
            )
         );
         if (this.exclude != null) {
            ans.exclusiveWith(this.exclude.build(fakeEnch, enchs));
         }

         for (Consumer<net.minecraft.world.item.enchantment.Enchantment.Builder> e : this.effects) {
            e.accept(ans);
         }

         this.cache = ans.build(id);
         return this.cache;
      }
   }

   interface Flag extends EnchVal {
      EECVal.Flag unit();
   }

   record FlagImpl(EECVal.Flag unit, ResourceKey<Enchantment> id, Lazy<EnchVal.Builder> builder) implements EnchVal.Flag, EnchVal.Impl {
   }

   interface Impl extends EnchVal {
      Lazy<EnchVal.Builder> builder();

      @Override
      default Holder<Enchantment> datagenDirect() {
         Enchantment val = ((EnchVal.Builder)this.builder().get()).cache;
         if (val == null) {
            throw new IllegalStateException("Enchantment is not built yet");
         } else {
            return new DataGenHolder<>(this.id(), val);
         }
      }
   }

   interface Legacy<T extends LegacyEnchantment> extends EnchVal {
      DeferredHolder<LegacyEnchantment, T> legacy();
   }

   record LegacyImpl<T extends LegacyEnchantment>(ResourceKey<Enchantment> id, DeferredHolder<LegacyEnchantment, T> legacy, Lazy<EnchVal.Builder> builder)
      implements EnchVal.Legacy<T>,
      EnchVal.Impl {
   }

   record Simple(ResourceKey<Enchantment> id, Lazy<EnchVal.Builder> builder) implements EnchVal.Impl {
   }
}
