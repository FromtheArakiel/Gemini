package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityRenderersEvent extends GeminiEvent {
    private final List<Registration<?>> registrations = new ArrayList<>();

    public <T extends Entity> void register(
            EntityType<? extends T> type,
            EntityRendererProvider<T> provider
    ) {
        this.registrations.add(new Registration<>(type, provider));
    }

    public List<Registration<?>> getRegistrations() {
        return List.copyOf(this.registrations);
    }

    public record Registration<T extends Entity>(
            EntityType<? extends T> type,
            EntityRendererProvider<T> provider
    ) {
        public Registration {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(provider, "provider");
        }
    }
}
