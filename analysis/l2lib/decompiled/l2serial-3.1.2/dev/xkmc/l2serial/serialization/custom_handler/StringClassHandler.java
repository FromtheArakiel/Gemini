package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import java.util.function.Function;
import net.minecraft.nbt.StringTag;

public class StringClassHandler<T> extends ClassHandler<StringTag, T> {
   public StringClassHandler(Class<T> cls, Function<String, T> fj, Function<T, String> tp) {
      super(cls, e -> (JsonElement)(tp == null ? JsonNull.INSTANCE : new JsonPrimitive(tp.apply(e))), e -> {
         if (e.isJsonNull()) {
            return null;
         }

         String str = e.getAsString();
         return str.length() == 0 ? null : fj.apply(str);
      }, p -> {
         String str = p.readUtf();
         return str.length() == 0 ? null : fj.apply(str);
      }, (p, t) -> p.writeUtf(t == null ? "" : tp.apply(t)), t -> fj.apply(t.getAsString()), e -> StringTag.valueOf(tp.apply(e)));
   }
}
