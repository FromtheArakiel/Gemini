package dev.arakiel.gemini.component.internal.entity;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentFactoryRegistries;
import dev.arakiel.gemini.component.ComponentInitializers;
import dev.arakiel.gemini.component.RespawnCopyStrategy;
import dev.arakiel.gemini.component.RespawnableComponent;
import dev.arakiel.gemini.component.internal.base.LazyDispatcher;
import dev.arakiel.gemini.component.internal.base.QualifiedComponentFactory;
import dev.arakiel.gemini.component.internal.base.ComponentPluginSupport;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings({"unchecked", "deprecation"})
public final class StaticEntityComponentPlugin extends LazyDispatcher implements ComponentFactoryRegistries.Entity {
   public static final StaticEntityComponentPlugin INSTANCE = new StaticEntityComponentPlugin();
   private final List<StaticEntityComponentPlugin.PredicatedComponentFactory<?>> dynamicFactories = new ArrayList<>();
   private final Map<Class<? extends Entity>, Map<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends Entity, ?>>>> componentFactories = new HashMap<>();

   private StaticEntityComponentPlugin() {
      super("instantiating an entity");
   }

   private static String getSuffix(Class<?> entityClass) {
      String simpleName = entityClass.getSimpleName();
      return String.format("EntityImpl_%s_%s", simpleName, Integer.toHexString(entityClass.getName().hashCode()));
   }

   public boolean requiresStaticFactory(Class<? extends Entity> entityClass) {
      this.ensureInitialized();

      for (StaticEntityComponentPlugin.PredicatedComponentFactory<?> dynamicFactory : this.dynamicFactories) {
         dynamicFactory.tryRegister(entityClass);
      }

      return entityClass == Entity.class || this.componentFactories.containsKey(entityClass);
   }

   public ComponentContainer.Factory<Entity> buildDedicatedFactory(Class<? extends Entity> entityClass) {
      this.ensureInitialized();
      var compiled = ComponentPluginSupport.collectClassHierarchy(
              this.componentFactories,
              entityClass,
              Entity.class
      );

      ComponentContainer.Factory.Builder<Entity> builder = ComponentContainer.Factory.builder(Entity.class).factoryNameSuffix(getSuffix(entityClass));

      for (Entry<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends Entity, ?>>> entry : compiled.entrySet()) {
         ComponentPluginSupport.addToBuilder(builder, entry);
      }

      return builder.build();
   }

   @Override
   protected void init() {
      StaticComponentPluginBase.processInitializers(
         StaticComponentPluginBase.getComponentEntrypoints("gemini:components-entity", ComponentInitializers.Entity.class),
         initializer -> initializer.registerEntityComponentFactories(this)
      );
   }

   @Override
   public <C extends Component, E extends Entity> void registerFor(Class<E> target, ComponentKey<C> type, ComponentFactory<E, ? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.Entity.class, "register");
      this.register0(target, type, new QualifiedComponentFactory<>(factory, type.getComponentClass(), Set.of()));
   }

   @Override
   public <C extends Component, E extends Entity> ComponentFactoryRegistries.Entity.Registration<C, E> beginRegistration(Class<E> target, ComponentKey<C> key) {
      return new StaticEntityComponentPlugin.RegistrationImpl<>(target, key);
   }

   @Override
   public <C extends RespawnableComponent<? super C>> void registerForPlayers(ComponentKey<? super C> key, ComponentFactory<Player, C> factory) {
      this.registerForPlayers(key, factory, GeminiEntityInternals.DEFAULT_COPY_STRATEGY);
   }

   @Override
   public <C extends Component, P extends C> void registerForPlayers(
      ComponentKey<C> key, ComponentFactory<Player, P> factory, RespawnCopyStrategy<? super P> respawnStrategy
   ) {
      this.registerFor(Player.class, key, factory);
      GeminiEntityInternals.registerRespawnCopyStrat(key, Player.class, respawnStrategy);
   }

   private <C extends Component, F extends C, E extends Entity> void register0(
      Class<? extends E> target, ComponentKey<? super C> key, QualifiedComponentFactory<ComponentFactory<E, F>> factory
   ) {
      ComponentPluginSupport.putFactory(this.componentFactories, target, key, factory);
      QualifiedComponentFactory.checkNoDependencyCycles(this.componentFactories.get(target));
   }

   private final class PredicatedComponentFactory<C extends Component> {
      private final Predicate<Class<? extends Entity>> predicate;
      private final ComponentKey<? super C> type;
      private final QualifiedComponentFactory<ComponentFactory<Entity, C>> factory;

      public PredicatedComponentFactory(
         Predicate<Class<? extends Entity>> predicate, ComponentKey<? super C> type, QualifiedComponentFactory<ComponentFactory<Entity, C>> factory
      ) {
         this.type = type;
         this.factory = factory;
         this.predicate = predicate;
      }

      public void tryRegister(Class<? extends Entity> clazz) {
         if (this.predicate.test(clazz)) {
            StaticEntityComponentPlugin.this.register0(clazz, this.type, this.factory);
         }
      }
   }

   private final class RegistrationImpl<C extends Component, E extends Entity> implements ComponentFactoryRegistries.Entity.Registration<C, E> {
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
      public ComponentFactoryRegistries.Entity.Registration<C, E> filter(Predicate<Class<? extends E>> test) {
         this.test = this.test == null ? test : this.test.and(test);
         return this;
      }

      @Override
      public ComponentFactoryRegistries.Entity.Registration<C, E> after(ComponentKey<?> dependency) {
         this.dependencies.add(dependency);
         return this;
      }

      @Override
      public <I extends C> ComponentFactoryRegistries.Entity.Registration<I, E> impl(Class<I> impl) {
         this.componentClass = (Class<C>) impl;
         return (ComponentFactoryRegistries.Entity.Registration<I, E>) this;
      }

      @Override
      public ComponentFactoryRegistries.Entity.Registration<C, E> respawnStrategy(RespawnCopyStrategy<? super C> strategy) {
         GeminiEntityInternals.registerRespawnCopyStrat(this.key, this.target, strategy);
         return this;
      }

      @Override
      public void end(ComponentFactory<E, C> factory) {
         StaticEntityComponentPlugin.this.checkLoading(ComponentFactoryRegistries.Entity.Registration.class, "end");
         if (this.test == null) {
            StaticEntityComponentPlugin.this.register0(this.target, this.key, new QualifiedComponentFactory<>(factory, this.componentClass, this.dependencies));
         } else {
            StaticEntityComponentPlugin.this.dynamicFactories
               .add(
                  StaticEntityComponentPlugin.this.new PredicatedComponentFactory<>(
                     c -> this.target.isAssignableFrom(c) && this.test.test(c.asSubclass(this.target)),
                     this.key,
                     new QualifiedComponentFactory<>(entity -> factory.createComponent(this.target.cast(entity)), this.componentClass, this.dependencies)
                  )
               );
         }
      }
   }
}
