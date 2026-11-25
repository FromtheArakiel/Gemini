package dev.arakiel.gemini.config.internal;

import dev.arakiel.gemini.config.ConfigHandle;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {
    private static final Map<ResourceLocation, ConfigDefinition<?>> CONFIGS = new ConcurrentHashMap<>();
    private static volatile Path root = Path.of("config");

    private ConfigManager() {
    }

    public static synchronized void init() {
        root = FabricLoader.getInstance().getConfigDir();
    }

    public static void configureRoot(Path path) {
        root = Objects.requireNonNull(path, "path").toAbsolutePath().normalize();
    }

    public static Path root() {
        return root;
    }

    public static synchronized <T> ConfigHandle<T> register(
            ResourceLocation id,
            boolean nested,
            Class<T> type
    ) {
        ConfigDefinition<?> existing = CONFIGS.get(id);
        if (existing != null) {
            if (existing.type() != type) {
                throw new IllegalStateException(
                        "Config id " + id + " is already registered to " + existing.type().getName()
                );
            }
            @SuppressWarnings("unchecked")
            ConfigHandle<T> handle = (ConfigHandle<T>) existing;
            return handle;
        }
        Path file = nested
                ? root.resolve(id.getNamespace()).resolve(id.getPath() + ".toml")
                : root.resolve(id.getNamespace() + "-" + id.getPath() + ".toml");
        ConfigDefinition<T> definition = new ConfigDefinition<>(id, file, type);
        CONFIGS.put(id, definition);
        return definition;
    }

    public static ConfigHandle<?> get(ResourceLocation id) {
        return CONFIGS.get(id);
    }

    public static <T> ConfigHandle<T> get(Class<T> type) {
        for (ConfigDefinition<?> definition : CONFIGS.values()) {
            if (definition.type() == type) {
                @SuppressWarnings("unchecked")
                ConfigHandle<T> handle = (ConfigHandle<T>) definition;
                return handle;
            }
        }
        return null;
    }

    public static Collection<ConfigHandle<?>> all() {
        List<ConfigHandle<?>> handles = new ArrayList<>(CONFIGS.size());
        for (ConfigDefinition<?> definition : CONFIGS.values()) {
            handles.add(definition);
        }
        return List.copyOf(handles);
    }

    public static void reloadAll() {
        for (ConfigDefinition<?> definition : CONFIGS.values()) {
            definition.reload();
        }
    }

    public static void saveAll() {
        for (ConfigDefinition<?> definition : CONFIGS.values()) {
            definition.save();
        }
    }
}
