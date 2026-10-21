package dev.xkmc.l2serial.serialization.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.serialization.unified_processor.JsonContext;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedCodec;
import dev.xkmc.l2serial.util.Wrappers;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;

public class JsonCodec {
   private final Provider access;

   public JsonCodec(Provider access) {
      this.access = access;
   }

   @Nullable
   public <T> T from(JsonElement obj, Class<T> cls, @Nullable T ans) {
      return Wrappers.get(() -> (T)UnifiedCodec.deserializeValue(new JsonContext(this.access), obj, TypeInfo.of(cls), ans));
   }

   @Nullable
   public <T> JsonElement toJson(T obj) {
      return Wrappers.get(() -> UnifiedCodec.serializeValue(new JsonContext(this.access), TypeInfo.of(obj.getClass()), obj));
   }

   @Nullable
   public <T extends R, R> JsonElement toJson(T obj, Class<R> cls) {
      return Wrappers.get(() -> UnifiedCodec.serializeValue(new JsonContext(this.access), TypeInfo.of(cls), obj));
   }

   @Nullable
   public <T> JsonObject toJsonObject(T obj, JsonObject input) {
      return Wrappers.get(() -> UnifiedCodec.serializeObject(new JsonContext(this.access), input, ClassCache.get(obj.getClass()), obj));
   }
}
