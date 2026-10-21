package dev.xkmc.l2menustacker.click;

import dev.xkmc.l2menustacker.init.L2MenuStacker;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public abstract class SlotClickHandler {
   public static final ConcurrentHashMap<ResourceLocation, SlotClickHandler> MAP = new ConcurrentHashMap<>();
   private final ResourceLocation id;

   public SlotClickHandler(ResourceLocation rl) {
      MAP.put(rl, this);
      this.id = rl;
   }

   protected final void slotClickToServer(int index, int slot, int wid) {
      L2MenuStacker.PACKET_HANDLER.toServer(new SlotClickToServer(this.id, index, slot, wid));
   }

   public abstract boolean isAllowed(ItemStack var1);

   public abstract void handle(ServerPlayer var1, int var2, int var3, int var4);
}
