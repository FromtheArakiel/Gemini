package dev.xkmc.l2serial.serialization.unified_processor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapLike;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import dev.xkmc.l2serial.serialization.generic_types.CodecReg;
import dev.xkmc.l2serial.serialization.generic_types.HolderCodec;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;

public class JsonContext extends TreeContext<JsonElement, JsonObject, JsonArray> {
   public JsonContext(Provider access) {
      this(access.createSerializationContext(JsonOps.INSTANCE));
   }

   public JsonContext(DynamicOps<JsonElement> ops) {
      super(Optional.of(Pair.of(JsonNull.INSTANCE, Optional.empty())), ops);
   }

   @Override
   public boolean hasSpecialHandling(Class<?> cls) {
      return Handlers.JSON_MAP.containsKey(cls);
   }

   public Object deserializeSpecial(Class<?> cls, JsonElement e) {
      return Handlers.JSON_MAP.get(cls).fromJson(e);
   }

   public JsonElement serializeSpecial(Class<?> cls, Object obj) {
      return Handlers.JSON_MAP.get(cls).toJson(obj);
   }

   public Object deserializeCodec(CodecReg<?> cls, JsonElement e) {
      return ((Pair)cls.codec().decode(this.ops(), e).getOrThrow()).getFirst();
   }

   public JsonElement serializeCodec(CodecReg<?> cls, Object e) {
      return (JsonElement)cls.codec().encodeStart(this.ops(), Wrappers.cast(e)).getOrThrow();
   }

   public Optional<Either<Optional<Object>, TypeInfo>> fetchRealClass(@Nullable JsonElement e, TypeInfo def) throws Exception {
      if (e != null && !e.isJsonNull()) {
         if (e.isJsonObject()) {
            JsonObject obj = e.getAsJsonObject();
            if (obj.has("_class")) {
               String scls = obj.get("_class").getAsString();
               if (scls != null && scls.length() > 0) {
                  return Optional.of(Either.right(TypeInfo.of(Class.forName(scls))));
               }
            }
         }

         return Optional.empty();
      } else {
         return Optional.of(Either.left(Optional.empty()));
      }
   }

   public boolean shouldRead(JsonObject obj, FieldCache field) {
      return obj.has(field.getName());
   }

   @Override
   public boolean shouldReadMap(MapLike<JsonElement> obj, FieldCache field) throws Exception {
      return obj.get(field.getName()) != null;
   }

   public JsonElement retrieve(JsonObject obj, String field) {
      return obj.get(field);
   }

   public JsonElement retrieveMap(MapLike<JsonElement> obj, String field) {
      return (JsonElement)obj.get(field);
   }

   public JsonArray castAsList(JsonElement e) {
      return e.getAsJsonArray();
   }

   public int getSize(JsonArray arr) {
      return arr.size();
   }

   public JsonElement getElement(JsonArray arr, int i) {
      return arr.get(i);
   }

   public boolean isListFormat(JsonElement e) {
      return e.isJsonArray();
   }

   public JsonObject castAsMap(JsonElement e) {
      return e.getAsJsonObject();
   }

   public Object deserializeEfficientMap(JsonElement e, TypeInfo ckey, TypeInfo cval, Object ans) throws Exception {
      ((Map)ans).clear();

      for (Entry<String, JsonElement> ent : e.getAsJsonObject().entrySet()) {
         Class kcls = ckey.getAsClass();
         Object key = null;
         if (kcls == String.class) {
            key = ent.getKey();
         } else if (kcls.isEnum()) {
            key = Enum.valueOf(kcls, ent.getKey());
         } else if (HolderCodec.INS.predicate(kcls)) {
            key = HolderCodec.INS.deserializeValue(this, new JsonPrimitive(ent.getKey()), ckey, null);
         } else if (Handlers.CODEC_MAP.containsKey(kcls)) {
            key = this.deserializeCodec(Handlers.CODEC_MAP.get(kcls), new JsonPrimitive(ent.getKey()));
         } else if (Handlers.JSON_MAP.containsKey(kcls)) {
            key = Handlers.JSON_MAP.get(kcls).fromJson(new JsonPrimitive(ent.getKey()));
         }

         if (key != null) {
            ((Map)ans).put(key, UnifiedCodec.deserializeValue(this, ent.getValue(), cval, null));
         }
      }

      return ans;
   }

   public String getAsString(@Nullable JsonElement e) {
      return e == null ? "" : e.getAsString();
   }

   public void addField(JsonObject obj, String str, JsonElement e) {
      obj.add(str, e);
   }

   public JsonArray createList(int size) {
      return new JsonArray(size);
   }

   public JsonObject createMap() {
      return new JsonObject();
   }

   public void addListItem(JsonArray arr, JsonElement e) {
      arr.add(e);
   }

   public boolean canBeString(JsonElement e) {
      return e instanceof JsonPrimitive p && p.isString();
   }

   public JsonElement fromString(String str) {
      return new JsonPrimitive(str);
   }

   @Override
   public boolean shouldWrite(SerialField sf) {
      return true;
   }
}
