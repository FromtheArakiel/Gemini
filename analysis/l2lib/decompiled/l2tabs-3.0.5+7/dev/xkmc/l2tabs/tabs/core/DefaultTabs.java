package dev.xkmc.l2tabs.tabs.core;

import dev.xkmc.l2tabs.init.L2Tabs;

public class DefaultTabs {
   public static final TabSprites UP = new TabSprites(
      26, 32, L2Tabs.loc("up/des_0"), L2Tabs.loc("up/des_1"), L2Tabs.loc("up/des_2"), L2Tabs.loc("up/sel_0"), L2Tabs.loc("up/sel_1"), L2Tabs.loc("up/sel_2")
   );
   public static final TabSprites BOTTOM = new TabSprites(
      26,
      32,
      L2Tabs.loc("bottom/des_0"),
      L2Tabs.loc("bottom/des_1"),
      L2Tabs.loc("bottom/des_2"),
      L2Tabs.loc("bottom/sel_0"),
      L2Tabs.loc("bottom/sel_1"),
      L2Tabs.loc("bottom/sel_2")
   );
   public static final TabSprites LEFT = new TabSprites(
      32,
      28,
      L2Tabs.loc("left/des_0"),
      L2Tabs.loc("left/des_1"),
      L2Tabs.loc("left/des_2"),
      L2Tabs.loc("left/sel_0"),
      L2Tabs.loc("left/sel_1"),
      L2Tabs.loc("left/sel_2")
   );
   public static final TabSprites RIGHT = new TabSprites(
      32,
      28,
      L2Tabs.loc("right/des_0"),
      L2Tabs.loc("right/des_1"),
      L2Tabs.loc("right/des_2"),
      L2Tabs.loc("right/sel_0"),
      L2Tabs.loc("right/sel_1"),
      L2Tabs.loc("right/sel_2")
   );
}
