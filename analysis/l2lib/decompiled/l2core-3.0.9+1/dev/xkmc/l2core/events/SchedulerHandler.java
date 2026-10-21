package dev.xkmc.l2core.events;

import dev.xkmc.l2core.serial.config.PacketHandlerWithConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

@EventBusSubscriber(modid = "l2core", bus = Bus.GAME)
public class SchedulerHandler {
   private static List<BooleanSupplier> TASKS = new ArrayList<>();

   @SubscribeEvent
   public static void addReloadListeners(AddReloadListenerEvent event) {
      PacketHandlerWithConfig.addReloadListeners(event);
   }

   @SubscribeEvent
   public static void onDatapackSync(OnDatapackSyncEvent event) {
      PacketHandlerWithConfig.onDatapackSync(event);
   }

   @SubscribeEvent
   public static void serverTick(Post event) {
      execute();
   }

   public static synchronized void schedule(Runnable runnable) {
      TASKS.add(() -> {
         runnable.run();
         return true;
      });
   }

   public static synchronized void schedulePersistent(BooleanSupplier runnable) {
      TASKS.add(runnable);
   }

   private static synchronized void execute() {
      if (!TASKS.isEmpty()) {
         List<BooleanSupplier> temp = TASKS;
         TASKS = new ArrayList<>();
         temp.removeIf(BooleanSupplier::getAsBoolean);
         temp.addAll(TASKS);
         TASKS = temp;
      }
   }
}
