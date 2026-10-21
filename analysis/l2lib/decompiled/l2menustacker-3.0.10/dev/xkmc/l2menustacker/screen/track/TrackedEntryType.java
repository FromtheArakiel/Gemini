package dev.xkmc.l2menustacker.screen.track;

import dev.xkmc.l2core.init.reg.registrate.NamedEntry;
import dev.xkmc.l2menustacker.screen.base.L2MSReg;
import dev.xkmc.l2menustacker.screen.base.LayerPopType;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public abstract class TrackedEntryType<T extends Record & TrackedEntryData<T>> extends NamedEntry<TrackedEntryType<?>> {
   public TrackedEntryType() {
      super(L2MSReg.TRACKED_ENTRY_TYPE);
   }

   public abstract LayerPopType restoreMenuNotifyClient(ServerPlayer var1, T var2, @Nullable Component var3);
}
