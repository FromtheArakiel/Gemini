package dev.arakiel.gemini.capability;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentKey;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class CapabilityComponents {
    private CapabilityComponents() {
    }

    public static <C extends Component, V> CapabilityProvider<V, BlockCapabilityContext> blockEntity(
            ComponentKey<C> key,
            Function<C, V> viewFactory
    ) {
        return blockEntity(key, (component, ignored) -> viewFactory.apply(component));
    }

    public static <C extends Component, V> CapabilityProvider<V, BlockCapabilityContext> blockEntity(
            ComponentKey<C> key,
            BiFunction<C, BlockCapabilityContext, V> viewFactory
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(viewFactory, "viewFactory");
        return context -> context.blockEntity() == null
                ? Optional.empty()
                : key.maybeGet(context.blockEntity()).map(component -> viewFactory.apply(component, context));
    }

    public static <C extends Component, V> CapabilityProvider<V, EntityCapabilityContext> entity(
            ComponentKey<C> key,
            Function<C, V> viewFactory
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(viewFactory, "viewFactory");
        return context -> key.maybeGet(context.entity()).map(viewFactory);
    }

    public static <C extends Component, V> CapabilityProvider<V, ItemCapabilityContext> itemStack(
            ComponentKey<C> key,
            Function<C, V> viewFactory
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(viewFactory, "viewFactory");
        return context -> key.maybeGet(context.stack()).map(viewFactory);
    }

    public static <C extends Component, V> CapabilityProvider<V, LevelCapabilityContext> level(
            ComponentKey<C> key,
            Function<C, V> viewFactory
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(viewFactory, "viewFactory");
        return context -> key.maybeGet(context.level()).map(viewFactory);
    }
}
