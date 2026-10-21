package dev.xkmc.l2core.init;

import dev.xkmc.l2core.base.effects.EffectToClient;
import dev.xkmc.l2core.capability.conditionals.TokenToClient;
import dev.xkmc.l2core.capability.player.PlayerCapToClient;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.serial.config.SyncPacket;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.l2serial.network.PacketHandler.NetDir;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("l2core")
public class L2Core {
   public static final String MODID = "l2core";
   public static final Logger LOGGER = LogManager.getLogger();
   public static final L2Registrate REGISTRATE = new L2Registrate("l2core");
   public static final PacketHandler PACKET_HANDLER = new PacketHandler(
      "l2core",
      1,
      new Function[]{
         e -> e.create(EffectToClient.class, NetDir.PLAY_TO_CLIENT),
         e -> e.create(PlayerCapToClient.class, NetDir.PLAY_TO_CLIENT),
         e -> e.create(TokenToClient.class, NetDir.PLAY_TO_CLIENT),
         e -> e.create(SyncPacket.class, NetDir.PLAY_TO_CLIENT)
      }
   );

   public L2Core(IEventBus bus) {
      L2LibReg.register();
      L2CoreConfig.init();
      Handlers.register();
      REGISTRATE.addDataGenerator(L2TagGen.EFF_TAGS, L2TagGen::onEffectTagGen);
   }

   public static ResourceLocation loc(String id) {
      return ResourceLocation.fromNamespaceAndPath("l2core", id);
   }
}
