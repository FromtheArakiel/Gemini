package dev.xkmc.l2serial.serialization.type_cache;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TypeInfo {
   private final Class<?> cls;
   private final Type type;

   public static TypeInfo of(Class<?> cls) {
      return new TypeInfo(cls, null);
   }

   public static TypeInfo of(Field field) {
      return new TypeInfo(field.getType(), field.getGenericType());
   }

   public static TypeInfo of(RecordComponent field) {
      return new TypeInfo(field.getType(), field.getGenericType());
   }

   private static TypeInfo of(Type type) {
      if (type instanceof Class cls) {
         return new TypeInfo(cls, null);
      } else if (type instanceof ParameterizedType ptype && ptype.getRawType() instanceof Class cls) {
         return new TypeInfo(cls, ptype);
      } else if (type instanceof GenericArrayType array) {
         TypeInfo sub = of(array.getGenericComponentType());
         return new TypeInfo(sub.cls.arrayType(), array);
      } else if (type instanceof TypeVariable var) {
         throw new IllegalStateException(
            "type parameter "
               + var
               + " cannot be resolved to a class. It is likely a type variable of a generic class. Declare the field with concrete type arguments, e.g. List<String> instead of List<T>"
         );
      } else if (type instanceof WildcardType wild) {
         Type[] upper = wild.getUpperBounds();
         if (upper.length > 0 && !(upper[0] instanceof WildcardType) && !(upper[0] instanceof TypeVariable)) {
            return of(upper[0]);
         } else {
            throw new IllegalStateException(
               "wildcard type "
                  + type
                  + " cannot be resolved to a class. Declare the field with concrete type arguments, e.g. List<String> instead of List<? extends T>"
            );
         }
      } else {
         throw new IllegalStateException("type parameter cannot be converted to class. Generic Type: " + type + ", class: " + type.getClass());
      }
   }

   private TypeInfo(Class<?> cls, Type type) {
      this.cls = cls;
      this.type = type;
   }

   public Class<?> getAsClass() {
      return this.cls;
   }

   public TypeInfo getComponentType() {
      Type com = null;
      if (this.type instanceof GenericArrayType array) {
         com = array.getGenericComponentType();
      }

      return new TypeInfo(this.cls.getComponentType(), com);
   }

   public TypeInfo getGenericType(int i) {
      if (this.type instanceof ParameterizedType param) {
         Type[] types = param.getActualTypeArguments();
         if (types.length <= i) {
            throw new IllegalArgumentException("generic type " + this.type + "has " + types.length + " fields, accessing index " + i);
         } else {
            return of(types[i]);
         }
      } else {
         TypeInfo resolved = this.findFirstGenericArgument(i);
         if (resolved != null) {
            return resolved;
         } else {
            throw new IllegalStateException(
               "type parameter is missing. Type: "
                  + this.type
                  + ", Class: "
                  + this.cls
                  + ". The class is not declared with type parameters, and no generic superclass/interface with concrete type arguments was found. Declare the field with explicit type arguments, e.g. List<String>, or bind them in the subclass, e.g. class StringList extends ArrayList<String>"
            );
         }
      }
   }

   public TypeInfo getGenericType(Class<?> base, int i) {
      TypeVariable<?>[] params = base.getTypeParameters();
      if (params.length <= i) {
         throw new IllegalArgumentException("class " + base + " has " + params.length + " type parameters, accessing index " + i);
      }

      Type resolved = InheritanceGenericResolver.resolve(this.cls, this.type, base, i);
      if (resolved == null) {
         throw new IllegalStateException(
            "cannot resolve type argument "
               + i
               + " of "
               + base
               + " from Type: "
               + this.type
               + ", Class: "
               + this.cls
               + ". The class does not extend/implement "
               + base.getName()
               + " with concrete type arguments. Declare the field with explicit type arguments, e.g. List<String>, or bind them in the subclass, e.g. class StringList extends ArrayList<String>"
         );
      }

      try {
         return of(resolved);
      } catch (IllegalStateException e) {
         throw new IllegalStateException(
            "cannot resolve type argument "
               + i
               + " of "
               + base
               + " from Type: "
               + this.type
               + ", Class: "
               + this.cls
               + ". The resolved type is still generic: "
               + resolved
               + ". Declare the field with concrete type arguments.",
            e
         );
      }
   }

   public Object newInstance() throws Exception {
      return this.cls.getConstructor().newInstance();
   }

   private TypeInfo findFirstGenericArgument(int i) {
      Set<Type> visited = new HashSet<>();
      ArrayDeque<Type> queue = new ArrayDeque<>();
      if (this.cls.getGenericSuperclass() != null) {
         queue.add(this.cls.getGenericSuperclass());
      }

      queue.addAll(Arrays.asList(this.cls.getGenericInterfaces()));

      while (!queue.isEmpty()) {
         Type sup = queue.poll();
         if (visited.add(sup)) {
            if (sup instanceof ParameterizedType ptype) {
               Type[] args = ptype.getActualTypeArguments();
               if (args.length > i) {
                  try {
                     return of(args[i]);
                  } catch (IllegalStateException var10) {
                  }
               }

               if (ptype.getRawType() instanceof Class raw) {
                  if (raw.getGenericSuperclass() != null) {
                     queue.add(raw.getGenericSuperclass());
                  }

                  queue.addAll(Arrays.asList(raw.getGenericInterfaces()));
               }
            } else if (sup instanceof Class raw) {
               if (raw.getGenericSuperclass() != null) {
                  queue.add(raw.getGenericSuperclass());
               }

               queue.addAll(Arrays.asList(raw.getGenericInterfaces()));
            }
         }
      }

      return null;
   }

   public boolean isArray() {
      return this.cls.isArray();
   }

   @Override
   public String toString() {
      return "{cls = " + this.cls + ", type = " + this.type + "}";
   }

   public ClassCache toCache() {
      return ClassCache.get(this.cls);
   }
}
