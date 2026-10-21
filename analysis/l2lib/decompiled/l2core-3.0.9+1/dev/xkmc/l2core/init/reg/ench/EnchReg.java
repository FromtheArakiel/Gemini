package dev.xkmc.l2core.init.reg.ench;

import com.mojang.serialization.Codec;
import com.tterrag.registrate.providers.DataProviderInitializer;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.init.L2TagGen;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.Reg;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnchReg {
   private final DeferredRegister<DataComponentType<?>> reg;
   private final DeferredRegister<LegacyEnchantment> legacy;
   private final L2Registrate pvd;
   private final List<EnchReg> parent = new ArrayList<>();
   private final List<EnchVal.Impl> list = new ArrayList<>();

   public static EnchReg of(Reg reg, L2Registrate pvd) {
      return new EnchReg(reg, pvd);
   }

   private EnchReg(Reg reg, L2Registrate pvd) {
      this.reg = reg.make(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);
      this.legacy = reg.make(L2LibReg.ENCH.get());
      this.pvd = pvd;
      pvd.addDataGenerator(L2TagGen.ENCH_TAGS, this::doTagGen);
      DataProviderInitializer init = pvd.getDataGenInitializer();
      init.add(Registries.ENCHANTMENT, this::build);
      init.addDependency(L2TagGen.ENCH_TAGS, ProviderType.DYNAMIC);
   }

   private <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> reg(String id, Codec<T> codec) {
      return this.reg.register(id, () -> DataComponentType.builder().persistent(codec).build());
   }

   public EECVal.Flag unit(String id) {
      return new EECVal.Flag.Impl(this.reg(id, Unit.CODEC));
   }

   public <T> EECVal.Special<T> special(String id, Codec<T> codec) {
      return new EECVal.Special.Impl<>(this.reg(id, codec.listOf()));
   }

   public <T> EECVal<T> eff(String id, Codec<T> codec, LootContextParamSet loot) {
      return new EECVal.Impl<>(this.reg(id, ConditionalEffect.codec(codec, loot).listOf()));
   }

   public EECVal.Num val(String id) {
      return new EECVal.Num.Impl(this.reg(id, ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()));
   }

   private <T extends EnchVal.Impl> T enchBase(String id, String name, String desc, Function<ResourceKey<Enchantment>, T> factory) {
      ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, this.pvd.loc(id));
      this.pvd.addRawLang("enchantment." + this.pvd.getModid() + "." + id, name);
      this.pvd.addRawLang("enchantment." + this.pvd.getModid() + "." + id + ".desc", desc);
      T ans = (T)factory.apply(key);
      this.list.add(ans);
      return ans;
   }

   public EnchVal ench(String id, String name, String desc, UnaryOperator<EnchVal.Builder> cons) {
      return this.enchBase(id, name, desc, key -> new EnchVal.Simple(key, Lazy.of(() -> cons.apply(new EnchVal.Builder(key.location())))));
   }

   public EnchVal.Flag enchFlag(String id, String name, String desc, UnaryOperator<EnchVal.Builder> cons) {
      EECVal.Flag unit = this.unit(id);
      return this.enchBase(
         id,
         name,
         desc,
         key -> new EnchVal.FlagImpl(
            unit, key, Lazy.of(() -> cons.apply(new EnchVal.Builder(key.location()).effect(e -> e.withEffect((DataComponentType)unit.get()))))
         )
      );
   }

   public <T extends LegacyEnchantment> EnchVal.Legacy<T> enchLegacy(
      String id, String name, String desc, UnaryOperator<EnchVal.Builder> cons, Supplier<T> factory
   ) {
      DeferredHolder<LegacyEnchantment, T> unit = this.legacy.register(id, factory);
      return this.enchBase(
         id,
         name,
         desc,
         key -> new EnchVal.LegacyImpl<>(
            key,
            unit,
            Lazy.of(
               () -> cons.apply(
                  new EnchVal.Builder(key.location()).effect(e -> e.withSpecialEffect(L2LibReg.LEGACY.get(), List.of((LegacyEnchantment)unit.get())))
               )
            )
         )
      );
   }

   public void addParent(EnchReg reg) {
      this.parent.add(reg);
   }

   public void build(BootstrapContext<Enchantment> ctx) {
      for (EnchReg e : this.parent) {
         e.build(ctx);
      }

      for (EnchVal.Impl e : this.list) {
         ctx.register(e.id(), ((EnchVal.Builder)e.builder().get()).build(ctx, e.id().location()));
      }
   }

   public void doTagGen(RegistrateTagsProvider<Enchantment> pvd) {
      for (EnchVal.Impl e : this.list) {
         for (TagKey<Enchantment> t : ((EnchVal.Builder)e.builder().get()).tags) {
            pvd.addTag(t).add(e.id());
         }
      }
   }
}
