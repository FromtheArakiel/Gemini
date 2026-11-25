package dev.arakiel.gemini.capability;

import dev.arakiel.gemini.capability.internal.CapabilityRegistration;

public final class CapabilityProviderHandle {
    private final CapabilityRegistry registry;
    private final CapabilityRegistration<?> registration;

    CapabilityProviderHandle(CapabilityRegistry registry, CapabilityRegistration<?> registration) {
        this.registry = registry;
        this.registration = registration;
    }

    public void invalidate() {
        this.registry.invalidate(this);
    }

    public boolean unregister() {
        return this.registry.unregister(this);
    }

    public boolean isRegistered() {
        return this.registry.isRegistered(this);
    }

    @Override
    public String toString() {
        return "CapabilityProviderHandle[" + this.registration.key().id() + "]";
    }
}
