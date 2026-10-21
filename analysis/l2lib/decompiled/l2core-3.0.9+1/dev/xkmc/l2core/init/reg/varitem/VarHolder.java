package dev.xkmc.l2core.init.reg.varitem;

import com.tterrag.registrate.util.entry.ItemEntry;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class VarHolder<T extends Item> implements VarEntry<T>, ItemLike, Supplier<T> {
   private final String str;
   private final VarBuilder<T> builder;
   private ItemEntry<T> item;

   public VarHolder(String str, VarBuilder<T> builder) {
      this.str = str;
      this.builder = builder;
   }

   public ItemEntry<T> item() {
      return this.item;
   }

   public Item asItem() {
      return this.item.asItem();
   }

   public T get() {
      return (T)this.item.get();
   }

   @Override
   public String id() {
      return this.str;
   }

   @Override
   public void callback(ItemEntry<T> item) {
      this.item = item;
   }

   @Override
   public VarBuilder<T> builder() {
      return this.builder;
   }
}
