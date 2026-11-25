package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterParticleProvidersEvent extends GeminiEvent {
    private final List<Registration<?>> registrations = new ArrayList<>();

    public <T extends ParticleOptions> void register(
            ParticleType<T> type,
            ParticleProvider<T> provider
    ) {
        this.registrations.add(new Registration<>(type, provider));
    }

    public List<Registration<?>> getRegistrations() {
        return List.copyOf(this.registrations);
    }

    public record Registration<T extends ParticleOptions>(
            ParticleType<T> type,
            ParticleProvider<T> provider
    ) {
        public Registration {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(provider, "provider");
        }
    }
}
