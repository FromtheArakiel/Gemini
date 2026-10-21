package dev.xkmc.l2serial.serialization.codec;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.xkmc.l2serial.serialization.nulldefer.NullDefer;
import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.RecordCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.serialization.unified_processor.JsonContext;
import dev.xkmc.l2serial.serialization.unified_processor.TagContext;
import dev.xkmc.l2serial.serialization.unified_processor.TreeContext;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedCodec;
import dev.xkmc.l2serial.util.Wrappers;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.Level;

public abstract class MapCodecAdaptor<T> extends MapCodec<T> {
   public static <T> MapCodecAdaptor<T> of(Class<T> cls) {
      return cls.isRecord() ? new MapCodecAdaptor.Rcd<>(cls) : new MapCodecAdaptor.Cls<>(cls);
   }

   @Nullable
   public static <E, O extends E, A extends E> TreeContext<E, O, A> getContext(DynamicOps<?> ops) {
      if (ops.empty() == Unit.INSTANCE) {
         return null;
      } else if (ops.empty() instanceof JsonElement) {
         DynamicOps<JsonElement> jops = Wrappers.cast(ops);
         return Wrappers.cast(new JsonContext(jops));
      } else if (ops.empty() instanceof Tag) {
         DynamicOps<Tag> tops = Wrappers.cast(ops);
         return Wrappers.cast(new TagContext(tops, f -> true));
      } else {
         CodecAdaptor.LOGGER.error("[MapCodecAdaptor] Unknown ops type with empty ops: {}", ops.empty());
         return null;
      }
   }

   private static class Cls<T> extends MapCodecAdaptor<T> {
      private final Class<T> cls;
      private final Map<String, FieldCache> map = new LinkedHashMap<>();

      private Cls(Class<T> cls) {
         this.cls = cls;

         try {
            for (ClassCache cache = ClassCache.get(this.cls); cache.getSerialAnnotation() != null; cache = cache.getSuperclass()) {
               for (FieldCache f : cache.getFields()) {
                  if (f.getSerialAnnotation() != null) {
                     this.map.put(f.getName(), f);
                  }
               }
            }
         } catch (Exception e) {
            CodecAdaptor.LOGGER.throwing(Level.ERROR, e);
         }
      }

      public <E> Stream<E> keys(DynamicOps<E> ops) {
         return this.map.keySet().stream().map(ops::createString);
      }

      public <E> DataResult<T> decode(DynamicOps<E> ops, MapLike<E> input) {
         TreeContext<E, ?, ?> ctx = getContext(ops);
         if (ctx == null) {
            return DataResult.error(() -> "[Cls::decode] Unknown ops type with empty ops: " + ops.empty());
         }

         try {
            ClassCache cache = ClassCache.get(this.cls);
            Object ans = cache.create();

            for (Entry<String, FieldCache> entry : this.map.entrySet()) {
               FieldCache f = entry.getValue();
               if (ctx.shouldReadMap(input, f)) {
                  Object def = f.get(ans);
                  Object content = UnifiedCodec.deserializeValue(ctx, ctx.retrieveMap(input, f.getName()), f.toType(), def);
                  f.set(ans, content);
               } else {
                  NullDefer<?> nil = NullDefer.get(f.toType().getAsClass());
                  if (nil != null) {
                     f.set(ans, nil.getNullDefault());
                  }
               }
            }

            return DataResult.success(Wrappers.cast(ans));
         } catch (Exception e) {
            CodecAdaptor.LOGGER.throwing(Level.ERROR, e);
            return DataResult.error(e::getMessage);
         }
      }

      public <E> RecordBuilder<E> encode(T input, DynamicOps<E> ops, RecordBuilder<E> prefix) {
         TreeContext<Object, Object, Object> ctx = getContext(ops);
         if (ctx == null) {
            return prefix;
         }

         try {
            for (Entry<String, FieldCache> entry : this.map.entrySet()) {
               FieldCache f = entry.getValue();
               E content = Wrappers.cast(UnifiedCodec.serializeValue(ctx, f.toType(), f.get(input)));
               prefix.add(entry.getKey(), content);
            }
         } catch (Exception e) {
            CodecAdaptor.LOGGER.throwing(Level.ERROR, e);
         }

         return prefix;
      }
   }

   private static class Rcd<T> extends MapCodecAdaptor<T> {
      private final Class<T> cls;
      private final Map<String, Field> map = new LinkedHashMap<>();

      private Rcd(Class<T> cls) {
         this.cls = cls;

         try {
            RecordCache cache = RecordCache.get(cls);

            for (Field f : cache.getFields()) {
               this.map.put(f.getName(), f);
            }
         } catch (Exception e) {
            CodecAdaptor.LOGGER.throwing(Level.ERROR, e);
         }
      }

      public <E> Stream<E> keys(DynamicOps<E> ops) {
         return this.map.keySet().stream().map(ops::createString);
      }

      public <E> DataResult<T> decode(DynamicOps<E> ops, MapLike<E> input) {
         TreeContext<Object, Object, Object> ctx = getContext(ops);
         if (ctx == null) {
            return DataResult.error(() -> "[Rcd::decode] Unknown ops type with empty ops: " + ops.empty());
         }

         try {
            RecordCache cache = RecordCache.get(this.cls);
            Field[] fields = cache.getFields();
            Object[] objs = new Object[fields.length];

            for (int i = 0; i < fields.length; i++) {
               Field f = fields[i];
               objs[i] = UnifiedCodec.deserializeValue(ctx, input.get(f.getName()), TypeInfo.of(f), null);
            }

            T ans = Wrappers.cast(cache.create(objs));
            return DataResult.success(ans);
         } catch (Exception e) {
            return DataResult.error(e::getMessage);
         }
      }

      public <E> RecordBuilder<E> encode(T input, DynamicOps<E> ops, RecordBuilder<E> prefix) {
         TreeContext<Object, Object, Object> ctx = getContext(ops);
         if (ctx == null) {
            return prefix;
         }

         try {
            for (Entry<String, Field> entry : this.map.entrySet()) {
               Field f = entry.getValue();
               E val = Wrappers.cast(UnifiedCodec.serializeValue(ctx, TypeInfo.of(f), f.get(input)));
               prefix.add(entry.getKey(), val);
            }
         } catch (Exception e) {
            CodecAdaptor.LOGGER.throwing(Level.ERROR, e);
         }

         return prefix;
      }
   }
}
