package dev.arakiel.gemini.component.internal.base;

import com.google.common.collect.Lists;
import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentLoadingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class QualifiedComponentFactory<I> {
   private static final boolean DEV = Boolean.getBoolean("fabric.development");
   private final I factory;
   private final Class<? extends Component> impl;
   private final Set<ComponentKey<?>> dependencies;
   private QualifiedComponentFactory.SortingState sortingState = QualifiedComponentFactory.SortingState.UNSORTED;

   public QualifiedComponentFactory(I factory, Class<? extends Component> impl, Set<ComponentKey<?>> dependencies) {
      this.factory = factory;
      this.impl = impl;
      this.dependencies = dependencies;
   }

   public static <I> void checkNoDependencyCycles(Map<ComponentKey<?>, QualifiedComponentFactory<I>> factories) {
      if (DEV) {
         sort(factories);
      }
   }

   public static <I> Map<ComponentKey<?>, QualifiedComponentFactory<I>> sort(Map<ComponentKey<?>, QualifiedComponentFactory<I>> factories) {
      factories.values().forEach(f -> f.sortingState = QualifiedComponentFactory.SortingState.UNSORTED);
      List<Entry<ComponentKey<?>, QualifiedComponentFactory<I>>> in = Lists.reverse(new ArrayList<>(factories.entrySet()));
      Deque<Entry<ComponentKey<?>, QualifiedComponentFactory<I>>> out = new ArrayDeque<>();

      while (!in.isEmpty()) {
         visitComponentNode(in, in.get(0), out);
      }

      return out.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (c1, c2) -> c1, LinkedHashMap::new));
   }

   public static <I> void checkDependenciesSatisfied(Map<ComponentKey<?>, QualifiedComponentFactory<I>> factories) {
      RuntimeException ex = null;

      for (Entry<ComponentKey<?>, QualifiedComponentFactory<I>> checked : factories.entrySet()) {
         for (ComponentKey<?> dependency : checked.getValue().dependencies()) {
            if (!factories.containsKey(dependency)) {
               RuntimeException ex1 = new StaticComponentLoadingException("Unsatisfied dependency for " + checked.getKey() + ": " + dependency);
               if (ex == null) {
                  ex = ex1;
               } else {
                  ex.addSuppressed(ex1);
               }
            }
         }
      }

      if (ex != null) {
         throw ex;
      }
   }

   private static <I> void visitComponentNode(
      List<Entry<ComponentKey<?>, QualifiedComponentFactory<I>>> factories,
      Entry<ComponentKey<?>, QualifiedComponentFactory<I>> node,
      Deque<Entry<ComponentKey<?>, QualifiedComponentFactory<I>>> out
   ) {
      switch (node.getValue().sortingState) {
         case UNSORTED:
            node.getValue().sortingState = QualifiedComponentFactory.SortingState.SORTING;

            try {
               for (Entry<ComponentKey<?>, QualifiedComponentFactory<I>> dependant : factories.stream()
                  .filter(entry -> entry.getValue().dependencies().contains(node.getKey()))
                  .toList()) {
                  visitComponentNode(factories, dependant, out);
               }
            } catch (StaticComponentLoadingException var5) {
               throw new StaticComponentLoadingException(var5.getMessage() + " <- " + node.getKey());
            }

            factories.remove(node);
            node.getValue().sortingState = QualifiedComponentFactory.SortingState.SORTED;
            out.addFirst(node);
         case SORTED:
         default:
            return;
         case SORTING:
            throw new StaticComponentLoadingException("Circular dependency detected: " + node.getKey());
      }
   }

   public I factory() {
      return this.factory;
   }

   public Class<? extends Component> impl() {
      return this.impl;
   }

   public Set<ComponentKey<?>> dependencies() {
      return this.dependencies;
   }

   @Override
   public String toString() {
      return "QualifiedComponentFactory[factory=" + this.factory + ", impl=" + this.impl + ", dependencies=" + this.dependencies + "]";
   }

   static enum SortingState {
      UNSORTED,
      SORTING,
      SORTED;
   }
}
