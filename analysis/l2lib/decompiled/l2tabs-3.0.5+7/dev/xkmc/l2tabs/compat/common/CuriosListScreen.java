package dev.xkmc.l2tabs.compat.common;

import dev.xkmc.l2tabs.compat.api.AccessoriesMultiplex;
import dev.xkmc.l2tabs.tabs.core.ITabScreen;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CuriosListScreen extends BaseCuriosListScreen<CuriosListMenu> implements ITabScreen {
   public CuriosListScreen(CuriosListMenu cont, Inventory plInv, Component title) {
      super(cont, plInv, title);
   }

   @Override
   public void init() {
      super.init();
      new TabManager<>(this, new InvTabData()).init(x$0 -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
      }, (TabToken<InvTabData, ?>)AccessoriesMultiplex.TAB_CURIOS.get());
   }
}
