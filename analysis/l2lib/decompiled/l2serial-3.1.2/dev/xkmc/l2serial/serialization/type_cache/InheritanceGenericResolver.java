package dev.xkmc.l2serial.serialization.type_cache;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class InheritanceGenericResolver {
   private static final Map<InheritanceGenericResolver.Key, Optional<Type[]>> TEMPLATE_CACHE = new ConcurrentHashMap<>();

   private InheritanceGenericResolver() {
   }

   public static Type resolve(Class<?> child, Type genericType, Class<?> base, int index) {
      if (genericType instanceof ParameterizedType ptype && ptype.getRawType() == base) {
         Type[] args = ptype.getActualTypeArguments();
         if (index >= 0 && index < args.length) {
            Type ans = args[index];
            return ans instanceof TypeVariable ? null : ans;
         } else {
            return null;
         }
      } else {
         Class<?> raw = child;
         Type[] actualArgs = null;
         if (genericType instanceof ParameterizedType ptype && ptype.getRawType() instanceof Class cls) {
            raw = cls;
            actualArgs = ptype.getActualTypeArguments();
         }

         Map<TypeVariable<?>, Type> bindings = new HashMap<>();
         if (actualArgs != null) {
            TypeVariable<?>[] vars = raw.getTypeParameters();

            for (int j = 0; j < vars.length && j < actualArgs.length; j++) {
               bindings.put(vars[j], actualArgs[j]);
            }
         }

         Type[] template = templateFor(raw, base);
         if (template != null && index >= 0 && index < template.length) {
            Type ans = substitute(template[index], bindings);
            return ans instanceof TypeVariable ? null : ans;
         } else {
            return null;
         }
      }
   }

   private static Type[] templateFor(Class<?> child, Class<?> base) {
      InheritanceGenericResolver.Key key = new InheritanceGenericResolver.Key(child, base);
      Optional<Type[]> cached = TEMPLATE_CACHE.get(key);
      if (cached == null) {
         Optional<Type[]> fresh = Optional.ofNullable(buildTemplate(child, base));
         Optional<Type[]> prev = TEMPLATE_CACHE.putIfAbsent(key, fresh);
         cached = prev != null ? prev : fresh;
      }

      return cached.orElse(null);
   }

   private static Type[] buildTemplate(Class<?> child, Class<?> base) {
      if (!base.isAssignableFrom(child)) {
         return null;
      }

      TypeVariable<?>[] params = base.getTypeParameters();
      Type[] ans = new Type[params.length];
      Map<TypeVariable<?>, Type> empty = new HashMap<>();

      for (int i = 0; i < params.length; i++) {
         Type found = search(child, base, params[i], empty);
         if (found == null) {
            return null;
         }

         ans[i] = found;
      }

      return ans;
   }

   private static Type search(Class<?> current, Class<?> target, TypeVariable<?> targetVar, Map<TypeVariable<?>, Type> map) {
      if (current == target) {
         return substitute(targetVar, map);
      }

      Type ans = searchInSuperType(current.getGenericSuperclass(), target, targetVar, map);
      if (ans != null) {
         return ans;
      }

      for (Type iface : current.getGenericInterfaces()) {
         ans = searchInSuperType(iface, target, targetVar, map);
         if (ans != null) {
            return ans;
         }
      }

      return null;
   }

   private static Type searchInSuperType(Type sup, Class<?> target, TypeVariable<?> targetVar, Map<TypeVariable<?>, Type> map) {
      if (sup instanceof Class raw) {
         return !target.isAssignableFrom(raw) ? null : search(raw, target, targetVar, map);
      } else if (!(sup instanceof ParameterizedType ptype && ptype.getRawType() instanceof Class raw)) {
         return null;
      } else {
         if (!target.isAssignableFrom(raw)) {
            return null;
         }

         Map<TypeVariable<?>, Type> childMap = new HashMap<>(map);
         Type[] args = ptype.getActualTypeArguments();
         TypeVariable<?>[] vars = raw.getTypeParameters();

         for (int j = 0; j < vars.length && j < args.length; j++) {
            childMap.put(vars[j], substitute(args[j], map));
         }

         return search(raw, target, targetVar, childMap);
      }
   }

   private static Type substitute(Type t, Map<TypeVariable<?>, Type> map) {
      Set<Type> seen = new HashSet<>();

      while (t instanceof TypeVariable) {
         TypeVariable<?> var = (TypeVariable<?>)t;
         if (!seen.add(t)) {
            break;
         }

         Type next = map.get(var);
         if (next == null) {
            return t;
         }

         t = next;
      }

      if (t instanceof ParameterizedType ptype && ptype.getRawType() instanceof Class) {
         Type[] args = ptype.getActualTypeArguments();
         Type[] resolved = new Type[args.length];
         boolean changed = false;

         for (int j = 0; j < args.length; j++) {
            resolved[j] = substitute(args[j], map);
            changed |= resolved[j] != args[j];
         }

         return !changed ? t : new InheritanceGenericResolver.ResolvedParamType(ptype.getRawType(), resolved, ptype.getOwnerType());
      } else if (t instanceof GenericArrayType array) {
         Type com = array.getGenericComponentType();
         Type resolved = substitute(com, map);
         return resolved == com ? t : new InheritanceGenericResolver.ResolvedArrayType(resolved);
      } else if (t instanceof WildcardType wild) {
         Type[] upper = wild.getUpperBounds();
         Type[] lower = wild.getLowerBounds();
         Type[] rupper = new Type[upper.length];
         Type[] rlower = new Type[lower.length];
         boolean changed = false;

         for (int j = 0; j < upper.length; j++) {
            rupper[j] = substitute(upper[j], map);
            changed |= rupper[j] != upper[j];
         }

         for (int j = 0; j < lower.length; j++) {
            rlower[j] = substitute(lower[j], map);
            changed |= rlower[j] != lower[j];
         }

         return !changed ? t : new InheritanceGenericResolver.ResolvedWildType(rupper, rlower);
      } else {
         return t;
      }
   }

   private record Key(Class<?> child, Class<?> base) {
   }

   private record ResolvedArrayType(Type component) implements GenericArrayType {
      @Override
      public Type getGenericComponentType() {
         return this.component;
      }
   }

   private record ResolvedParamType(Type raw, Type[] args, Type owner) implements ParameterizedType {
      @Override
      public Type[] getActualTypeArguments() {
         return (Type[])this.args.clone();
      }

      @Override
      public Type getRawType() {
         return this.raw;
      }

      @Override
      public Type getOwnerType() {
         return this.owner;
      }
   }

   private record ResolvedWildType(Type[] upper, Type[] lower) implements WildcardType {
      @Override
      public Type[] getUpperBounds() {
         return (Type[])this.upper.clone();
      }

      @Override
      public Type[] getLowerBounds() {
         return (Type[])this.lower.clone();
      }
   }
}
