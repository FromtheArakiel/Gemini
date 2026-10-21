package dev.xkmc.l2serial.serialization.generic_types;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedCodec;
import dev.xkmc.l2serial.serialization.unified_processor.UnifiedContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public class MapCodec extends GenericCodec {
   @Override
   public boolean predicate(Class<?> cls) {
      return Map.class.isAssignableFrom(cls);
   }

   @Override
   public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E> Object deserializeValue(C ctx, E e, TypeInfo cls, @Nullable Object ans) throws Exception {
      if (ans == null) {
         ans = cls.newInstance();
      }

      TypeInfo ckey = cls.getGenericType(Map.class, 0);
      TypeInfo cval = cls.getGenericType(Map.class, 1);
      if (!ctx.isListFormat(e)) {
         return ctx.deserializeEfficientMap(e, ckey, cval, ans);
      }

      A arr = ctx.castAsList(e);
      int n = ctx.getSize(arr);
      ((Map)ans).clear();

      for (int i = 0; i < n; i++) {
         O jeo = ctx.castAsMap(ctx.getElement(arr, i));
         Object key = UnifiedCodec.deserializeValue(ctx, ctx.getKeyOfEntry(jeo), ckey, null);
         Object val = UnifiedCodec.deserializeValue(ctx, ctx.getValueOfEntry(jeo), cval, null);
         ((Map)ans).put(key, val);
      }

      return ans;
   }

   @Override
   public <C extends UnifiedContext<E, O, A>, E, O extends E, A extends E> E serializeValue(C ctx, TypeInfo cls, @Nullable Object obj) throws Exception {
      Map<?, ?> map = (Map<?, ?>)obj;
      TypeInfo ckey = cls.getGenericType(Map.class, 0);
      TypeInfo cval = cls.getGenericType(Map.class, 1);
      List<Pair<E, E>> list = new ArrayList<>();
      A lans = ctx.createList(map.size());
      boolean can_be_map = true;

      for (Entry<?, ?> ent : map.entrySet()) {
         E k = UnifiedCodec.serializeValue(ctx, ckey, ent.getKey());
         E v = UnifiedCodec.serializeValue(ctx, cval, ent.getValue());
         list.add(Pair.of(k, v));
         can_be_map &= ctx.canBeString(k);
      }

      if (can_be_map) {
         O ans = ctx.createMap();

         for (Pair<E, E> p : list) {
            ctx.addField(ans, ctx.getAsString((E)p.getFirst()), (E)p.getSecond());
         }

         return (E)ans;
      } else {
         for (Pair<E, E> p : list) {
            O entry = ctx.createMap();
            ctx.setKeyOfEntry(entry, (E)p.getFirst());
            ctx.setValueOfEntry(entry, (E)p.getSecond());
            ctx.addListItem(lans, (E)entry);
         }

         return (E)lans;
      }
   }
}
