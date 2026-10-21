package dev.xkmc.l2serial.serialization.type_cache;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.DataResult.Error;
import dev.xkmc.l2serial.util.Wrappers;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class RecordCache {
   private static final Map<Class<?>, RecordCache> CACHE = new ConcurrentHashMap<>();
   private final Class<?> cls;
   private volatile boolean init = false;
   private volatile Field[] fields;
   private volatile RecordComponent[] components;
   private volatile Constructor<?> factory;

   public static RecordCache get(Class<?> cls) {
      return CACHE.computeIfAbsent(cls, RecordCache::new);
   }

   private RecordCache(Class<?> cls) {
      this.cls = cls;
   }

   private synchronized void init() throws Exception {
      if (!this.init) {
         this.init = true;
         this.components = this.cls.getRecordComponents();
         this.fields = Arrays.stream(this.cls.getDeclaredFields()).filter(fx -> !Modifier.isStatic(fx.getModifiers())).toArray(Field[]::new);

         for (Field f : this.fields) {
            f.setAccessible(true);
         }

         Class[] clss = Arrays.stream(this.fields).map(Field::getType).toArray(Class[]::new);
         this.factory = this.cls.getConstructor(clss);
         this.factory.setAccessible(true);
      }
   }

   public Field[] getFields() throws Exception {
      this.init();
      return this.fields;
   }

   public RecordComponent[] getComponents() throws Exception {
      this.init();
      return this.components;
   }

   public <T> T get(Object obj, int index) throws Exception {
      this.init();
      return Wrappers.cast(this.components[index].getAccessor().invoke(obj));
   }

   public Object create(Object[] objs) throws Exception {
      this.init();
      return this.factory.newInstance(objs);
   }

   public MapCodec<?> getCodec(List<MapCodec<?>> list) {
      return new RecordCache.RecordListCodec(list);
   }

   private class RecordListCodec<O> extends MapCodec<O> {
      private final List<MapCodec<?>> list;

      public RecordListCodec(List<MapCodec<?>> list) {
         this.list = list;
      }

      public <T> Stream<T> keys(DynamicOps<T> ops) {
         return this.list.stream().flatMap(e -> e.keys(ops));
      }

      public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> map) {
         try {
            RecordCache.this.init();
            int n = RecordCache.this.fields.length;
            Object[] args = new Object[n];

            for (int i = 0; i < n; i++) {
               DataResult<?> res = this.list.get(i).decode(ops, map);
               if (res.error().isPresent()) {
                  return DataResult.error(((Error)res.error().get()).messageSupplier());
               }

               if (res.result().isPresent()) {
                  args[i] = res.result().get();
               }
            }

            return DataResult.success(Wrappers.cast(RecordCache.this.create(args)));
         } catch (Exception e) {
            return DataResult.error(e::getMessage);
         }
      }

      public <T> RecordBuilder<T> encode(O o, DynamicOps<T> ops, RecordBuilder<T> builder) {
         try {
            RecordCache.this.init();

            for (int i = 0; i < this.list.size(); i++) {
               this.list.get(i).encode(RecordCache.this.get(o, i), ops, builder);
            }

            return builder;
         } catch (Exception e) {
            return builder.withErrorsFrom(DataResult.error(e::getMessage));
         }
      }
   }
}
