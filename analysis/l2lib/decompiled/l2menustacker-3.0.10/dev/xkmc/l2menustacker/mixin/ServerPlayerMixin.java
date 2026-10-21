package dev.xkmc.l2menustacker.mixin;

import dev.xkmc.l2menustacker.screen.base.MenuTriggerType;
import dev.xkmc.l2menustacker.screen.base.ScreenTracker;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
   @Inject(at = @At("TAIL"), remap = false, method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;")
   private void l2library_openMenu_recordTitle(MenuProvider menu, Consumer<RegistryFriendlyByteBuf> cons, CallbackInfoReturnable<OptionalInt> cir) {
      if (menu != null) {
         ServerPlayer player = (ServerPlayer)this;
         ScreenTracker.onServerOpenMenu(player, menu, MenuTriggerType.OPEN_MENU, cons);
      }
   }
}
