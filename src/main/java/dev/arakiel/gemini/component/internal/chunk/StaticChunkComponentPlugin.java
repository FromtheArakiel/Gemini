package dev.arakiel.gemini.component.internal.chunk;

import com.google.common.base.Suppliers;
import dev.arakiel.gemini.component.api.ComponentFactoryRegistries;
import dev.arakiel.gemini.component.api.ComponentInitializers;
import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.api.ComponentContainer;
import dev.arakiel.gemini.component.api.ComponentFactory;
import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class StaticChunkComponentPlugin extends StaticComponentPluginBase<ChunkAccess, ComponentInitializers.Chunk> implements ComponentFactoryRegistries.Chunk {
   public static final StaticChunkComponentPlugin INSTANCE = new StaticChunkComponentPlugin();
   private static final Supplier<ComponentContainer.Factory<ChunkAccess>> componentsContainerFactory;

   public static ComponentContainer createContainer(ChunkAccess chunk) {
      return componentsContainerFactory.get().createContainer(chunk);
   }

   private StaticChunkComponentPlugin() {
      super("loading a chunk", ChunkAccess.class);
   }

   @Override
   protected Collection<EntrypointContainer<ComponentInitializers.Chunk>> getEntrypoints() {
      return getComponentEntrypoints("gemini:components-chunk", ComponentInitializers.Chunk.class);
   }

   protected void dispatchRegistration(ComponentInitializers.Chunk entrypoint) {
      entrypoint.registerChunkComponentFactories(this);
   }

   @Override
   public <C extends Component> void register(ComponentKey<C> type, ComponentFactory<ChunkAccess, ? extends C> factory) {
      this.register(type, type.getComponentClass(), factory);
   }

   @Override
   public <C extends Component> void register(ComponentKey<? super C> type, Class<C> impl, ComponentFactory<ChunkAccess, ? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.Chunk.class, "register");
      super.register(type, factory);
   }

   static {
      StaticChunkComponentPlugin var10000 = INSTANCE;
      Objects.requireNonNull(INSTANCE);
      StaticChunkComponentPlugin var0 = var10000;
      componentsContainerFactory = Suppliers.memoize(() -> var0.buildContainerFactory());
   }
}
