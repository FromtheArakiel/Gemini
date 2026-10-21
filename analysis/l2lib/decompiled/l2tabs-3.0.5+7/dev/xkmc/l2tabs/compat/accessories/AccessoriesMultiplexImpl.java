package dev.xkmc.l2tabs.compat.accessories;

import dev.xkmc.l2menustacker.screen.source.MenuSourceRegistry;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import dev.xkmc.l2menustacker.screen.track.MenuTraceRegistry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntryType;
import dev.xkmc.l2tabs.compat.api.AccessoriesMultiplex;
import dev.xkmc.l2tabs.compat.api.IAccessoriesWrapper;
import dev.xkmc.l2tabs.compat.api.INamedSlot;
import dev.xkmc.l2tabs.compat.common.CuriosListMenu;
import dev.xkmc.l2tabs.compat.track.CurioSlotData;
import dev.xkmc.l2tabs.compat.track.CurioSource;
import dev.xkmc.l2tabs.compat.track.CurioTraceData;
import dev.xkmc.l2tabs.init.data.L2TabsConfig;
import dev.xkmc.l2tabs.tabs.contents.TabInventory;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.client.AccessoriesClient;
import io.wispforest.accessories.menu.AccessoriesInternalSlot;
import io.wispforest.accessories.menu.AccessoriesMenuTypes;
import io.wispforest.accessories.menu.AccessoriesMenuVariant;
import io.wispforest.accessories.menu.variants.AccessoriesMenuBase;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AccessoriesMultiplexImpl extends AccessoriesMultiplex {
   @Override
   public IAccessoriesWrapper wrap(LivingEntity player, int page) {
      return new AccessoriesWrapper(player, page);
   }

   @Override
   public void onClientInit() {
      Predicate<Screen> old = TabInventory.inventoryTest;
      TabInventory.inventoryTest = screen -> {
         Class<? extends Screen> cls = (Class<? extends Screen>)screen.getClass();
         boolean isCurio = cls.getName().startsWith("top.theillusivec4.curios")
            || cls.getName().startsWith("io.wispforest.accessories") && cls.getSimpleName().equals("AccessoriesScreen");
         boolean onlyCurio = (Boolean)L2TabsConfig.CLIENT.showTabsOnlyCurio.get();
         return onlyCurio ? isCurio : old.test(screen) || isCurio;
      };
      Runnable prev = TabInventory.openInventory;
      TabInventory.openInventory = () -> {
         if ((Boolean)L2TabsConfig.CLIENT.redirectInventoryTabToCuriosInventory.get()) {
            this.clientOpenInventory();
         } else {
            prev.run();
         }
      };
   }

   @Override
   public void commonSetup() {
      MenuSourceRegistry.register(AccessoriesMenuTypes.ORIGINAL_MENU, (menu, slot, index, wid) -> this.getPlayerSlotImpl(slot, index, wid, menu));
      MenuSourceRegistry.register(AccessoriesMenuTypes.EXPERIMENTAL_MENU, (menu, slot, index, wid) -> this.getPlayerSlotImpl(slot, index, wid, menu));
      MenuTraceRegistry.register(
         AccessoriesMenuTypes.ORIGINAL_MENU, menu -> Optional.of(TrackedEntry.of((TrackedEntryType)TE_CURIO_INV.get(), new CurioTraceData(0)))
      );
      MenuTraceRegistry.register(
         AccessoriesMenuTypes.EXPERIMENTAL_MENU, menu -> Optional.of(TrackedEntry.of((TrackedEntryType)TE_CURIO_INV.get(), new CurioTraceData(0)))
      );
   }

   @Override
   public void openCuriosInv(ServerPlayer player, CurioTraceData data) {
      Accessories.openAccessoriesMenu(player, AccessoriesMenuVariant.ORIGINAL, null);
   }

   private void clientOpenInventory() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         AccessoriesClient.attemptToOpenScreen();
      }
   }

   @Override
   public Optional<PlayerSlot<?>> getPlayerSlotImpl(int slot, int index, int wid, AbstractContainerMenu menu) {
      if (menu.containerId != wid) {
         return Optional.empty();
      }

      Optional<Player> pl = this.getPlayerFromCurioCont(menu);
      if (pl.isPresent()) {
         Slot s = menu.getSlot(index);
         CurioSource source = (CurioSource)AccessoriesMultiplex.IS_CURIOS.get();
         if (s instanceof AccessoriesInternalSlot curioSlot) {
            if (curioSlot.isCosmetic) {
               return Optional.empty();
            }

            return Optional.of(new PlayerSlot(source, new CurioSlotData(curioSlot.accessoriesContainer.getSlotName(), curioSlot.getSlotIndex())));
         }

         if (s instanceof INamedSlot curioSlot) {
            return Optional.of(new PlayerSlot(source, curioSlot.toSlotData()));
         }
      }

      return Optional.empty();
   }

   private Optional<Player> getPlayerFromCurioCont(AbstractContainerMenu menu) {
      if (menu instanceof AccessoriesMenuBase cont) {
         return cont.targetEntity() != null && cont.targetEntity() != cont.owner() ? Optional.empty() : Optional.of(cont.owner());
      } else {
         return menu instanceof CuriosListMenu cont ? Optional.of(cont.inventory.player) : Optional.empty();
      }
   }

   @Override
   public ItemStack getItemFromSlot(Player player, CurioSlotData slot) {
      AccessoriesCapability cap = AccessoriesCapability.get(player);
      if (cap == null) {
         return ItemStack.EMPTY;
      }

      AccessoriesContainer handler = (AccessoriesContainer)cap.getContainers().get(slot.id());
      return handler != null && handler.getSize() > slot.slot() ? handler.getAccessories().getItem(slot.slot()) : ItemStack.EMPTY;
   }
}
