package dev.xkmc.l2itemselector.select.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class CircularSelector<T> extends IItemSelector {
   public CircularSelector(ResourceLocation id) {
      super(id);
   }

   public abstract List<T> getAll(ItemStack var1);

   @Nullable
   public abstract T getSelected(ItemStack var1);

   @Override
   public int getIndex(Player player, ItemStack stack) {
      List<T> list = this.getAll(stack);
      if (list.size() >= 9) {
         return 4;
      }

      T id = this.getSelected(stack);
      if (id == null) {
         return 0;
      }

      int index = list.indexOf(id);
      return Math.max(index, 0);
   }

   protected List<ItemStack> getListGeneric(ItemStack stack, Function<T, ItemStack> func) {
      T id = this.getSelected(stack);
      ArrayList<ItemStack> ans = new ArrayList<>();
      List<T> list = this.getAll(stack);
      int index = id == null ? -1 : list.indexOf(id);
      if (list.size() < 9) {
         if (index < 0) {
            ans.add(stack.copy());
         }

         for (T e : list) {
            ans.add(func.apply(e));
         }

         return ans;
      } else if (index < 0) {
         for (int i = 0; i < 4; i++) {
            ans.add(func.apply(list.get(list.size() - 4 + i)));
         }

         ans.add(stack.copy());

         for (int i = 0; i < 4; i++) {
            ans.add(func.apply(list.get(i)));
         }

         return ans;
      } else {
         for (int i = 0; i < 4; i++) {
            ans.add(func.apply(list.get((index - 4 + i + list.size()) % list.size())));
         }

         ans.add(func.apply(id));

         for (int i = 0; i < 4; i++) {
            ans.add(func.apply(list.get((index + 1 + i) % list.size())));
         }

         return ans;
      }
   }
}
