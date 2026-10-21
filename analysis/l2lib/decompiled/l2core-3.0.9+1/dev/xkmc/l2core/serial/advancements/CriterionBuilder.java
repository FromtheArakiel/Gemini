package dev.xkmc.l2core.serial.advancements;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;

public class CriterionBuilder implements IAdvBuilder {
   private final CriterionBuilder.RequirementsStrategy req;
   private final List<Pair<String, Criterion<?>>> list = new ArrayList<>();

   @Deprecated
   public static CriterionBuilder none() {
      return one(TriggerInstance.hasItems(new Builder[]{Builder.item()}));
   }

   public static CriterionBuilder item(Item item) {
      return one(TriggerInstance.hasItems(new ItemLike[]{item}));
   }

   public static CriterionBuilder item(TagKey<Item> item, DataComponentPredicate tag) {
      return one(TriggerInstance.hasItems(new ItemPredicate[]{Builder.item().of(item).hasComponents(tag).build()}));
   }

   public static CriterionBuilder item(ItemLike item, DataComponentPredicate tag) {
      return one(TriggerInstance.hasItems(new ItemPredicate[]{Builder.item().of(new ItemLike[]{item}).hasComponents(tag).build()}));
   }

   public static CriterionBuilder items(Item... item) {
      return one(TriggerInstance.hasItems(new ItemPredicate[]{Builder.item().of(item).build()}));
   }

   public static CriterionBuilder item(TagKey<Item> item) {
      return one(TriggerInstance.hasItems(new ItemPredicate[]{Builder.item().of(item).build()}));
   }

   public static CriterionBuilder book(Holder<Enchantment> enchantment) {
      return one(
         TriggerInstance.hasItems(
            new Builder[]{
               Builder.item()
                  .withSubPredicate(
                     ItemSubPredicates.STORED_ENCHANTMENTS,
                     ItemEnchantmentsPredicate.storedEnchantments(List.of(new EnchantmentPredicate(enchantment, Ints.ANY)))
                  )
            }
         )
      );
   }

   public static CriterionBuilder enchanted(Holder<Enchantment> enchantment) {
      return one(
         TriggerInstance.hasItems(
            new Builder[]{
               Builder.item()
                  .withSubPredicate(
                     ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(enchantment, Ints.ANY)))
                  )
            }
         )
      );
   }

   public static CriterionBuilder enchanted(ItemLike item, Holder<Enchantment> enchantment) {
      return one(
         TriggerInstance.hasItems(
            new Builder[]{
               Builder.item()
                  .of(new ItemLike[]{item})
                  .withSubPredicate(
                     ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(enchantment, Ints.ANY)))
                  )
            }
         )
      );
   }

   public static CriterionBuilder enchanted(TagKey<Item> item, Holder<Enchantment> enchantment) {
      return one(
         TriggerInstance.hasItems(
            new Builder[]{
               Builder.item()
                  .of(item)
                  .withSubPredicate(
                     ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(enchantment, Ints.ANY)))
                  )
            }
         )
      );
   }

   public static CriterionBuilder player(PlayerTrigger trigger) {
      return one(trigger.createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty())));
   }

   public static CriterionBuilder one(Criterion<?> instance) {
      return new CriterionBuilder(CriterionBuilder.RequirementsStrategy.AND).add(instance);
   }

   public static CriterionBuilder and() {
      return new CriterionBuilder(CriterionBuilder.RequirementsStrategy.AND);
   }

   public static CriterionBuilder or() {
      return new CriterionBuilder(CriterionBuilder.RequirementsStrategy.OR);
   }

   private CriterionBuilder(CriterionBuilder.RequirementsStrategy req) {
      this.req = req;
   }

   public CriterionBuilder add(Criterion<?> instance) {
      return this.add(this.list.size() + "", instance);
   }

   public CriterionBuilder add(String str, Criterion<?> instance) {
      this.list.add(Pair.of(str, instance));
      return this;
   }

   @Override
   public void onBuild(String id, net.minecraft.advancements.Advancement.Builder builder, List<ICondition> conditions) {
      if (this.list.size() > 1) {
         List<String> ids = this.list.stream().<String>map(Pair::getFirst).toList();
         builder.requirements(this.req == CriterionBuilder.RequirementsStrategy.AND ? AdvancementRequirements.allOf(ids) : AdvancementRequirements.anyOf(ids));
      }

      for (Pair<String, Criterion<?>> c : this.list) {
         builder.addCriterion((String)c.getFirst(), (Criterion)c.getSecond());
      }
   }

   public enum RequirementsStrategy {
      AND,
      OR;
   }
}
