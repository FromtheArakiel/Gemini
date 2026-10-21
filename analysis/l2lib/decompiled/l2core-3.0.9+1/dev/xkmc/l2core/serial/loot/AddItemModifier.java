package dev.xkmc.l2core.serial.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2core.serial.configval.DoubleConfigValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.function.DoubleSupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddItemModifier extends LootModifier {
   public static final MapCodec<AddItemModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(
      i -> LootModifier.codecStart(i)
         .and(
            i.group(
               BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item),
               BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("fail").forGetter(m -> m.fail == Items.AIR ? Optional.empty() : Optional.of(m.fail)),
               DoubleConfigValue.CODEC.optionalFieldOf("chance").forGetter(m -> Optional.ofNullable(m.chance))
            )
         )
         .apply(i, AddItemModifier::new)
   );
   public final Item item;
   public final Item fail;
   @Nullable
   public final DoubleSupplier chance;

   protected AddItemModifier(LootItemCondition[] conditionsIn, Item item, Optional<Item> fail, Optional<DoubleSupplier> chance) {
      super(conditionsIn);
      this.item = item;
      this.fail = fail.orElse(Items.AIR);
      this.chance = chance.orElse(null);
   }

   public AddItemModifier(Item item, @Nullable DoubleConfigValue chance, LootItemCondition... conditionsIn) {
      this(item, Items.AIR, chance, conditionsIn);
   }

   public AddItemModifier(Item item, Item fail, @Nullable DoubleConfigValue chance, LootItemCondition... conditionsIn) {
      super(conditionsIn);
      this.item = item;
      this.fail = fail;
      this.chance = chance;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      if (!context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
         return generatedLoot;
      }

      if (this.chance == null || context.getRandom().nextDouble() <= this.chance.getAsDouble()) {
         generatedLoot.add(new ItemStack(this.item));
      } else if (this.fail != Items.AIR) {
         generatedLoot.add(new ItemStack(this.fail));
      }

      return generatedLoot;
   }

   public MapCodec<AddItemModifier> codec() {
      return MAP_CODEC;
   }
}
