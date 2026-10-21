package dev.xkmc.l2menustacker.screen.track;

import dev.xkmc.l2menustacker.screen.base.LayerPopType;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.server.level.ServerPlayer;

@SerialClass
public final class TrackedEntry<T extends Record & TrackedEntryData<T>> {
   @SerialField
   private TrackedEntryType<T> type;
   private T data;
   @SerialField
   private String title;

   @Deprecated
   public TrackedEntry() {
   }

   public TrackedEntry(TrackedEntryType<T> type, T data) {
      this.type = type;
      this.data = data;
   }

   public static <T extends Record & TrackedEntryData<T>> TrackedEntry<T> of(TrackedEntryType<T> type, T data) {
      return new TrackedEntry<>(type, data);
   }

   public LayerPopType restoreServerMenu(ServerPlayer player) {
      Component comp = this.title == null ? null : Serializer.fromJson(this.title, player.level().registryAccess());
      return this.type.restoreMenuNotifyClient(player, this.data, comp);
   }

   public boolean shouldReturn(TrackedEntry<?> next) {
      return this.type != next.type ? false : this.data.equals(next.data());
   }

   public TrackedEntryType<T> type() {
      return this.type;
   }

   public T data() {
      return this.data;
   }

   public String title() {
      return this.title;
   }

   public void setTitle(@Nullable Component title, Provider access) {
      this.title = title == null ? "" : Serializer.toJson(title, access);
   }
}
