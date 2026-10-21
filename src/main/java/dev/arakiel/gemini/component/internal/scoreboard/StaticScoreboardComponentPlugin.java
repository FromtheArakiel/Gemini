package dev.arakiel.gemini.component.internal.scoreboard;

import com.google.common.base.Suppliers;
import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.api.ComponentContainer;
import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.api.ComponentFactoryRegistries;
import dev.arakiel.gemini.component.api.ComponentInitializers;
import dev.arakiel.gemini.component.internal.base.GenericContainerBuilder;
import dev.arakiel.gemini.component.internal.base.LazyDispatcher;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class StaticScoreboardComponentPlugin extends LazyDispatcher implements ComponentFactoryRegistries.Scoreboard {
   public static final StaticScoreboardComponentPlugin INSTANCE = new StaticScoreboardComponentPlugin();
   public static final Supplier<ScoreboardComponentContainerFactory> scoreboardComponentsContainerFactory = Suppliers.memoize(
      INSTANCE::buildScoreboardContainerFactory
   );
   public static final Supplier<TeamComponentContainerFactory> teamComponentsContainerFactory = Suppliers.memoize(INSTANCE::buildTeamContainerFactory);
   private final GenericContainerBuilder<ComponentFactoryRegistries.ScoreboardFactory<?>, ScoreboardComponentContainerFactory> scoreboardFactories = new GenericContainerBuilder<>(
      ComponentFactoryRegistries.ScoreboardFactory.class,
      ScoreboardComponentContainerFactory.class,
      List.of(Scoreboard.class, MinecraftServer.class),
      (scoreboard, server) -> ComponentContainer.EMPTY
   );
   private final GenericContainerBuilder<ComponentFactoryRegistries.TeamFactory<?>, TeamComponentContainerFactory> teamFactories = new GenericContainerBuilder<>(
      ComponentFactoryRegistries.TeamFactory.class,
      TeamComponentContainerFactory.class,
      List.of(PlayerTeam.class, Scoreboard.class, MinecraftServer.class),
      (team, scoreboard, server) -> ComponentContainer.EMPTY
   );

   private ScoreboardComponentContainerFactory buildScoreboardContainerFactory() {
      this.ensureInitialized();
      return this.scoreboardFactories.factoryNameSuffix("ScoreboardImpl").build();
   }

   private TeamComponentContainerFactory buildTeamContainerFactory() {
      this.ensureInitialized();
      return this.teamFactories.factoryNameSuffix("TeamImpl").build();
   }

   private StaticScoreboardComponentPlugin() {
      super("made a scoreboard");
   }

   @Override
   protected void init() {
      StaticComponentPluginBase.processInitializers(
         StaticComponentPluginBase.getComponentEntrypoints("gemini:components-scoreboard", ComponentInitializers.Scoreboard.class),
         initializer -> initializer.registerScoreboardComponentFactories(this)
      );
   }

   @Override
   public <C extends Component> void registerScoreboardComponent(ComponentKey<C> type, ComponentFactoryRegistries.ScoreboardFactory<? extends C> factory) {
      this.registerScoreboardComponent(type, type.getComponentClass(), factory);
   }

   @Override
   public <C extends Component> void registerScoreboardComponent(ComponentKey<? super C> type, Class<C> impl, ComponentFactoryRegistries.ScoreboardFactory<? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.Scoreboard.class, "registerForScoreboards");
      this.scoreboardFactories.component(type, impl, factory, Set.of());
   }

   @Override
   public <C extends Component> void registerTeamComponent(ComponentKey<C> type, ComponentFactoryRegistries.TeamFactory<? extends C> factory) {
      this.registerTeamComponent(type, type.getComponentClass(), factory);
   }

   @Override
   public <C extends Component> void registerTeamComponent(ComponentKey<? super C> type, Class<C> impl, ComponentFactoryRegistries.TeamFactory<? extends C> factory) {
      this.checkLoading(ComponentFactoryRegistries.Scoreboard.class, "register");
      this.teamFactories.component(type, impl, factory, Set.of());
   }
}