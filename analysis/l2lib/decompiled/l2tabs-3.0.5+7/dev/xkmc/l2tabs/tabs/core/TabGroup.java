package dev.xkmc.l2tabs.tabs.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class TabGroup<G extends TabGroupData<G>> {
   private final List<TabToken<G, ?>> tokens = new ArrayList<>();
   public final TabType type;
   private final int max;
   private final boolean enableLast;

   public TabGroup(TabType type, int max, boolean enableLast) {
      this.type = type;
      this.max = max;
      this.enableLast = enableLast;
   }

   public synchronized <T extends TabBase<G, T>> TabToken<G, T> registerTab(Supplier<TabToken.TabFactory<G, T>> sup, Component title) {
      TabToken<G, T> ans = new TabToken<>(this, sup, title);
      this.tokens.add(ans);
      return ans;
   }

   public List<TabToken<G, ?>> getTabs(G token) {
      List<TabToken<G, ?>> ans = new ArrayList<>();
      this.tokens.sort(Comparator.comparingInt(TabToken::getOrder));

      for (TabToken<G, ?> e : this.tokens) {
         if (token.allows(e)) {
            ans.add(e);
         }
      }

      return ans;
   }

   public TabSprites texture() {
      return this.type.sprite;
   }

   public int max() {
      return this.max;
   }

   public boolean enableLast() {
      return this.enableLast;
   }
}
