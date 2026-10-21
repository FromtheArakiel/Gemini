package dev.arakiel.gemini.component.internal.token;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.component.GeminiComponents;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.StaticComponentInitializer;
import dev.arakiel.gemini.component.ComponentFactoryRegistries;
import dev.arakiel.gemini.component.ComponentInitializers;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;

public final class ConditionalTokenRegistration implements
        StaticComponentInitializer,
        ComponentInitializers.Entity {
    private static final ResourceLocation ID = Gemini.id("conditional_tokens");
    private static ComponentKey<ConditionalData> key;

    public static ComponentKey<ConditionalData> key() {
        if (key == null) {
            key = GeminiComponents.key(ID, ConditionalData.class);
        }
        return key;
    }

    @Override
    public Collection<ResourceLocation> getSupportedComponentKeys() {
        return List.of(ID);
    }

    @Override
    public void finalizeStaticBootstrap() {
        key();
    }

    @Override
    public void registerEntityComponentFactories(ComponentFactoryRegistries.Entity registry) {
        registry.registerForPlayers(key(), ConditionalData::new);
    }
}
