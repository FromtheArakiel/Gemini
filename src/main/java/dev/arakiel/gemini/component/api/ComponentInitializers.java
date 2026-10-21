package dev.arakiel.gemini.component.api;

import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;

public final class ComponentInitializers {
    private ComponentInitializers() {
    }

    public interface Block extends ComponentRegistrationInitializer {
        void registerBlockComponentFactories(ComponentFactoryRegistries.Block registry);
    }

    public interface Chunk extends ComponentRegistrationInitializer {
        void registerChunkComponentFactories(ComponentFactoryRegistries.Chunk registry);
    }

    public interface Entity extends ComponentRegistrationInitializer {
        void registerEntityComponentFactories(ComponentFactoryRegistries.Entity registry);
    }

    public interface Level extends ComponentRegistrationInitializer {
        void registerLevelComponentFactories(ComponentFactoryRegistries.Level registry);
    }

    public interface Scoreboard extends ComponentRegistrationInitializer {
        void registerScoreboardComponentFactories(ComponentFactoryRegistries.Scoreboard registry);
    }

    public interface World extends ComponentRegistrationInitializer {
        void registerWorldComponentFactories(ComponentFactoryRegistries.World registry);
    }
}
