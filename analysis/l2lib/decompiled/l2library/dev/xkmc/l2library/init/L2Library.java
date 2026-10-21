package dev.xkmc.l2library.init;

import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2library.content.raytrace.TargetSetPacket;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.l2serial.network.PacketHandler.NetDir;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("l2library")
public class L2Library {
   public static final String MODID = "l2library";
   public static final Logger LOGGER = LogManager.getLogger();
   public static final L2Registrate REGISTRATE = new L2Registrate("l2library");
   public static final PacketHandler PACKET_HANDLER = new PacketHandler(
      "l2library", 1, new Function[]{e -> e.create(TargetSetPacket.class, NetDir.PLAY_TO_SERVER)}
   );

   public L2Library(IEventBus bus) {
      L2LibraryConfig.init();
   }

   public static ResourceLocation loc(String id) {
      return ResourceLocation.fromNamespaceAndPath("l2library", id);
   }
}
