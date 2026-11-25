package dev.arakiel.gemini.component.internal.base;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentLoadingException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ComponentPluginSupport {
    private ComponentPluginSupport() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addToBuilder(
            ComponentContainer.Factory.Builder<?> builder,
            Map.Entry<?, ?> entry
    ) {
        ComponentKey key = (ComponentKey) entry.getKey();
        QualifiedComponentFactory<?> qualified = (QualifiedComponentFactory<?>) entry.getValue();
        ((ComponentContainer.Factory.Builder) builder).component(
                key,
                qualified.impl(),
                qualified.factory(),
                qualified.dependencies()
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void putFactory(
            Map<?, ?> factories,
            Object target,
            ComponentKey<?> key,
            QualifiedComponentFactory<?> factory
    ) {
        Map<ComponentKey<?>, QualifiedComponentFactory<?>> specialized =
                (Map<ComponentKey<?>, QualifiedComponentFactory<?>>) ((Map) factories)
                        .computeIfAbsent(target, ignored -> new LinkedHashMap<>());
        QualifiedComponentFactory<?> previous = specialized.get(key);
        if (previous != null) {
            throw new StaticComponentLoadingException(
                    "Duplicate factory declarations for %s on %s: %s and %s"
                            .formatted(key.getId(), target, factory, previous)
            );
        }
        specialized.put(key, factory);
    }

    public static <T> Map<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends T, ?>>> collectClassHierarchy(
            Map<Class<? extends T>, Map<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends T, ?>>>> factories,
            Class<? extends T> concrete,
            Class<T> base
    ) {
        Map<ComponentKey<?>, QualifiedComponentFactory<ComponentFactory<? extends T, ?>>> compiled =
                new LinkedHashMap<>(factories.getOrDefault(concrete, Collections.emptyMap()));
        Class<?> current = concrete;
        while (current != base) {
            current = current.getSuperclass();
            if (current == null) {
                break;
            }
            factories.getOrDefault(current, Collections.emptyMap()).forEach(compiled::putIfAbsent);
        }
        return compiled;
    }
}
