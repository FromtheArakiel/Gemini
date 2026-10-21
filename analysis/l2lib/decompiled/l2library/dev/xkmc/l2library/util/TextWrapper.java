package dev.xkmc.l2library.util;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class TextWrapper {
   public static List<FormattedCharSequence> wrapText(Font font, List<Component> list, int width) {
      int tooltipTextWidth = list.stream().mapToInt(font::width).max().orElse(0);
      boolean needsWrap = tooltipTextWidth > width;
      return needsWrap
         ? list.stream().flatMap(text -> font.split(text, width).stream()).toList()
         : list.stream().<FormattedCharSequence>map(Component::getVisualOrderText).toList();
   }
}
