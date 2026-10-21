package dev.xkmc.l2modularblock.core;

import dev.xkmc.l2modularblock.type.BlockMethod;
import dev.xkmc.l2modularblock.type.MultipleBlockMethod;
import dev.xkmc.l2modularblock.type.SingletonBlockMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockImplementor {
   final Properties props;
   private final List<MultipleBlockMethod> list = new ArrayList<>();
   private final HashMap<Class<?>, SingletonBlockMethod> map = new HashMap<>();

   public BlockImplementor(Properties p) {
      this.props = p;
   }

   public BlockImplementor addImpls(BlockMethod... impls) {
      for (BlockMethod impl : impls) {
         if (impl instanceof MultipleBlockMethod) {
            this.list.add((MultipleBlockMethod)impl);
         }

         if (impl instanceof SingletonBlockMethod one) {
            List<Class<?>> list = new ArrayList<>();
            this.addOneImpl(one.getClass(), list);

            for (Class<?> cls : list) {
               if (this.map.containsKey(cls)) {
                  throw new RuntimeException("class " + cls + " is implemented twice with " + this.map.get(cls) + " and " + impl);
               }

               this.map.put(cls, one);
            }
         }
      }

      return this;
   }

   private void addOneImpl(Class<?> cls, List<Class<?>> list) {
      Class<?> sup = cls.getSuperclass();
      if (sup != null && SingletonBlockMethod.class.isAssignableFrom(sup)) {
         this.addOneImpl(sup, list);
      }

      for (Class<?> ci : cls.getInterfaces()) {
         if (ci == SingletonBlockMethod.class) {
            throw new RuntimeException("class " + cls + " should not implement IOneImpl directly");
         }

         if (SingletonBlockMethod.class.isAssignableFrom(ci)) {
            Class<?>[] arr = ci.getInterfaces();
            if (arr.length == 1 && arr[0] == SingletonBlockMethod.class) {
               list.add(ci);
            } else {
               this.addOneImpl(ci, list);
            }
         }
      }
   }

   @Deprecated
   public <T extends MultipleBlockMethod> Stream<T> execute(Class<T> cls) {
      return this.list.stream().filter(cls::isInstance).map(e -> (T)e);
   }

   public <T extends MultipleBlockMethod> void forEach(Class<T> cls, Consumer<T> cons) {
      for (MultipleBlockMethod method : this.list) {
         if (cls.isInstance(method)) {
            cons.accept((T)method);
         }
      }
   }

   public <T extends MultipleBlockMethod, U> U reduce(Class<T> cls, U init, BiFunction<U, T, U> func) {
      for (MultipleBlockMethod method : this.list) {
         if (cls.isInstance(method)) {
            init = func.apply(init, (T)method);
         }
      }

      return init;
   }

   public <T extends MultipleBlockMethod> boolean testAnd(Class<T> cls, Predicate<T> cons) {
      for (MultipleBlockMethod method : this.list) {
         if (cls.isInstance(method) && !cons.test((T)method)) {
            return false;
         }
      }

      return true;
   }

   public <T extends MultipleBlockMethod> boolean testOr(Class<T> cls, Predicate<T> cons) {
      for (MultipleBlockMethod method : this.list) {
         if (cls.isInstance(method) && cons.test((T)method)) {
            return true;
         }
      }

      return false;
   }

   public <T extends SingletonBlockMethod> Optional<T> one(Class<T> cls) {
      return Optional.ofNullable((T)this.map.get(cls));
   }
}
