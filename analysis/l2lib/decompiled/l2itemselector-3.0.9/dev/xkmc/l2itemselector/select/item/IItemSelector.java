package dev.xkmc.l2itemselector.select.item;

import dev.xkmc.l2itemselector.init.data.L2ISTagGen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class IItemSelector {
   private static final HashMap<ResourceLocation, IItemSelector> LIST = new HashMap<>();
   private final ResourceLocation id;

   public static synchronized void register(IItemSelector sel) {
      LIST.put(sel.getID(), sel);
   }

   @Nullable
   public static IItemSelector.Holder getSelection(Player player) {
      ItemStack main = player.getMainHandItem();
      ItemStack off = player.getOffhandItem();
      if (main.is(L2ISTagGen.SELECTABLE)) {
         ItemSelector ans = SimpleItemSelectConfig.get(main);
         if (ans != null) {
            return new IItemSelector.Holder(main, ans);
         }

         for (IItemSelector sel : LIST.values()) {
            if (sel.test(main)) {
               return new IItemSelector.Holder(main, sel);
            }
         }
      }

      if (off.is(L2ISTagGen.SELECTABLE)) {
         ItemSelector ans = SimpleItemSelectConfig.get(off);
         if (ans != null) {
            return new IItemSelector.Holder(off, ans);
         }

         for (IItemSelector sel : LIST.values()) {
            if (sel.test(off)) {
               return new IItemSelector.Holder(off, sel);
            }
         }
      }

      return null;
   }

   public IItemSelector(ResourceLocation id) {
      this.id = id;
   }

   public abstract boolean test(ItemStack var1);

   public void swap(Player sender, int index, ItemStack stack) {
      List<ItemStack> list = this.getList(stack);
      index = (index + list.size()) % list.size();
      if (index >= 0) {
         if (this.test(sender.getMainHandItem())) {
            ItemStack e = list.get(index).copy();
            e.setCount(sender.getMainHandItem().getCount());
            sender.setItemInHand(InteractionHand.MAIN_HAND, e);
         } else if (this.test(sender.getOffhandItem())) {
            ItemStack e = list.get(index).copy();
            e.setCount(sender.getOffhandItem().getCount());
            sender.setItemInHand(InteractionHand.OFF_HAND, e);
         }
      }
   }

   public abstract int getIndex(Player var1, ItemStack var2);

   @OnlyIn(Dist.CLIENT)
   public int move(int i, Player player, ItemStack stack) {
      List<ItemStack> list = this.getList(stack);
      int index = this.getIndex(player, stack);

      while (i < 0) {
         i += list.size();
      }

      return (index + i) % list.size();
   }

   public abstract List<ItemStack> getList(ItemStack var1);

   public List<ItemStack> getDisplayList(ItemStack stack) {
      List<ItemStack> ans = new ArrayList<>();

      for (ItemStack e : this.getList(stack)) {
         if (e.getItem() instanceof CustomDisplaySelectItem item) {
            ans.add(item.getDisplay(this.id, e));
         } else {
            ans.add(e);
         }
      }

      return ans;
   }

   public ResourceLocation getID() {
      return this.id;
   }

   public int getSelHash(ItemStack stack) {
      return 0;
   }

   public record Holder(ItemStack stack, IItemSelector selector) {
      public int move(int off, Player player) {
         return this.selector.move(off, player, this.stack);
      }

      public void swap(Player player, int index) {
         this.selector.swap(player, index, this.stack);
      }

      public int getIndex(Player player) {
         return this.selector.getIndex(player, this.stack);
      }

      public List<ItemStack> getDisplayList() {
         return this.selector.getDisplayList(this.stack);
      }

      public List<ItemStack> getList() {
         return this.selector.getList(this.stack);
      }

      public int getSelHash() {
         return this.selector.getSelHash(this.stack);
      }
   }
}
