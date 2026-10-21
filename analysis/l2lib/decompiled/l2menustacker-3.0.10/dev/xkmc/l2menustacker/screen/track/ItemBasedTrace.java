package dev.xkmc.l2menustacker.screen.track;

import dev.xkmc.l2menustacker.screen.base.LayerPopType;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class ItemBasedTrace extends TrackedEntryType<ItemBasedTraceData> {
   public LayerPopType restoreMenuNotifyClient(ServerPlayer player, ItemBasedTraceData data, @Nullable Component comp) {
      ItemStack stack = data.parent().getItem(player);
      return stack.getItem() != data.verifier() ? LayerPopType.FAIL : this.restore(player, data.parent(), stack, comp);
   }

   public abstract LayerPopType restore(ServerPlayer var1, PlayerSlot<?> var2, ItemStack var3, @Nullable Component var4);
}
