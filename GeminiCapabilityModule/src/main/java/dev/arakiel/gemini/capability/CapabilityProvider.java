package dev.arakiel.gemini.capability;

import java.util.Optional;

@FunctionalInterface
public interface CapabilityProvider<T, C extends CapabilityContext> {
    Optional<T> find(C context);
}
