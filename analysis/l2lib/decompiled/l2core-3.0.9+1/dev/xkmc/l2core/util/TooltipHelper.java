package dev.xkmc.l2core.util;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

public record TooltipHelper(Level level, boolean shift, boolean alt) {
   public static void addClient(TooltipContext ctx, TooltipFlag flag, Consumer<TooltipHelper> action) {
      if (FMLEnvironment.dist.isClient()) {
         if (ctx.registries() != null) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
               action.accept(new TooltipHelper(level, Screen.hasShiftDown(), Screen.hasAltDown()));
            }
         }
      }
   }
}
