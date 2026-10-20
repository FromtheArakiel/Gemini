package dev.arakiel.gemini.api;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.ComponentRegistry;
import dev.arakiel.gemini.component.sync.ComponentPacketWriter;
import dev.arakiel.gemini.component.sync.PlayerSyncPredicate;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

public final class GeminiComponents {
    private GeminiComponents() {
    }

    public static <C extends Component> ComponentKey<C> key(String path, Class<C> type) {
        return ComponentRegistry.getOrCreate(Gemini.id(path), type);
    }

    public static <C extends Component> ComponentKey<C> key(String namespace, String path, Class<C> type) {
        return ComponentRegistry.getOrCreate(ResourceLocation.fromNamespaceAndPath(namespace, path), type);
    }

    public static <C extends Component> ComponentKey<C> key(ResourceLocation id, Class<C> type) {
        return ComponentRegistry.getOrCreate(id, type);
    }

    public static ComponentKey<?> find(ResourceLocation id) {
        return ComponentRegistry.get(id);
    }

    public static Stream<ComponentKey<?>> keys() {
        return ComponentRegistry.stream();
    }

    public static <C extends Component> C get(ComponentKey<C> key, Object provider) {
        return key.get(provider);
    }

    public static <C extends Component> C getNullable(ComponentKey<C> key, Object provider) {
        return key.getNullable(provider);
    }

    public static ComponentProvider provider(Object object) {
        return (ComponentProvider) object;
    }

    public static void sync(ComponentKey<?> key, Object provider) {
        key.sync(provider);
    }

    public static void sync(ComponentKey<?> key, Object provider, ComponentPacketWriter writer) {
        key.sync(provider, writer);
    }

    public static void sync(
            ComponentKey<?> key,
            Object provider,
            ComponentPacketWriter writer,
            PlayerSyncPredicate predicate
    ) {
        key.sync(provider, writer, predicate);
    }
}
