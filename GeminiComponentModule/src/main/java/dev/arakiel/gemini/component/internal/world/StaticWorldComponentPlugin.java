package dev.arakiel.gemini.component.internal.world;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentFactoryRegistries;
import dev.arakiel.gemini.component.ComponentInitializers;
import dev.arakiel.gemini.component.internal.base.QualifiedComponentFactory;
import dev.arakiel.gemini.component.internal.base.ComponentPluginSupport;
import dev.arakiel.gemini.component.internal.base.asm.ComponentAsmHelper;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class StaticWorldComponentPlugin extends StaticComponentPluginBase<Level, ComponentInitializers.World> implements ComponentFactoryRegistries.World {
   public static final StaticWorldComponentPlugin INSTANCE = new StaticWorldComponentPlugin();
   private final Map<ResourceKey<Level>, Map<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<Level, ?>>>> worldComponentFactories = new Reference2ObjectOpenHashMap();

   private static String getSuffix(@Nullable ResourceKey<Level> dimensionId) {
      return "WorldImpl" + (dimensionId == null ? "" : "_" + ComponentAsmHelper.getJavaIdentifierName(dimensionId.location()));
   }

   private StaticWorldComponentPlugin() {
      super("loading a world", Level.class);
   }

   public boolean requiresStaticFactory(ResourceKey<Level> dimensionId) {
      INSTANCE.ensureInitialized();
      return this.worldComponentFactories.containsKey(dimensionId);
   }

   public ComponentContainer.Factory<Level> buildDedicatedFactory(@Nullable ResourceKey<Level> dimensionId) {
      INSTANCE.ensureInitialized();
      LinkedHashMap<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<Level, ?>>> compiled = new LinkedHashMap<>(
         this.worldComponentFactories.getOrDefault(null, Collections.emptyMap())
      );
      compiled.putAll(this.worldComponentFactories.getOrDefault(dimensionId, Collections.emptyMap()));
      ComponentContainer.Factory.Builder<Level> builder = ComponentContainer.Factory.builder(Level.class).factoryNameSuffix(getSuffix(dimensionId));

      for (Entry<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<Level, ?>>> entry : compiled.entrySet()) {
         ComponentPluginSupport.addToBuilder(builder, entry);
      }

      return builder.build();
   }

   @Override
   protected Collection<EntrypointContainer<ComponentInitializers.World>> getEntrypoints() {
      return getComponentEntrypoints("gemini:components-world", ComponentInitializers.World.class);
   }

   protected void dispatchRegistration(ComponentInitializers.World entrypoint) {
      entrypoint.registerWorldComponentFactories(this);
   }

   @Override
   public <C extends Component> void register(ComponentKey<C> type, ComponentFactory<Level, ? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.World.class, "register");
      this.register0(null, type, new QualifiedComponentFactory<>(factory, type.getComponentClass(), Set.of()));
   }

   @Override
   public <C extends Component> void register(ComponentKey<? super C> type, Class<C> impl, ComponentFactory<Level, ? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.World.class, "register");
      this.register0(null, type, new QualifiedComponentFactory<>(factory, (Class<? extends Component>)type.getComponentClass(), Set.of()));
   }

   @Override
   public <C extends Component> void registerFor(ResourceKey<Level> dimensionId, ComponentKey<C> type, ComponentFactory<Level, ? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.World.class, "register");
      this.register0(dimensionId, type, new QualifiedComponentFactory<>(factory, type.getComponentClass(), Set.of()));
   }

   @Override
   public <C extends Component> void registerFor(
      ResourceKey<Level> dimensionId, ComponentKey<? super C> type, Class<C> impl, ComponentFactory<Level, ? extends C> factory
   ) {
      this.checkLoading(ComponentFactoryRegistries.World.class, "register");
      this.register0(dimensionId, type, new QualifiedComponentFactory<>(factory, impl, Set.of()));
   }

   private <C extends Component> void register0(
      @Nullable ResourceKey<Level> dimensionId, ComponentKey<? super C> type, QualifiedComponentFactory<ComponentFactory<Level, ? extends C>> factory
   ) {
      ComponentPluginSupport.putFactory(this.worldComponentFactories, dimensionId, type, factory);
   }
}
