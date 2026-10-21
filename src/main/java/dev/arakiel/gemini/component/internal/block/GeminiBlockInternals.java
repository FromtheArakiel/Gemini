package dev.arakiel.gemini.component.internal.block;

import dev.arakiel.gemini.component.api.ComponentContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings({"unchecked", "deprecation"})
public final class GeminiBlockInternals {
   private static final Map<Class<? extends BlockEntity>, ComponentContainer.Factory<BlockEntity>> entityContainerFactories = new HashMap<>();

   public static ComponentContainer createComponents(BlockEntity blockEntity) {
      Class<? extends BlockEntity> entityClass = (Class<? extends BlockEntity>)blockEntity.getClass();
      ComponentContainer.Factory<BlockEntity> existing = entityContainerFactories.get(entityClass);
      return Objects.requireNonNullElseGet(existing, () -> getBeComponentFactory(entityClass)).createContainer(blockEntity);
   }

   private static synchronized ComponentContainer.Factory<BlockEntity> getBeComponentFactory(Class<? extends BlockEntity> entityClass) {
      ComponentContainer.Factory<BlockEntity> existing = entityContainerFactories.get(entityClass);
      if (existing != null) {
         return existing;
      } else {
         ComponentContainer.Factory<BlockEntity> factory;
         if (StaticBlockComponentPlugin.INSTANCE.requiresStaticFactory(entityClass)) {
            factory = StaticBlockComponentPlugin.INSTANCE.buildDedicatedFactory(entityClass);
         } else {
            Class<? extends BlockEntity> superclass = (Class<? extends BlockEntity>)entityClass.getSuperclass();

            assert BlockEntity.class.isAssignableFrom(superclass) : "requiresStaticFactory returned false on BlockEntity?";

            factory = getBeComponentFactory(superclass);
         }

         entityContainerFactories.put(entityClass, factory);
         return factory;
      }
   }
}
