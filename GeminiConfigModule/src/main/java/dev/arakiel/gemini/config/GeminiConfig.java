package dev.arakiel.gemini.config;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.config.Config;
import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.internal.ConfigManager;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Collection;

public final class GeminiConfig {
    private GeminiConfig() {
    }

    public static void init() {
        ConfigManager.init();
    }

    public static <T> ConfigHandle<T> register(Class<T> type) {
        Config declaration = type.getAnnotation(Config.class);
        String namespace = declaration == null || declaration.namespace().isBlank()
                ? Gemini.MOD_ID
                : declaration.namespace();
        String name = declaration == null || declaration.value().isBlank()
                ? snakeCase(type.getSimpleName())
                : declaration.value();
        boolean nested = declaration == null || declaration.nested();
        return register(namespace, name, nested, type);
    }

    public static <T> ConfigHandle<T> register(String namespace, String name, Class<T> type) {
        return register(namespace, name, true, type);
    }

    public static <T> ConfigHandle<T> register(
            String namespace,
            String name,
            boolean nested,
            Class<T> type
    ) {
        return register(ResourceLocation.fromNamespaceAndPath(namespace, name), nested, type);
    }

    public static <T> ConfigHandle<T> register(ResourceLocation id, boolean nested, Class<T> type) {
        return ConfigManager.register(id, nested, type);
    }

    public static ConfigHandle<?> get(ResourceLocation id) {
        return ConfigManager.get(id);
    }

    public static <T> ConfigHandle<T> get(Class<T> type) {
        return ConfigManager.get(type);
    }

    public static Collection<ConfigHandle<?>> all() {
        return ConfigManager.all();
    }

    public static Path root() {
        return ConfigManager.root();
    }

    public static void reloadAll() {
        ConfigManager.reloadAll();
    }

    public static void saveAll() {
        ConfigManager.saveAll();
    }

    private static String snakeCase(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (Character.isUpperCase(character)) {
                if (!result.isEmpty()) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(character));
            } else {
                result.append(Character.toLowerCase(character));
            }
        }
        return result.toString();
    }
}
