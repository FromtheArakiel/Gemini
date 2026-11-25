package dev.arakiel.gemini.capability.internal;

import dev.arakiel.gemini.capability.CapabilityContext;
import dev.arakiel.gemini.capability.CapabilityKey;
import dev.arakiel.gemini.capability.CapabilityPriority;
import dev.arakiel.gemini.capability.CapabilityProvider;

import java.util.Optional;

public record CapabilityRegistration<T>(
        CapabilityKey<T> key,
        CapabilityRegistryState.Source source,
        CapabilityProvider<T, ? extends CapabilityContext> provider,
        CapabilityPriority priority,
        long order
) {
    public Optional<?> find(CapabilityContext context) {
        try {
            return this.findTyped(context);
        } catch (RuntimeException exception) {
            dev.arakiel.gemini.Gemini.LOGGER.error(
                    "Capability provider {} failed for source {}",
                    this.key.id(),
                    this.source.displayName(),
                    exception
            );
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<?> findTyped(CapabilityContext context) {
        CapabilityProvider<T, CapabilityContext> typed = (CapabilityProvider<T, CapabilityContext>) this.provider;
        return typed.find(context);
    }
}
