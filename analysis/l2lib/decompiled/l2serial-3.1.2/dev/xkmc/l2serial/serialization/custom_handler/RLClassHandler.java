package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.MappedRegistry;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class RLClassHandler<R extends Tag, T> extends ClassHandler<R, T> {
   @Deprecated
   public RLClassHandler(Class<T> cls, MappedRegistry<T> r) {
      super(
         cls,
         e -> (JsonElement)(e == null ? JsonNull.INSTANCE : new JsonPrimitive(r.getKey(e).toString())),
         e -> (T)(e.isJsonNull() ? null : r.get(ResourceLocation.parse(e.getAsString()))),
         p -> {
            int index = p.readInt();
            return (T)(index == -1 ? null : r.byId(index));
         },
         (p, t) -> p.writeInt(t == null ? -1 : r.getId(t)),
         s -> (T)(s.getAsString().isEmpty() ? null : r.get(ResourceLocation.parse(s.getAsString()))),
         t -> t == null ? StringTag.valueOf("") : StringTag.valueOf(r.getKey(t).toString())
      );
   }
}
