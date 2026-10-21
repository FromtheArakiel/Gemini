package dev.xkmc.l2menustacker.click.quickaccess;

import dev.xkmc.l2menustacker.click.ReadOnlyStackClickHandler;
import dev.xkmc.l2menustacker.init.L2MSTagGen;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class QuickAccessClickHandler extends ReadOnlyStackClickHandler {
   public static QuickAccessClickHandler INS;
   private static final Map<Item, QuickAccessAction> MAP = new HashMap<>();

   public static synchronized void register(Item item, QuickAccessAction action) {
      MAP.put(item, action);
   }

   public QuickAccessClickHandler(ResourceLocation rl) {
      super(rl);
   }

   @Override
   public boolean isAllowed(ItemStack stack) {
      return stack.is(L2MSTagGen.QUICK_ACCESS_VANILLA);
   }

   @Override
   public void handle(ServerPlayer player, ItemStack stack) {
      QuickAccessAction accessAction = MAP.get(stack.getItem());
      if (accessAction != null) {
         accessAction.perform(player, stack);
      }
   }
}
