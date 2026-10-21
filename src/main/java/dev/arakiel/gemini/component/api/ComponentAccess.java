package dev.arakiel.gemini.component.api;


public interface ComponentAccess {
   default <C extends Component> C getComponent(ComponentKey<C> key) {
      return key.get(this.asComponentProvider());
   }

   default void syncComponent(ComponentKey<?> key) {
      key.sync(this.asComponentProvider());
   }

   default void syncComponent(ComponentKey<?> key, ComponentSync.PacketWriter packetWriter) {
      key.sync(this.asComponentProvider(), packetWriter);
   }

   default void syncComponent(ComponentKey<?> key, ComponentSync.PacketWriter packetWriter, ComponentSync.PlayerPredicate predicate) {
      key.sync(this.asComponentProvider(), packetWriter, predicate);
   }

   default ComponentProvider asComponentProvider() {
      return (ComponentProvider)this;
   }
}