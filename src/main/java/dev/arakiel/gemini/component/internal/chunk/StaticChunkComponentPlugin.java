package dev.arakiel.gemini.component.internal.chunk;

import com.google.common.base.Suppliers;
import dev.arakiel.gemini.component.chunk.ChunkComponentFactoryRegistry;
import dev.arakiel.gemini.component.chunk.ChunkComponentInitializer;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class StaticChunkComponentPlugin extends StaticComponentPluginBase<ChunkAccess, ChunkComponentInitializer> implements ChunkComponentFactoryRegistry {
   public static final StaticChunkComponentPlugin INSTANCE = new StaticChunkComponentPlugin();
   private static final Supplier<ComponentContainer.Factory<ChunkAccess>> componentsContainerFactory;

   public static ComponentContainer createContainer(ChunkAccess chunk) {
      return componentsContainerFactory.get().createContainer(chunk);
   }

   private StaticChunkComponentPlugin() {
      super("loading a chunk", ChunkAccess.class);
   }

   @Override
   protected Collection<EntrypointContainer<ChunkComponentInitializer>> getEntrypoints() {
      return getComponentEntrypoints("gemini:components-chunk", ChunkComponentInitializer.class);
   }

   protected void dispatchRegistration(ChunkComponentInitializer entrypoint) {
      entrypoint.registerChunkComponentFactories(this);
   }

   @Override
   public <C extends Component> void register(ComponentKey<C> type, ComponentFactory<ChunkAccess, ? extends C> factory) {
      this.register(type, type.getComponentClass(), factory);
   }

   @Override
   public <C extends Component> void register(ComponentKey<? super C> type, Class<C> impl, ComponentFactory<ChunkAccess, ? extends C> factory) {
      this.checkLoading(ChunkComponentFactoryRegistry.class, "register");
      super.register(type, factory);
   }

   static {
      StaticChunkComponentPlugin var10000 = INSTANCE;
      Objects.requireNonNull(INSTANCE);
      StaticChunkComponentPlugin var0 = var10000;
      componentsContainerFactory = Suppliers.memoize(() -> var0.buildContainerFactory());
   }
}
