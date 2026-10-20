package dev.arakiel.gemini.component.internal.entity;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.CopyableComponent;
import dev.arakiel.gemini.component.entity.RespawnCopyStrategy;
import dev.arakiel.gemini.component.entity.RespawnableComponent;
import dev.arakiel.gemini.component.internal.base.ComponentsInternals;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings({"unchecked", "deprecation"})
public final class GeminiEntityInternals {
   public static final RespawnCopyStrategy<Component> DEFAULT_COPY_STRATEGY = GeminiEntityInternals::defaultCopyStrategy;
   private static final Map<Class<? extends Entity>, ComponentContainer.Factory<Entity>> entityContainerFactories = new HashMap<>();
   private static final Map<ComponentKey<?>, Map<Class<? extends Entity>, RespawnCopyStrategy<?>>> respawnCopyStrategies = new HashMap<>();
   private static final Object factoryMutex = new Object();

   private GeminiEntityInternals() {
      throw new AssertionError();
   }

   public static ComponentContainer createEntityComponentContainer(Entity entity) {
      Class<? extends Entity> entityClass = (Class<? extends Entity>)entity.getClass();
      return Objects.requireNonNullElseGet(entityContainerFactories.get(entityClass), () -> getEntityFactory(entityClass)).createContainer(entity);
   }

   private static synchronized ComponentContainer.Factory<Entity> getEntityFactory(Class<? extends Entity> entityClass) {
      ComponentContainer.Factory<Entity> existing = entityContainerFactories.get(entityClass);
      if (existing != null) {
         return existing;
      } else {
         ComponentContainer.Factory<Entity> factory;
         if (StaticEntityComponentPlugin.INSTANCE.requiresStaticFactory(entityClass)) {
            factory = StaticEntityComponentPlugin.INSTANCE.buildDedicatedFactory(entityClass);
         } else {
            Class<? extends Entity> superclass = (Class<? extends Entity>)entityClass.getSuperclass();

            assert Entity.class.isAssignableFrom(superclass) : "requiresStaticFactory returned false on Entity?";

            factory = getEntityFactory(superclass);
         }

         entityContainerFactories.put(entityClass, factory);
         return factory;
      }
   }

   public static <C extends Component> void registerRespawnCopyStrat(
      ComponentKey<? super C> type, Class<? extends Entity> entityClass, RespawnCopyStrategy<? super C> strategy
   ) {
      if (respawnCopyStrategies.computeIfAbsent(type, t -> new HashMap<>()).put(entityClass, strategy) != null) {
         ComponentsInternals.LOGGER.warn("Multiple respawn copy strategies registered for key {} and entity class {}", type, entityClass);
      }
   }

   public static <C extends Component> RespawnCopyStrategy<? super C> getRespawnCopyStrategy(ComponentKey<C> type, Class<? extends LivingEntity> entityClass) {
      RespawnCopyStrategy<?> strat = null;

      for (Class<?> c = entityClass; strat == null && LivingEntity.class.isAssignableFrom(c); c = c.getSuperclass()) {
         strat = respawnCopyStrategies.getOrDefault(type, Map.of()).get(c);
      }

      return strat == null ? DEFAULT_COPY_STRATEGY : (RespawnCopyStrategy<? super C>) strat;
   }

   private static void defaultCopyStrategy(
      Component from, Component to, Provider registryLookup, boolean lossless, boolean keepInventory, boolean sameCharacter
   ) {
      if (to instanceof RespawnableComponent) {
         playerComponentCopy(from, (RespawnableComponent)to, registryLookup, lossless, keepInventory, sameCharacter);
      } else {
         RespawnCopyStrategy.LOSSLESS_ONLY.copyForRespawn(from, to, registryLookup, lossless, keepInventory, sameCharacter);
      }
   }

   private static <C extends Component> void playerComponentCopy(
      Component from, RespawnableComponent<C> to, Provider registryLookup, boolean lossless, boolean keepInventory, boolean sameCharacter
   ) {
      if (to.shouldCopyForRespawn(lossless, keepInventory, sameCharacter)) {
         to.copyForRespawn((C)from, registryLookup, lossless, keepInventory, sameCharacter);
      }
   }

   public static <C extends Component> void copyAsCopyable(Component from, CopyableComponent<C> to, Provider registryLookup) {
      to.copyFrom((C)from, registryLookup);
   }
}
