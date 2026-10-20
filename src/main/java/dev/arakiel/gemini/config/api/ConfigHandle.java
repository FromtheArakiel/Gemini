package dev.arakiel.gemini.config.api;

import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface ConfigHandle<T> {
    ResourceLocation id();

    Path path();

    Class<T> type();

    T value();

    List<ConfigOption> options();

    Object get(String path);

    void stage(String path, Object value);

    void set(String path, Object value);

    void save();

    void reload();

    void reset(String path);

    void resetAll();

    boolean needsRestart();

    void addListener(Consumer<? super T> listener);

    void removeListener(Consumer<? super T> listener);
}
