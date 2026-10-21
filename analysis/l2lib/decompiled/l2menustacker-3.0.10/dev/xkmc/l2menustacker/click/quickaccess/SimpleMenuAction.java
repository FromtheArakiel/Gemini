package dev.xkmc.l2menustacker.click.quickaccess;

import dev.xkmc.l2menustacker.init.L2MenuStacker;
import dev.xkmc.l2menustacker.screen.base.ScreenTracker;
import dev.xkmc.l2menustacker.screen.packets.CacheMouseToClient;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public record SimpleMenuAction(SimpleMenuAction.MenuFactory factory, String id) implements QuickAccessAction {
   @Override
   public void perform(ServerPlayer player, ItemStack stack) {
      ScreenTracker.onServerOpen(player);
      L2MenuStacker.PACKET_HANDLER.toClientPlayer(new CacheMouseToClient(), player);
      player.openMenu(new SimpleMenuProvider((wid, inv, pl) -> this.factory.create(wid, inv, new DummyHandler(pl)), Component.translatable(this.id)));
   }

   public interface MenuFactory {
      AbstractContainerMenu create(int var1, Inventory var2, ContainerLevelAccess var3);
   }
}
