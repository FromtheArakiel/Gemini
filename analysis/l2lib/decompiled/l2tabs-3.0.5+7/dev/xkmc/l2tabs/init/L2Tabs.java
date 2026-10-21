package dev.xkmc.l2tabs.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2core.init.reg.datapack.DataMapReg;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate.RegistryInstance;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.l2core.init.reg.simple.SR;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2serial.network.PacketHandler;
import dev.xkmc.l2serial.network.PacketHandler.NetDir;
import dev.xkmc.l2tabs.compat.api.TabCuriosCompat;
import dev.xkmc.l2tabs.compat.common.CuriosEventHandler;
import dev.xkmc.l2tabs.init.data.AttrDispEntry;
import dev.xkmc.l2tabs.init.data.L2TabsConfig;
import dev.xkmc.l2tabs.init.data.L2TabsDataMapGen;
import dev.xkmc.l2tabs.init.data.L2TabsLangData;
import dev.xkmc.l2tabs.init.data.OpenCuriosPacket;
import dev.xkmc.l2tabs.tabs.contents.TabAttributes;
import dev.xkmc.l2tabs.tabs.contents.TabInventory;
import dev.xkmc.l2tabs.tabs.core.TabGroup;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.core.TabType;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod("l2tabs")
@EventBusSubscriber(modid = "l2tabs", bus = Bus.MOD)
public class L2Tabs {
   public static final String MODID = "l2tabs";
   public static final Reg REG = new Reg("l2tabs");
   public static final L2Registrate REGISTRATE = new L2Registrate("l2tabs");
   public static final PacketHandler PACKET_HANDLER = new PacketHandler(
      "l2tabs", 1, new Function[]{e -> e.create(OpenCuriosPacket.class, NetDir.PLAY_TO_SERVER)}
   );
   public static final DataMapReg<Attribute, AttrDispEntry> ATTRIBUTE_ENTRY = REG.dataMap("attribute_entry", Registries.ATTRIBUTE, AttrDispEntry.class);
   public static final RegistryInstance<TabToken<?, ?>> TABS = REGISTRATE.newRegistry("tab");
   public static final TabGroup<InvTabData> GROUP = new TabGroup<>(TabType.ABOVE, 7, false);
   public static final SR<TabToken<?, ?>> TAB_REG = SR.of(REG, TABS.reg());
   public static final Val<TabToken<InvTabData, TabInventory>> TAB_INVENTORY = TAB_REG.reg(
      "inventory", () -> GROUP.registerTab(() -> TabInventory::new, L2TabsLangData.INVENTORY.get())
   );
   public static final Val<TabToken<InvTabData, TabAttributes>> TAB_ATTRIBUTE = TAB_REG.reg(
      "attribute", () -> GROUP.registerTab(() -> TabAttributes::new, L2TabsLangData.ATTRIBUTE.get())
   );
   public static final DataMapReg<TabToken<?, ?>, Item> ICON = REG.dataMap("icon", TABS.key(), Item.class);
   public static final DataMapReg<TabToken<?, ?>, Integer> ORDER = REG.dataMap("order", TABS.key(), Integer.class);

   public L2Tabs(IEventBus bus) {
      L2TabsConfig.init();
      TabCuriosCompat.onStartup();
      REGISTRATE.addDataGenerator(ProviderType.LANG, L2TabsLangData::genLang);
      REGISTRATE.addDataGenerator(ProviderType.DATA_MAP, L2TabsDataMapGen::onDataMapGen);
      if (ModList.get().isLoaded("curios")) {
         NeoForge.EVENT_BUS.register(CuriosEventHandler.class);
      }

      ((Attribute)Attributes.ATTACK_DAMAGE.value()).setSyncable(true);
   }

   @SubscribeEvent
   public static void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> TabCuriosCompat.onCommonSetup());
   }

   public static ResourceLocation loc(String id) {
      return ResourceLocation.fromNamespaceAndPath("l2tabs", id);
   }
}
