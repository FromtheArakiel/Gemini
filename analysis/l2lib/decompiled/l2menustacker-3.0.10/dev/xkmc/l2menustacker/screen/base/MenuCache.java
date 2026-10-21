package dev.xkmc.l2menustacker.screen.base;

import dev.xkmc.l2menustacker.init.L2MSConfig;
import dev.xkmc.l2menustacker.screen.track.MenuProviderTraceData;
import dev.xkmc.l2menustacker.screen.track.TrackedEntry;
import dev.xkmc.l2menustacker.screen.track.TrackedEntryType;
import io.netty.buffer.Unpooled;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.connection.ConnectionType;

public record MenuCache(AbstractContainerMenu menu, MenuProvider pvd, Component title, MenuTriggerType type, @Nullable RegistryFriendlyByteBuf buf) {
   public static MenuCache of(
      RegistryAccess access, AbstractContainerMenu menu, MenuProvider next, MenuTriggerType type, @Nullable Consumer<RegistryFriendlyByteBuf> writer
   ) {
      RegistryFriendlyByteBuf buf = null;
      if (writer != null) {
         buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), access, ConnectionType.NEOFORGE);
         writer.accept(buf);
      }

      if ((Boolean)L2MSConfig.SERVER.tabSafeMode.get()) {
         if (buf != null && buf.writerIndex() > 0) {
            type = MenuTriggerType.DISABLED;
         } else {
            type = MenuTriggerType.OPEN_MENU;
         }

         buf = null;
      }

      return new MenuCache(menu, next, next.getDisplayName(), type, buf);
   }

   public TrackedEntry<?> constructEntry() {
      return TrackedEntry.of((TrackedEntryType<MenuProviderTraceData>)L2MSReg.TE_MENU_PROVIDER.get(), new MenuProviderTraceData(this));
   }

   public boolean restore(ServerPlayer player) {
      return this.type.restore(player, this.pvd, this.buf);
   }
}
