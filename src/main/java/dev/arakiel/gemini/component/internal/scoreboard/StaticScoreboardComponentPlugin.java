package dev.arakiel.gemini.component.internal.scoreboard;

import com.google.common.base.Suppliers;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.arakiel.gemini.component.scoreboard.ScoreboardComponentFactoryV2;
import dev.arakiel.gemini.component.scoreboard.ScoreboardComponentInitializer;
import dev.arakiel.gemini.component.scoreboard.TeamComponentFactoryV2;
import dev.arakiel.gemini.component.internal.base.GenericContainerBuilder;
import dev.arakiel.gemini.component.internal.base.LazyDispatcher;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class StaticScoreboardComponentPlugin extends LazyDispatcher implements ScoreboardComponentFactoryRegistry {
   public static final StaticScoreboardComponentPlugin INSTANCE = new StaticScoreboardComponentPlugin();
   public static final Supplier<ScoreboardComponentContainerFactory> scoreboardComponentsContainerFactory = Suppliers.memoize(
      INSTANCE::buildScoreboardContainerFactory
   );
   public static final Supplier<TeamComponentContainerFactory> teamComponentsContainerFactory = Suppliers.memoize(INSTANCE::buildTeamContainerFactory);
   private final GenericContainerBuilder<ScoreboardComponentFactoryV2<?>, ScoreboardComponentContainerFactory> scoreboardFactories = new GenericContainerBuilder<>(
      ScoreboardComponentFactoryV2.class,
      ScoreboardComponentContainerFactory.class,
      List.of(Scoreboard.class, MinecraftServer.class),
      (scoreboard, server) -> ComponentContainer.EMPTY
   );
   private final GenericContainerBuilder<TeamComponentFactoryV2<?>, TeamComponentContainerFactory> teamFactories = new GenericContainerBuilder<>(
      TeamComponentFactoryV2.class,
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
         StaticComponentPluginBase.getComponentEntrypoints("gemini:components-scoreboard", ScoreboardComponentInitializer.class),
         initializer -> initializer.registerScoreboardComponentFactories(this)
      );
   }

   @Override
   public <C extends Component> void registerScoreboardComponent(ComponentKey<C> type, ScoreboardComponentFactoryV2<? extends C> factory) {
      this.registerScoreboardComponent(type, type.getComponentClass(), factory);
   }

   @Override
   public <C extends Component> void registerScoreboardComponent(ComponentKey<? super C> type, Class<C> impl, ScoreboardComponentFactoryV2<? extends C> factory) {
      this.checkLoading(ScoreboardComponentFactoryRegistry.class, "registerForScoreboards");
      this.scoreboardFactories.component(type, impl, factory, Set.of());
   }

   @Override
   public <C extends Component> void registerTeamComponent(ComponentKey<C> type, TeamComponentFactoryV2<? extends C> factory) {
      this.registerTeamComponent(type, type.getComponentClass(), factory);
   }

   @Override
   public <C extends Component> void registerTeamComponent(ComponentKey<? super C> type, Class<C> impl, TeamComponentFactoryV2<? extends C> factory) {
      this.checkLoading(ScoreboardComponentFactoryRegistry.class, "register");
      this.teamFactories.component(type, impl, factory, Set.of());
   }
}
