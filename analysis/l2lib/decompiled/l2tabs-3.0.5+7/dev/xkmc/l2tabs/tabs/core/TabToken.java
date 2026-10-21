package dev.xkmc.l2tabs.tabs.core;

import dev.xkmc.l2tabs.init.L2Tabs;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class TabToken<G extends TabGroupData<G>, T extends TabBase<G, T>> {
   private final TabGroup<G> group;
   private final Supplier<TabToken.TabFactory<G, T>> factory;
   public final Component title;

   TabToken(TabGroup<G> group, Supplier<TabToken.TabFactory<G, T>> factory, Component component) {
      this.group = group;
      this.factory = factory;
      this.title = component;
   }

   public TabType getType() {
      return this.group.type;
   }

   public T create(int index, TabManager<G> manager) {
      return this.factory.get().create(index, this, manager, this.title);
   }

   public void draw(GuiGraphics g, int x, int y, boolean selected, int index) {
      int ind = index == 0 ? 0 : (this.group.enableLast() && index == this.group.max() ? 2 : 1);
      this.getType().draw(this.group.texture(), g, x, y, selected, ind);
   }

   public int getOrder() {
      Holder<TabToken<?, ?>> holder = L2Tabs.TABS.reg().wrapAsHolder(this);
      Integer ans = (Integer)holder.getData(L2Tabs.ORDER.reg());
      return ans == null ? 0 : ans;
   }

   public interface TabFactory<G extends TabGroupData<G>, T extends TabBase<G, T>> {
      T create(int var1, TabToken<G, T> var2, TabManager<G> var3, Component var4);
   }
}
