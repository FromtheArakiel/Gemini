package dev.xkmc.l2tabs.compat.api;

import dev.xkmc.l2tabs.compat.track.CurioSlotData;
import net.minecraft.network.chat.Component;

public interface INamedSlot {
   Component getName();

   CurioSlotData toSlotData();
}
