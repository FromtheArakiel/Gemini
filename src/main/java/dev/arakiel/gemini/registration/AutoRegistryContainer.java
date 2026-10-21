package dev.arakiel.gemini.registration;

import dev.arakiel.gemini.registration.internal.FieldRegistrationHandler;
import net.minecraft.core.Registry;

import java.lang.reflect.Field;

public interface AutoRegistryContainer<T> extends FieldProcessingSubject<T> {
    Registry<T> getRegistry();

    default RegistrySink<T> getSink() {
        return RegistrySink.from(getRegistry());
    }

    default void postProcessField(String namespace, T value, String identifier, Field field) {
    }

    static <T> void register(Class<? extends AutoRegistryContainer<T>> container, String namespace, boolean recurseIntoInnerClasses) {
        FieldRegistrationHandler.register(container, namespace, recurseIntoInnerClasses);
    }

    @SuppressWarnings("unchecked")
    static <T> Class<T> conform(Class<?> input) {
        return (Class<T>) input;
    }
}
