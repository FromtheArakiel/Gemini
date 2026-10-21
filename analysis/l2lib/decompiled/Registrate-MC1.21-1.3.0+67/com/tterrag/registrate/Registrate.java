package com.tterrag.registrate;

import java.util.Optional;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Registrate extends AbstractRegistrate<Registrate> {
   private static final Logger log = LogManager.getLogger(Registrate.class);

   public static Registrate create(String modid) {
      Registrate ret = new Registrate(modid);
      Optional<IEventBus> modEventBus = ModList.get().getModContainerById(modid).map(ModContainer::getEventBus);
      modEventBus.ifPresentOrElse(ret::registerEventListeners, () -> {
         String message = "# [Registrate] Failed to register eventListeners for mod " + modid + ", This should be reported to this mod's dev #";
         StringBuilder hashtags = new StringBuilder().append("#".repeat(message.length()));
         log.fatal(hashtags.toString());
         log.fatal(message);
         log.fatal(hashtags.toString());
      });
      return ret;
   }

   protected Registrate(String modid) {
      super(modid);
   }
}
