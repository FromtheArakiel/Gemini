package dev.xkmc.l2core.init.reg.registrate;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionBrewing.Builder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public class PotionBuilder {
   private final List<Consumer<Builder>> recipes = new ArrayList<>();
   private final PotionBuilder.TabHolder tab;
   private final L2Registrate reg;

   private PotionBuilder(L2Registrate reg, PotionBuilder.TabHolder tab) {
      this.reg = reg;
      this.tab = tab;
      NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipe);
   }

   public PotionBuilder(L2Registrate reg, PotionBuilder parent) {
      this(reg, parent.tab);
   }

   public PotionBuilder(L2Registrate reg) {
      this(reg, new PotionBuilder.TabHolder(reg.getModid()));
   }

   private void registerBrewingRecipe(RegisterBrewingRecipesEvent event) {
      Builder builder = event.getBuilder();
      this.recipes.forEach(e -> e.accept(builder));
   }

   public void addMix(Holder<Potion> source, ItemLike item, Holder<Potion> potion) {
      this.recipes.add(e -> e.addMix(source, item.asItem(), potion));
   }

   public Holder<Potion> regPotion(String id, String name, Holder<MobEffect> sup, int dur, int amp) {
      SimpleEntry<Potion> ans = this.reg
         .potion(id, RegistrateLangProvider.toEnglishName(name), () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(sup, dur, amp)}));
      this.tab.add(this.reg.getModid(), ans);
      return ans;
   }

   public Holder<Potion> regPotion(String id, String nameId, Holder<MobEffect> sup, Holder<Potion> source, ItemLike item, int dur, int amp) {
      Holder<Potion> potion = this.regPotion(id, nameId, sup, dur, amp);
      this.addMix(source, item, potion);
      return potion;
   }

   public void regPotion2(String id, Holder<MobEffect> sup, ItemLike item, int dur, int durLong) {
      Holder<Potion> potion = this.regPotion(id, id, sup, Potions.AWKWARD, item, dur, 0);
      this.regPotion("long_" + id, id, sup, potion, Items.REDSTONE, durLong, 0);
   }

   public void regPotion3(String id, Holder<MobEffect> sup, ItemLike item, int durStrong, int dur, int durLong, int amp, int ampStrong) {
      Holder<Potion> potion = this.regPotion(id, id, sup, Potions.AWKWARD, item, dur, amp);
      this.regPotion("long_" + id, id, sup, potion, Items.REDSTONE, durLong, amp);
      this.regPotion("strong_" + id, id, sup, potion, Items.GLOWSTONE_DUST, durStrong, ampStrong);
   }

   public void regPotion3(
      String id, Holder<MobEffect> sup, ItemLike item, int durStrong, int dur, int durLong, int amp, int ampStrong, ItemLike longItem, ItemLike strongItem
   ) {
      Holder<Potion> potion = this.regPotion(id, id, sup, Potions.AWKWARD, item, dur, amp);
      this.regPotion("long_" + id, id, sup, potion, longItem, durLong, amp);
      this.regPotion("strong_" + id, id, sup, potion, strongItem, durStrong, ampStrong);
   }

   public void interleave(
      String id,
      Holder<MobEffect> sup,
      int durStrong,
      int dur,
      int durLong,
      int amp,
      int ampStrong,
      ItemLike a,
      Holder<Potion> ap,
      @Nullable Holder<Potion> lap,
      @Nullable Holder<Potion> sap,
      ItemLike b,
      Holder<Potion> bp,
      @Nullable Holder<Potion> lbp,
      @Nullable Holder<Potion> sbp
   ) {
      Holder<Potion> potion = this.regPotion(id, id, sup, dur, amp);
      Holder<Potion> longPotion = this.regPotion("long_" + id, id, sup, potion, Items.REDSTONE, durLong, amp);
      Holder<Potion> strongPotion = this.regPotion("strong_" + id, id, sup, potion, Items.GLOWSTONE_DUST, durStrong, ampStrong);
      this.addMix(ap, a, potion);
      this.addMix(bp, b, potion);
      if (lap != null) {
         this.addMix(lap, a, longPotion);
      }

      if (lbp != null) {
         this.addMix(lbp, b, longPotion);
      }

      if (sap != null) {
         this.addMix(sap, a, strongPotion);
      }

      if (sbp != null) {
         this.addMix(sbp, b, strongPotion);
      }
   }

   public void regTab(ResourceKey<CreativeModeTab> key) {
      this.regTab(key, Items.POTION);
      this.regTab(key, Items.SPLASH_POTION);
      this.regTab(key, Items.LINGERING_POTION);
   }

   private void regTab(ResourceKey<CreativeModeTab> key, Item potion) {
      this.reg
         .modifyCreativeModeTab(key, m -> this.tab.stream().forEach(e -> m.accept(PotionContents.createItemStack(potion, e), TabVisibility.PARENT_TAB_ONLY)));
   }

   public static class TabHolder {
      private final ListMultimap<String, Holder<Potion>> potions = Multimaps.newListMultimap(new LinkedHashMap(), ArrayList::new);
      private final String modid;

      public TabHolder(String modid) {
         this.modid = modid;
      }

      public Stream<Holder<Potion>> stream() {
         List<String> ans = new ArrayList<>();
         ans.add(this.modid);
         TreeSet<String> set = new TreeSet<>(this.potions.keySet());
         set.remove(this.modid);
         ans.addAll(set);
         return ans.stream().flatMap(e -> this.potions.get(e).stream());
      }

      public synchronized void add(String modid, SimpleEntry<Potion> ans) {
         this.potions.put(modid, ans);
      }
   }
}
