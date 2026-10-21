package dev.xkmc.l2itemselector.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.serial.config.ConfigTypeEntry;
import dev.xkmc.l2core.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2itemselector.init.data.L2ISConfig;
import dev.xkmc.l2itemselector.init.data.L2ISLangData;
import dev.xkmc.l2itemselector.init.data.L2ISTagGen;
import dev.xkmc.l2itemselector.select.SelectionRegistry;
import dev.xkmc.l2itemselector.select.SetSelectedToServer;
import dev.xkmc.l2itemselector.select.item.ItemSelectionListener;
import dev.xkmc.l2itemselector.select.item.SimpleItemSelectConfig;
import dev.xkmc.l2serial.network.PacketHandler.NetDir;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod("l2itemselector")
@EventBusSubscriber(modid = "l2itemselector", bus = Bus.MOD)
public class L2ItemSelector {
   public static final String MODID = "l2itemselector";
   public static final L2Registrate REGISTRATE = new L2Registrate("l2itemselector");
   public static final PacketHandlerWithConfig PACKET_HANDLER = new PacketHandlerWithConfig(
      "l2itemselector", 1, new Function[]{e -> e.create(SetSelectedToServer.class, NetDir.PLAY_TO_SERVER)}
   );
   public static final ConfigTypeEntry<SimpleItemSelectConfig> ITEM_SELECTOR = new ConfigTypeEntry(
      PACKET_HANDLER, "item_selector", SimpleItemSelectConfig.class
   );

   public L2ItemSelector() {
      L2ISConfig.init();
      SelectionRegistry.register(-2000, ItemSelectionListener.INSTANCE);
      REGISTRATE.addDataGenerator(ProviderType.LANG, L2ISLangData::genLang);
      REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, L2ISTagGen::genItemTags);
   }

   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
   }

   public static ResourceLocation loc(String id) {
      return ResourceLocation.fromNamespaceAndPath("l2itemselector", id);
   }
}
