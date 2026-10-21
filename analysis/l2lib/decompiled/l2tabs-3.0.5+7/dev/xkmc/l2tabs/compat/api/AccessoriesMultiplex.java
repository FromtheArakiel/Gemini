package dev.xkmc.l2tabs.compat.api;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.source.MenuSourceRegistry;
import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import dev.xkmc.l2menustacker.screen.track.MenuTraceRegistry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntryType;
import dev.xkmc.l2tabs.compat.accessories.AccessoriesMultiplexImpl;
import dev.xkmc.l2tabs.compat.common.CuriosEventHandler;
import dev.xkmc.l2tabs.compat.common.CuriosListMenu;
import dev.xkmc.l2tabs.compat.common.CuriosListScreen;
import dev.xkmc.l2tabs.compat.common.CuriosMenuPvd;
import dev.xkmc.l2tabs.compat.common.TabCurios;
import dev.xkmc.l2tabs.compat.curios.CuriosMultiplexImpl;
import dev.xkmc.l2tabs.compat.track.CurioInvTrace;
import dev.xkmc.l2tabs.compat.track.CurioSlotData;
import dev.xkmc.l2tabs.compat.track.CurioSource;
import dev.xkmc.l2tabs.compat.track.CurioTabTrace;
import dev.xkmc.l2tabs.compat.track.CurioTraceData;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.init.data.L2TabsLangData;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public abstract class AccessoriesMultiplex {
   private static AccessoriesMultiplex INSTANCE;
   public static MenuEntry<CuriosListMenu> MT_CURIOS;
   public static Val<TabToken<InvTabData, TabCurios>> TAB_CURIOS;
   public static Val<CurioSource> IS_CURIOS;
   public static Val<CurioInvTrace> TE_CURIO_INV;
   public static Val<CurioTabTrace> TE_CURIO_TAB;

   public static boolean isPresent() {
      return get() != null;
   }

   public static Optional<AccessoriesMultiplex> getOptional() {
      return Optional.ofNullable(get());
   }

   @Nullable
   public static AccessoriesMultiplex get() {
      if (INSTANCE == null && ModList.get().isLoaded("curios")) {
         if (ModList.get().isLoaded("accessories")) {
            INSTANCE = new AccessoriesMultiplexImpl();
         } else {
            INSTANCE = new CuriosMultiplexImpl();
         }
      }

      return INSTANCE;
   }

   public void onStartUp() {
      MT_CURIOS = L2Tabs.REGISTRATE.menu("curios", CuriosListMenu::fromNetwork, () -> CuriosListScreen::new).register();
      TAB_CURIOS = L2Tabs.TAB_REG.reg("curios", () -> L2Tabs.GROUP.registerTab(() -> TabCurios::new, L2TabsLangData.CURIOS.get()));
      IS_CURIOS = L2MSReg.SOURCES.reg("curios", CurioSource::new);
      TE_CURIO_INV = L2MSReg.TRACKED.reg("curios_inv", CurioInvTrace::new);
      TE_CURIO_TAB = L2MSReg.TRACKED.reg("curios_tab", CurioTabTrace::new);
   }

   public void onCommonSetup() {
      MenuSourceRegistry.register((MenuType)MT_CURIOS.get(), (menu, slot, index, wid) -> this.getPlayerSlotImpl(slot, index, wid, menu));
      MenuTraceRegistry.register(
         (MenuType)MT_CURIOS.get(), menu -> Optional.of(TrackedEntry.of((TrackedEntryType)TE_CURIO_TAB.get(), new CurioTraceData(menu.curios.page)))
      );
      this.commonSetup();
   }

   public static void openScreen(ServerPlayer player, CurioTraceData data) {
      CuriosEventHandler.openMenuWrapped(player, () -> new CuriosMenuPvd((MenuType<CuriosListMenu>)MT_CURIOS.get(), data.page()).open(player));
   }

   public abstract void onClientInit();

   public abstract void commonSetup();

   public abstract IAccessoriesWrapper wrap(LivingEntity var1, int var2);

   public abstract Optional<PlayerSlot<?>> getPlayerSlotImpl(int var1, int var2, int var3, AbstractContainerMenu var4);

   public abstract ItemStack getItemFromSlot(Player var1, CurioSlotData var2);

   public abstract void openCuriosInv(ServerPlayer var1, CurioTraceData var2);
}
