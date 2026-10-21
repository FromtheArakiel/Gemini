package dev.xkmc.l2tabs.tabs.contents;

import dev.xkmc.l2tabs.tabs.core.TabBase;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TabAttributes extends TabBase<InvTabData, TabAttributes> {
   public TabAttributes(int index, TabToken<InvTabData, TabAttributes> token, TabManager<InvTabData> manager, Component title) {
      super(index, token, manager, title);
   }

   @Override
   public void onTabClicked() {
      Minecraft.getInstance().setScreen(new AttributeScreen(this.getMessage(), 0));
   }
}
