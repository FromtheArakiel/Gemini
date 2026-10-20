package dev.arakiel.gemini.component.scoreboard;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentKey;

public interface ScoreboardComponentFactoryRegistry {
   <C extends Component> void registerTeamComponent(ComponentKey<C> var1, TeamComponentFactoryV2<? extends C> var2);

   <C extends Component> void registerTeamComponent(ComponentKey<? super C> var1, Class<C> var2, TeamComponentFactoryV2<? extends C> var3);

   <C extends Component> void registerScoreboardComponent(ComponentKey<C> var1, ScoreboardComponentFactoryV2<? extends C> var2);

   <C extends Component> void registerScoreboardComponent(ComponentKey<? super C> var1, Class<C> var2, ScoreboardComponentFactoryV2<? extends C> var3);
}
