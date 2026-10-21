package dev.xkmc.l2core.serial.loot;

import java.util.Optional;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.advancements.critereon.StatePropertiesPredicate.PropertyMatcher;
import net.minecraft.advancements.critereon.StatePropertiesPredicate.RangedMatcher;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootPool.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

@Deprecated
public class LootTableTemplate {
   public static Builder getPool(int roll, int bonus) {
      return LootPool.lootPool().setRolls(ConstantValue.exactly(roll)).setBonusRolls(ConstantValue.exactly(0.0F));
   }

   public static net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder<?> getItem(Item item, int count) {
      return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)));
   }

   public static net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder<?> getItem(Item item, int min, int max) {
      return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
   }

   public static net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder byPlayer() {
      return LootItemKilledByPlayerCondition.killedByPlayer();
   }

   public static net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder chance(float chance) {
      return LootItemRandomChanceCondition.randomChance(chance);
   }

   public static net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.Builder withBlockState(
      Block block, Property<Integer> prop, int low, int high
   ) {
      net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder builder = net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties();
      builder.matchers.add(new PropertyMatcher(prop.getName(), new RangedMatcher(Optional.of(Integer.toString(low)), Optional.of(Integer.toString(high)))));
      return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(builder);
   }

   public static net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.Builder withBlockState(
      Block block, Property<Integer> prop, int val
   ) {
      return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
         .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(prop, val));
   }

   public static net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.Builder withBlockState(
      Block block, Property<Boolean> prop, boolean val
   ) {
      return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
         .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(prop, val));
   }

   public static net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.Builder withBlockState(
      Block block, Property<?> prop, String val
   ) {
      return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
         .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(prop, val));
   }

   public static EnchantmentPredicate hasEnchantment(Holder<Enchantment> enchant, int min) {
      return new EnchantmentPredicate(enchant, Ints.atLeast(min));
   }
}
