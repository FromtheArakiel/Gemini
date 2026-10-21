package dev.xkmc.l2core.serial.loot;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.providers.loot.RegistrateEntityLootTables;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.advancements.critereon.StatePropertiesPredicate.PropertyMatcher;
import net.minecraft.advancements.critereon.StatePropertiesPredicate.RangedMatcher;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public record LootHelper(Provider pvd) {
   public LootHelper(RegistrateBlockLootTables pvd) {
      this(pvd.getRegistries());
   }

   public LootHelper(RegistrateEntityLootTables pvd) {
      this(pvd.getRegistries());
   }

   public <T> Holder<T> resolve(ResourceKey<T> key) {
      return this.pvd.holderOrThrow(key);
   }

   public Builder<?> item(Item item) {
      return LootItem.lootTableItem(item);
   }

   public Builder<?> item(Item item, int count) {
      return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)));
   }

   public Builder<?> item(Item item, int min, int max) {
      return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
   }

   public EnchantmentPredicate hasEnch(ResourceKey<Enchantment> enchant, int min) {
      return new EnchantmentPredicate(this.resolve(enchant), Ints.atLeast(min));
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder toolHasEnch(ResourceKey<Enchantment> enchant, int min) {
      return MatchTool.toolMatches(
         net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
            .withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(this.hasEnch(enchant, min))))
      );
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder silk() {
      return this.toolHasEnch(Enchantments.SILK_TOUCH, 1);
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder intState(Block block, Property<Integer> prop, int val) {
      return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
         .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(prop, val));
   }

   public <T extends Comparable<T> & StringRepresentable> net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder enumState(
      Block block, Property<T> prop, T val
   ) {
      return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
         .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(prop, val));
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder intRangeState(Block block, Property<Integer> prop, int min, int max) {
      return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
         .setProperties(
            (net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder)Util.make(
               net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties(),
               e -> e.matchers.add(new PropertyMatcher(prop.getName(), new RangedMatcher(Optional.of(min + ""), Optional.of(max + ""))))
            )
         );
   }

   public net.minecraft.world.level.storage.loot.functions.LootItemFunction.Builder fortuneCount(int factor) {
      return ApplyBonusCount.addUniformBonusCount(this.resolve(Enchantments.FORTUNE), factor);
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder fortuneChance(int... denominators) {
      float[] fracs = new float[denominators.length];

      for (int i = 0; i < fracs.length; i++) {
         fracs[i] = 1.0F / denominators[i];
      }

      return BonusLevelTableCondition.bonusLevelFlatChance(this.resolve(Enchantments.FORTUNE), fracs);
   }

   public net.minecraft.world.level.storage.loot.functions.LootItemFunction.Builder fortuneBin() {
      return this.fortuneBin(0.5714286F, 3);
   }

   public net.minecraft.world.level.storage.loot.functions.LootItemFunction.Builder fortuneBin(float chance, int count) {
      return ApplyBonusCount.addBonusBinomialDistributionCount(this.resolve(Enchantments.FORTUNE), chance, count);
   }

   public net.minecraft.world.level.storage.loot.functions.LootItemFunction.Builder lootCount(float factor) {
      return EnchantedCountIncreaseFunction.lootingMultiplier(this.pvd, UniformGenerator.between(0.0F, factor));
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder lootChance(float base, float slope) {
      return LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.pvd, base, slope);
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder fire(boolean fire) {
      return LootItemEntityPropertyCondition.hasProperties(
         EntityTarget.THIS,
         net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
            .flags(net.minecraft.advancements.critereon.EntityFlagsPredicate.Builder.flags().setOnFire(fire))
            .build()
      );
   }

   public net.minecraft.world.level.storage.loot.functions.LootItemFunction.Builder smelt() {
      return SmeltItemFunction.smelted().when(this.fire(true));
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder damage(TagKey<DamageType> tag) {
      return DamageSourceCondition.hasDamageSource(net.minecraft.advancements.critereon.DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(tag)));
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder entity(EntityType<?> type) {
      return LootItemEntityPropertyCondition.hasProperties(
         EntityTarget.THIS, net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(type))
      );
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder entity(TagKey<EntityType<?>> tag) {
      return LootItemEntityPropertyCondition.hasProperties(
         EntityTarget.THIS, net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(tag))
      );
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder killer(EntityType<?> type) {
      return LootItemEntityPropertyCondition.hasProperties(
         EntityTarget.ATTACKER, net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(type))
      );
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder killer(TagKey<EntityType<?>> tag) {
      return LootItemEntityPropertyCondition.hasProperties(
         EntityTarget.ATTACKER, net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(tag))
      );
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder killerItem(EquipmentSlot slot, Item item) {
      return LootItemEntityPropertyCondition.hasProperties(
         EntityTarget.ATTACKER,
         net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
            .equipment(this.slot(slot, net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(new ItemLike[]{item})).build())
            .build()
      );
   }

   public net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder killerItem(EquipmentSlot slot, TagKey<Item> item) {
      return LootItemEntityPropertyCondition.hasProperties(
         EntityTarget.ATTACKER,
         net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
            .equipment(this.slot(slot, net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(item)).build())
            .build()
      );
   }

   private net.minecraft.advancements.critereon.EntityEquipmentPredicate.Builder slot(
      EquipmentSlot slot, net.minecraft.advancements.critereon.ItemPredicate.Builder item
   ) {
      net.minecraft.advancements.critereon.EntityEquipmentPredicate.Builder b = net.minecraft.advancements.critereon.EntityEquipmentPredicate.Builder.equipment();

      return switch (slot) {
         case MAINHAND -> b.mainhand(item);
         case OFFHAND -> b.offhand(item);
         case FEET -> b.feet(item);
         case LEGS -> b.legs(item);
         case CHEST -> b.chest(item);
         case HEAD -> b.head(item);
         case BODY -> b.body(item);
         default -> throw new MatchException(null, null);
      };
   }
}
