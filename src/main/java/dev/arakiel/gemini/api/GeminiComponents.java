package dev.arakiel.gemini.api;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.api.ComponentRegistry;
import dev.arakiel.gemini.component.api.ConditionalToken;
import dev.arakiel.gemini.component.api.ConditionalTokens;
import dev.arakiel.gemini.component.api.TokenKey;
import dev.arakiel.gemini.component.api.ComponentSync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Supplier;
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

    public static <T extends ConditionalToken> TokenKey<T> token(
            ResourceLocation id,
            Class<T> type,
            Supplier<T> factory
    ) {
        return ConditionalTokens.key(id, type, factory);
    }

    public static <T extends ConditionalToken> T getToken(Player player, TokenKey<T> key) {
        return ConditionalTokens.get(player, key);
    }

    public static <T extends ConditionalToken> T getOrCreateToken(Player player, TokenKey<T> key) {
        return ConditionalTokens.getOrCreate(player, key);
    }

    public static void removeToken(Player player, TokenKey<?> key) {
        ConditionalTokens.remove(player, key);
    }

    public static List<TokenKey<?>> tokens(Player player) {
        return ConditionalTokens.keys(player);
    }

    public static void sync(ComponentKey<?> key, Object provider) {
        key.sync(provider);
    }

    public static void sync(ComponentKey<?> key, Object provider, ComponentSync.PacketWriter writer) {
        key.sync(provider, writer);
    }

    public static void sync(
            ComponentKey<?> key,
            Object provider,
            ComponentSync.PacketWriter writer,
            ComponentSync.PlayerPredicate predicate
    ) {
        key.sync(provider, writer, predicate);
    }
}