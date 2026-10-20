package dev.arakiel.gemini.component.scoreboard;

import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;

public interface ScoreboardComponentInitializer extends ComponentRegistrationInitializer {
   void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry var1);
}
