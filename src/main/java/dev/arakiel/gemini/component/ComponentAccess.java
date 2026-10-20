package dev.arakiel.gemini.component;

import dev.arakiel.gemini.component.sync.ComponentPacketWriter;
import dev.arakiel.gemini.component.sync.PlayerSyncPredicate;

public interface ComponentAccess {
   default <C extends Component> C getComponent(ComponentKey<C> key) {
      return key.get(this.asComponentProvider());
   }

   default void syncComponent(ComponentKey<?> key) {
      key.sync(this.asComponentProvider());
   }

   default void syncComponent(ComponentKey<?> key, ComponentPacketWriter packetWriter) {
      key.sync(this.asComponentProvider(), packetWriter);
   }

   default void syncComponent(ComponentKey<?> key, ComponentPacketWriter packetWriter, PlayerSyncPredicate predicate) {
      key.sync(this.asComponentProvider(), packetWriter, predicate);
   }

   default ComponentProvider asComponentProvider() {
      return (ComponentProvider)this;
   }
}
