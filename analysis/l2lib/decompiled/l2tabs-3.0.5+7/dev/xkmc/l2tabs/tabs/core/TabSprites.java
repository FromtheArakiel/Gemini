package dev.xkmc.l2tabs.tabs.core;

import net.minecraft.resources.ResourceLocation;

public record TabSprites(
   int w,
   int h,
   ResourceLocation first,
   ResourceLocation mid,
   ResourceLocation last,
   ResourceLocation firstSel,
   ResourceLocation midSel,
   ResourceLocation lastSel
) {
   ResourceLocation get(int index, boolean sel) {
      return switch (index) {
         case 0 -> sel ? this.firstSel : this.first;
         case 2 -> sel ? this.lastSel : this.last;
         default -> sel ? this.midSel : this.mid;
      };
   }
}
