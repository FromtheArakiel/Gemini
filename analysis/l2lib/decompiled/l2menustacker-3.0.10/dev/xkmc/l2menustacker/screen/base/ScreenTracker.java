package dev.xkmc.l2menustacker.screen.base;

import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2menustacker.init.L2MenuStacker;
import dev.xkmc.l2menustacker.screen.packets.AddTrackedToClient;
import dev.xkmc.l2menustacker.screen.packets.CacheMouseToClient;
import dev.xkmc.l2menustacker.screen.packets.PopLayerToClient;
import dev.xkmc.l2menustacker.screen.track.MenuTraceParser;
import dev.xkmc.l2menustacker.screen.track.MenuTraceRegistry;
import dev.xkmc.l2menustacker.screen.track.NoData;
import dev.xkmc.l2menustacker.screen.track.TrackedEntry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntryType;
import dev.xkmc.l2menustacker.screen.triggers.ExitMenuTrigger;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

@SerialClass
public class ScreenTracker extends PlayerCapabilityTemplate<ScreenTracker> {
   final Stack<TrackedEntry<?>> stack = new Stack<>();
   int wid;
   private TrackedEntry<?> temp;
   private boolean restoring = false;
   private MenuCache current;
   boolean isWaiting;

   public static ScreenTracker get(Player player) {
      return (ScreenTracker)((PlayerCapabilityHolder)L2MSReg.TRACKER.type()).getOrCreate(player);
   }

   public static void onServerOpen(ServerPlayer player) {
      get(player).serverOpen(player);
   }

   public static void onServerOpenMenu(ServerPlayer player, MenuProvider next, MenuTriggerType type, @Nullable Consumer<RegistryFriendlyByteBuf> buf) {
      get(player).serverOpenMenu(player, MenuCache.of(player.level().registryAccess(), player.containerMenu, next, type, buf));
   }

   public static void removeAll(ServerPlayer player) {
      get(player).stack.clear();
   }

   public static int size(ServerPlayer player) {
      return get(player).stack.size();
   }

   private void serverOpenMenu(ServerPlayer player, MenuCache next) {
      if (this.temp != null) {
         if (this.current != null) {
            this.temp.setTitle(this.current.title(), player.level().registryAccess());
         }

         this.serverOpenMenu(player, this.temp, next.menu());
      }

      this.current = next;
      this.temp = null;
   }

   @Nullable
   private TrackedEntry<?> getEntry(AbstractContainerMenu prev) {
      if (prev.containerId == 0) {
         return TrackedEntry.of((TrackedEntryType<NoData>)L2MSReg.TE_INVENTORY.get(), NoData.DATA);
      }

      MenuTraceParser<?> getter = MenuTraceRegistry.get(prev.getType());
      if (getter != null) {
         Optional<TrackedEntry<?>> entry = (Optional<TrackedEntry<?>>)Wrappers.get(
            () -> ((MenuTraceParser<Object>)getter).track((AbstractContainerMenu)Wrappers.cast(prev))
         );
         if (entry != null && entry.isPresent()) {
            return entry.get();
         }
      }

      return this.current != null && this.current.menu() == prev ? this.current.constructEntry() : null;
   }

   private void serverOpen(ServerPlayer player) {
      if (!this.restoring) {
         this.temp = this.getEntry(player.containerMenu);
      }
   }

   private void serverOpenMenu(ServerPlayer player, TrackedEntry<?> prev, AbstractContainerMenu menu) {
      int toRemove = 0;
      if (!this.stack.isEmpty()) {
         TrackedEntry<?> next = this.getEntry(menu);
         if (next != null) {
            TrackedEntry<?> itr = this.stack.peek();
            if (itr.shouldReturn(next)) {
               toRemove = 1;
               this.stack.pop();
            }
         }
      }

      this.stack.push(prev);
      this.wid = menu.containerId;
      L2MenuStacker.PACKET_HANDLER.toClientPlayer(new AddTrackedToClient(prev, toRemove, this.wid), player);
   }

   public boolean serverRestore(ServerPlayer player, int wid) {
      if (this.stack.isEmpty()) {
         return false;
      }

      if (this.wid == wid) {
         this.restoring = true;
         L2MenuStacker.PACKET_HANDLER.toClientPlayer(new CacheMouseToClient(), player);
         LayerPopType type = (LayerPopType)Wrappers.get(() -> this.stack.pop().restoreServerMenu(player));
         this.restoring = false;
         if (type != null && type != LayerPopType.FAIL) {
            ((ExitMenuTrigger)L2MSReg.EXIT_MENU.get()).trigger(player, false);
            int id = player.containerMenu.containerId;
            this.wid = id;
            L2MenuStacker.PACKET_HANDLER.toClientPlayer(new PopLayerToClient(type, id), player);
            return true;
         }
      }

      return false;
   }
}
