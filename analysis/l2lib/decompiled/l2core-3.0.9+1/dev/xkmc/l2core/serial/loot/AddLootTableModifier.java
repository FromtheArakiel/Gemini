package dev.xkmc.l2core.serial.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

public class AddLootTableModifier extends LootModifier {
   public static final MapCodec<AddLootTableModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(
      inst -> codecStart(inst).and(ResourceLocation.CODEC.fieldOf("lootTable").forGetter(m -> m.lootTable)).apply(inst, AddLootTableModifier::new)
   );
   private final ResourceLocation lootTable;

   protected AddLootTableModifier(LootItemCondition[] conditionsIn, ResourceLocation lootTable) {
      super(conditionsIn);
      this.lootTable = lootTable;
   }

   public AddLootTableModifier(ResourceLocation lootTable, LootItemCondition... conditionsIn) {
      super(conditionsIn);
      this.lootTable = lootTable;
   }

   @Nonnull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      LootTable extraTable = (LootTable)context.getResolver()
         .lookupOrThrow(Registries.LOOT_TABLE)
         .getOrThrow(ResourceKey.create(Registries.LOOT_TABLE, this.lootTable))
         .value();
      extraTable.getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add));
      return generatedLoot;
   }

   public MapCodec<AddLootTableModifier> codec() {
      return MAP_CODEC;
   }
}
