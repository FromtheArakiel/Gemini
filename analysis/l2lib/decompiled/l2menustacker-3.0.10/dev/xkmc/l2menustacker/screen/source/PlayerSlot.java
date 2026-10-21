package dev.xkmc.l2menustacker.screen.source;

import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import dev.xkmc.l2serial.util.Wrappers;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public record PlayerSlot<T extends Record & ItemSourceData<T>>(ItemSource<T> type, T data) {
   public static PlayerSlot<SimpleSlotData> ofInventory(int slot) {
      return new PlayerSlot<>((ItemSource<SimpleSlotData>)L2MSReg.IS_INVENTORY.get(), new SimpleSlotData(slot));
   }

   @Nullable
   public static PlayerSlot<?> ofOtherInventory(int slot, int index, int wid, AbstractContainerMenu menu) {
      MenuSourceGetter<?> getter = MenuSourceRegistry.get(menu.getType());
      return getter == null ? null : ((MenuSourceGetter<Object>)getter).getSlot((AbstractContainerMenu)Wrappers.cast(menu), slot, index, wid).orElse(null);
   }

   public static PlayerSlot<?> read(RegistryFriendlyByteBuf buf) {
      PlayerSlot ans = (PlayerSlot)PacketCodec.from(buf, PlayerSlot.class, null);
      assert ans != null;
      return ans;
   }

   public void write(RegistryFriendlyByteBuf buf) {
      PacketCodec.to(buf, this);
   }

   public ItemStack getItem(Player player) {
      return this.type.getItem(player, this.data);
   }
}
