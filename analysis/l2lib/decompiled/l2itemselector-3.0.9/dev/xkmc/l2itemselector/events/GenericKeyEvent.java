package dev.xkmc.l2itemselector.events;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.function.Predicate;
import net.neoforged.bus.api.Event;

public class GenericKeyEvent extends Event {
   private final Predicate<Key> pred;
   private final int action;

   public GenericKeyEvent(Predicate<Key> pred, int action) {
      this.pred = pred;
      this.action = action;
   }

   public int getAction() {
      return this.action;
   }

   public boolean test(Key key) {
      return this.pred.test(key);
   }
}
