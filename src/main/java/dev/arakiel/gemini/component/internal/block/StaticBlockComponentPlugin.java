package dev.arakiel.gemini.component.internal.block;

import dev.arakiel.gemini.component.block.BlockComponentFactoryRegistry;
import dev.arakiel.gemini.component.block.BlockComponentInitializer;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.tick.ClientTickingComponent;
import dev.arakiel.gemini.component.tick.ServerTickingComponent;
import dev.arakiel.gemini.component.internal.base.LazyDispatcher;
import dev.arakiel.gemini.component.internal.base.QualifiedComponentFactory;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentLoadingException;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class StaticBlockComponentPlugin extends LazyDispatcher implements BlockComponentFactoryRegistry {
   public static final StaticBlockComponentPlugin INSTANCE = new StaticBlockComponentPlugin();
   private final List<StaticBlockComponentPlugin.PredicatedComponentFactory<?>> dynamicFactories = new ArrayList<>();
   private final Map<Class<? extends BlockEntity>, Map<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends BlockEntity, ?>>>> beComponentFactories = new Reference2ObjectOpenHashMap();
   private final Set<Class<? extends BlockEntity>> clientTicking = new ReferenceOpenHashSet();
   private final Set<Class<? extends BlockEntity>> serverTicking = new ReferenceOpenHashSet();

   private static String getSuffix(Class<? extends BlockEntity> key) {
      return "BlockEntityImpl_%s_%s".formatted(key.getSimpleName(), Integer.toHexString(key.getName().hashCode()));
   }

   private StaticBlockComponentPlugin() {
      super("creating a BlockEntity");
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getComponentTicker(Level world, T be, @Nullable BlockEntityTicker<T> base) {
      if (world.isClientSide && this.clientTicking.contains(be.getClass())) {
         return base == null
            ? (w, pos, state, blockEntity) -> blockEntity.asComponentProvider().getComponentContainer().tickClientComponents()
            : (w, pos, state, blockEntity) -> {
               blockEntity.asComponentProvider().getComponentContainer().tickClientComponents();
               base.tick(w, pos, state, blockEntity);
            };
      } else if (world.isClientSide || !this.serverTicking.contains(be.getClass())) {
         return base;
      } else {
         return base == null
            ? (w, pos, state, blockEntity) -> blockEntity.asComponentProvider().getComponentContainer().tickServerComponents()
            : (w, pos, state, blockEntity) -> {
               blockEntity.asComponentProvider().getComponentContainer().tickServerComponents();
               base.tick(w, pos, state, blockEntity);
            };
      }
   }

   public boolean requiresStaticFactory(Class<? extends BlockEntity> entityClass) {
      this.ensureInitialized();

      for (StaticBlockComponentPlugin.PredicatedComponentFactory<?> dynamicFactory : this.dynamicFactories) {
         dynamicFactory.tryRegister(entityClass);
      }

      return entityClass == BlockEntity.class || this.beComponentFactories.containsKey(entityClass);
   }

   public ComponentContainer.Factory<BlockEntity> buildDedicatedFactory(Class<? extends BlockEntity> entityClass) {
      this.ensureInitialized();
      LinkedHashMap<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends BlockEntity, ?>>> compiled = new LinkedHashMap<>(
         this.beComponentFactories.getOrDefault(entityClass, Collections.emptyMap())
      );
      Class<? extends BlockEntity> type = entityClass;

      while (type != BlockEntity.class) {
         type = type.getSuperclass().asSubclass(BlockEntity.class);

         for (Entry<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends BlockEntity, ?>>> e : this.beComponentFactories
            .getOrDefault(type, Collections.emptyMap())
            .entrySet()) {
            compiled.putIfAbsent(e.getKey(), e.getValue());
         }
      }

      ComponentContainer.Factory.Builder<BlockEntity> builder = ComponentContainer.Factory.builder(BlockEntity.class).factoryNameSuffix(getSuffix(entityClass));

      for (Entry<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends BlockEntity, ?>>> entry : compiled.entrySet()) {
         this.addToBuilder(builder, entry);
         if (ClientTickingComponent.class.isAssignableFrom(entry.getValue().impl())) {
            this.clientTicking.add(entityClass);
         }

         if (ServerTickingComponent.class.isAssignableFrom(entry.getValue().impl())) {
            this.serverTicking.add(entityClass);
         }
      }

      return builder.build();
   }

   private <C extends Component> void addToBuilder(
      ComponentContainer.Factory.Builder<BlockEntity> builder,
      Entry<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends BlockEntity, ?>>> entry
   ) {
      ComponentKey<C> key = (ComponentKey<C>)entry.getKey();
      ComponentFactory<BlockEntity, C> factory = (ComponentFactory<BlockEntity, C>)entry.getValue().factory();
      Class<C> impl = (Class<C>)entry.getValue().impl();
      builder.component(key, impl, factory, entry.getValue().dependencies());
   }

   @Override
   public <C extends Component, E extends BlockEntity> void registerFor(Class<E> target, ComponentKey<C> type, ComponentFactory<E, C> factory) {
      this.checkLoading(BlockComponentFactoryRegistry.class, "register");
      this.register0(target, type, new QualifiedComponentFactory<>(factory, type.getComponentClass(), Set.of()));
   }

   private <C extends Component, F extends C, E extends BlockEntity> void register0(
      Class<? extends E> target, ComponentKey<? super C> type, QualifiedComponentFactory<ComponentFactory<E, F>> factory
   ) {
      Map<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends BlockEntity, ?>>> specializedMap = this.beComponentFactories
         .computeIfAbsent(target, t -> new LinkedHashMap<>());
      QualifiedComponentFactory<ComponentFactory<? extends BlockEntity, ?>> previousFactory = specializedMap.get(type);
      if (previousFactory != null) {
         throw new StaticComponentLoadingException(
            "Duplicate factory declarations for %s on %s: %s and %s".formatted(type.getId(), target, factory, previousFactory)
         );
      } else {
         specializedMap.put(type, (QualifiedComponentFactory) factory);
      }
   }

   @Override
   public <C extends Component, B extends BlockEntity> BlockComponentFactoryRegistry.Registration<C, B> beginRegistration(Class<B> target, ComponentKey<C> key) {
      return new StaticBlockComponentPlugin.RegistrationImpl<>(target, key);
   }

   @Override
   protected void init() {
      StaticComponentPluginBase.processInitializers(
         StaticComponentPluginBase.getComponentEntrypoints("gemini:components-block", BlockComponentInitializer.class),
         initializer -> initializer.registerBlockComponentFactories(this)
      );
   }

   private final class PredicatedComponentFactory<C extends Component> {
      private final Predicate<Class<? extends BlockEntity>> predicate;
      private final ComponentKey<? super C> type;
      private final QualifiedComponentFactory<ComponentFactory<BlockEntity, C>> factory;

      public PredicatedComponentFactory(
         Predicate<Class<? extends BlockEntity>> predicate, ComponentKey<? super C> type, QualifiedComponentFactory<ComponentFactory<BlockEntity, C>> factory
      ) {
         this.type = type;
         this.factory = factory;
         this.predicate = predicate;
      }

      public void tryRegister(Class<? extends BlockEntity> clazz) {
         if (this.predicate.test(clazz)) {
            StaticBlockComponentPlugin.this.register0(clazz, this.type, this.factory);
         }
      }
   }

   private final class RegistrationImpl<C extends Component, E extends BlockEntity> implements BlockComponentFactoryRegistry.Registration<C, E> {
      private final Class<E> target;
      private final ComponentKey<? super C> key;
      private final Set<ComponentKey<?>> dependencies;
      private Class<C> componentClass;
      private Predicate<Class<? extends E>> test;

      RegistrationImpl(Class<E> target, ComponentKey<C> key) {
         this.target = target;
         this.componentClass = key.getComponentClass();
         this.dependencies = new LinkedHashSet<>();
         this.test = null;
         this.key = key;
      }

      @Override
      public BlockComponentFactoryRegistry.Registration<C, E> filter(Predicate<Class<? extends E>> test) {
         this.test = this.test == null ? test : this.test.and(test);
         return this;
      }

      @Override
      public BlockComponentFactoryRegistry.Registration<C, E> after(ComponentKey<?> dependency) {
         this.dependencies.add(dependency);
         return this;
      }

      @Override
      public <I extends C> BlockComponentFactoryRegistry.Registration<I, E> impl(Class<I> impl) {
         this.componentClass = (Class<C>) impl;
         return (BlockComponentFactoryRegistry.Registration<I, E>) this;
      }

      @Override
      public void end(ComponentFactory<E, C> factory) {
         StaticBlockComponentPlugin.this.checkLoading(BlockComponentFactoryRegistry.Registration.class, "end");
         if (this.test == null) {
            StaticBlockComponentPlugin.this.register0(this.target, this.key, new QualifiedComponentFactory<>(factory, this.componentClass, this.dependencies));
         } else {
            StaticBlockComponentPlugin.this.dynamicFactories
               .add(
                  StaticBlockComponentPlugin.this.new PredicatedComponentFactory<>(
                     c -> this.target.isAssignableFrom(c) && this.test.test(c.asSubclass(this.target)),
                     this.key,
                     new QualifiedComponentFactory<>(entity -> factory.createComponent(this.target.cast(entity)), this.componentClass, this.dependencies)
                  )
               );
         }
      }
   }
}
