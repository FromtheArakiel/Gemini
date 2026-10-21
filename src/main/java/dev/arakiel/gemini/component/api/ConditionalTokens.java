package dev.arakiel.gemini.component.api;

import dev.arakiel.gemini.component.internal.token.ConditionalData;
import dev.arakiel.gemini.component.internal.token.ConditionalTokenRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class ConditionalTokens {
    private ConditionalTokens() {
    }

    public static <T extends ConditionalToken> TokenKey<T> key(
            ResourceLocation id,
            Class<T> type,
            Supplier<T> factory
    ) {
        return new TokenKey<>(id, type, factory);
    }

    public static <T extends ConditionalToken> T get(Player player, TokenKey<T> key) {
        return data(player).get(key);
    }

    public static <T extends ConditionalToken> T getOrCreate(Player player, TokenKey<T> key) {
        ConditionalData data = data(player);
        T token = data.getOrCreate(key);
        sync(player);
        return token;
    }

    public static boolean has(Player player, TokenKey<?> key) {
        return data(player).get(key) != null;
    }

    public static void remove(Player player, TokenKey<?> key) {
        if (data(player).remove(key) != null) {
            sync(player);
        }
    }

    public static void clear(Player player) {
        ConditionalData data = data(player);
        if (!data.isEmpty()) {
            data.clear();
            sync(player);
        }
    }

    public static List<TokenKey<?>> keys(Player player) {
        return data(player).keys();
    }

    public static void sync(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            conditionalKey().sync(serverPlayer);
        }
    }

    public static ComponentKey<ConditionalData> conditionalKey() {
        return ConditionalTokenRegistration.key();
    }

    private static ConditionalData data(Player player) {
        Objects.requireNonNull(player, "player");
        return conditionalKey().get(player);
    }
}