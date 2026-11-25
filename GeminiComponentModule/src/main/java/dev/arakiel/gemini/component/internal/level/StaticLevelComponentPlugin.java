package dev.arakiel.gemini.component.internal.level;

import com.google.common.base.Suppliers;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentFactoryRegistries;
import dev.arakiel.gemini.component.ComponentInitializers;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.level.storage.LevelData;

public final class StaticLevelComponentPlugin extends StaticComponentPluginBase<LevelData, ComponentInitializers.Level> implements ComponentFactoryRegistries.Level {
   public static final StaticLevelComponentPlugin INSTANCE = new StaticLevelComponentPlugin();
   public static final Supplier<ComponentContainer.Factory<LevelData>> componentContainerFactory;

   public static ComponentContainer createContainer(LevelData properties) {
      return componentContainerFactory.get().createContainer(properties);
   }

   private StaticLevelComponentPlugin() {
      super("loading a world save", LevelData.class);
   }

   @Override
   protected Collection<EntrypointContainer<ComponentInitializers.Level>> getEntrypoints() {
      return getComponentEntrypoints("gemini:components-level", ComponentInitializers.Level.class);
   }

   protected void dispatchRegistration(ComponentInitializers.Level entrypoint) {
      entrypoint.registerLevelComponentFactories(this);
   }

   @Override
   public <C extends Component> void register(ComponentKey<C> type, ComponentFactory<LevelData, ? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.Level.class, "register");
      super.register(type, factory);
   }

   @Override
   public <C extends Component> void register(ComponentKey<? super C> type, Class<C> impl, ComponentFactory<LevelData, ? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.Level.class, "register");
      super.register(type, impl, factory);
   }

   static {
      StaticLevelComponentPlugin var10000 = INSTANCE;
      Objects.requireNonNull(INSTANCE);
      StaticLevelComponentPlugin var0 = var10000;
      componentContainerFactory = Suppliers.memoize(() -> var0.buildContainerFactory());
   }
}
