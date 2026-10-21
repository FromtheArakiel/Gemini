package dev.xkmc.l2core.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2core", bus = Bus.GAME)
public class ClientScheduler {
   private static List<BooleanSupplier> TASKS = new ArrayList<>();

   @SubscribeEvent
   public static void clientTick(Post event) {
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
