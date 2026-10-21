package dev.xkmc.l2itemselector.select.item;

import dev.xkmc.l2core.serial.config.BaseConfig;
import dev.xkmc.l2core.serial.config.CollectType;
import dev.xkmc.l2core.serial.config.ConfigCollect;
import dev.xkmc.l2itemselector.init.L2ItemSelector;
import dev.xkmc.l2itemselector.init.data.L2ISTagGen;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@SerialClass
public class SimpleItemSelectConfig extends BaseConfig {
   @ConfigCollect(CollectType.MAP_COLLECT)
   @SerialField
   public final HashMap<ResourceLocation, ArrayList<Item>> map = new HashMap<>();
   private HashMap<Item, ItemSelector> cache;

   @Nullable
   public static ItemSelector get(ItemStack stack) {
      SimpleItemSelectConfig config = (SimpleItemSelectConfig)L2ItemSelector.ITEM_SELECTOR.getMerged();
      return config.find(stack);
   }

   protected void postMerge() {
      this.cache = new HashMap<>();

      for (Entry<ResourceLocation, ArrayList<Item>> ent : this.map.entrySet()) {
         ItemSelector sel = new ItemSelector(ent.getKey(), ent.getValue().stream().map(Item::getDefaultInstance).toArray(ItemStack[]::new));

         for (Item item : ent.getValue()) {
            this.cache.put(item, sel);
         }
      }
   }

   @Nullable
   private ItemSelector find(ItemStack stack) {
      return !stack.is(L2ISTagGen.SELECTABLE) ? null : this.cache.get(stack.getItem());
   }

   public SimpleItemSelectConfig add(ResourceLocation id, Item... items) {
      this.map.put(id, new ArrayList<>(List.of(items)));
      return this;
   }
}
