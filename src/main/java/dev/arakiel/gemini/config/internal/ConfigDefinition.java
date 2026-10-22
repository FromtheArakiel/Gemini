package dev.arakiel.gemini.config.internal;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.config.ConfigEntry;
import dev.arakiel.gemini.config.ConfigHandle;
import dev.arakiel.gemini.config.ConfigOption;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class ConfigDefinition<T> implements ConfigHandle<T> {
    private final ResourceLocation id;
    private final Path path;
    private final Class<T> type;
    private final T instance;
    private final CommentedConfig file;
    private final ConfigSpec spec;
    private final List<ConfigField> fields;
    private final Map<String, ConfigField> fieldsByPath;
    private final List<Consumer<? super T>> listeners = new CopyOnWriteArrayList<>();
    private final Map<String, Object> restartBaseline = new LinkedHashMap<>();

    public ConfigDefinition(ResourceLocation id, Path path, Class<T> type) {
        this.id = Objects.requireNonNull(id, "id");
        this.path = Objects.requireNonNull(path, "path").toAbsolutePath().normalize();
        this.type = Objects.requireNonNull(type, "type");
        this.instance = instantiate(type);
        this.fields = discoverFields(instance, type, id.getNamespace(), id.getPath());
        this.fieldsByPath = indexFields(fields);
        try {
            Path parent = this.path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            this.file = CommentedConfig.inMemory();
            if (Files.exists(this.path)) {
                load();
            }
            this.spec = createSpec(fields);
            int corrections = spec.correct(file);
            applyComments(file, fields);
            readIntoInstance();
            resetRestartBaseline();
            if (corrections > 0 || !Files.exists(this.path)) {
                writeFieldsToConfig();
                saveFile();
            }
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Failed to load config " + id + " from " + this.path, exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to create config directory for " + this.path, exception);
        }
    }

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public T value() {
        return instance;
    }

    @Override
    public synchronized List<ConfigOption> options() {
        List<ConfigOption> options = new ArrayList<>(fields.size());
        for (ConfigField field : fields) {
            options.add(field.option(field.read(instance)));
        }
        return List.copyOf(options);
    }

    @Override
    public synchronized Object get(String path) {
        return field(path).read(instance);
    }

    @Override
    public synchronized void stage(String path, Object value) {
        ConfigField field = field(path);
        Object normalized = field.normalize(value);
        field.write(instance, normalized);
        file.set(field.path(), field.toTomlValue(normalized));
        notifyListeners();
    }

    @Override
    public synchronized void set(String path, Object value) {
        stage(path, value);
        save();
    }

    @Override
    public synchronized void save() {
        writeFieldsToConfig();
        saveFile();
    }

    @Override
    public synchronized void reload() {
        file.clear();
        if (Files.exists(path)) {
            load();
        }
        spec.correct(file);
        applyComments(file, fields);
        readIntoInstance();
        resetRestartBaseline();
        writeFieldsToConfig();
        saveFile();
        notifyListeners();
    }

    @Override
    public synchronized void reset(String path) {
        ConfigField field = field(path);
        field.write(instance, field.defaultValue());
        file.set(field.path(), field.toTomlValue(field.defaultValue()));
        notifyListeners();
    }

    @Override
    public synchronized void resetAll() {
        for (ConfigField field : fields) {
            field.write(instance, field.defaultValue());
            file.set(field.path(), field.toTomlValue(field.defaultValue()));
        }
        notifyListeners();
    }

    @Override
    public boolean needsRestart() {
        for (ConfigField field : fields) {
            if (field.restartRequired()
                    && !field.valuesEqual(field.read(instance), restartBaseline.get(field.pathString()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addListener(Consumer<? super T> listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    @Override
    public void removeListener(Consumer<? super T> listener) {
        listeners.remove(listener);
    }

    private synchronized void readIntoInstance() {
        for (ConfigField field : fields) {
            Object raw = file.get(field.path());
            if (raw == null) {
                raw = field.defaultValue();
                file.set(field.path(), field.toTomlValue(raw));
            }
            field.write(instance, raw);
        }
    }

    private synchronized void writeFieldsToConfig() {
        for (ConfigField field : fields) {
            file.set(field.path(), field.toTomlValue(field.read(instance)));
        }
    }

    private synchronized void resetRestartBaseline() {
        restartBaseline.clear();
        for (ConfigField field : fields) {
            if (field.restartRequired()) {
                restartBaseline.put(field.pathString(), field.normalize(field.read(instance)));
            }
        }
    }

    private ConfigField field(String path) {
        ConfigField field = fieldsByPath.get(path);
        if (field == null) {
            throw new IllegalArgumentException("Unknown config option " + path + " in " + id);
        }
        return field;
    }

    private void notifyListeners() {
        for (Consumer<? super T> listener : listeners) {
            try {
                listener.accept(instance);
            } catch (RuntimeException exception) {
                Gemini.LOGGER.error("Config listener failed for {}", id, exception);
            }
        }
    }

    private static ConfigSpec createSpec(List<ConfigField> fields) {
        ConfigSpec spec = new ConfigSpec();
        for (ConfigField field : fields) {
            Object defaultValue = field.toTomlValue(field.defaultValue());
            if (field.type().name().endsWith("_LIST")) {
                if (!(defaultValue instanceof List<?> list)) {
                    throw new IllegalStateException("Invalid config list default for " + field.field());
                }
                spec.defineList(field.path(), list, field::isValidElement);
            } else {
                spec.define(field.path(), defaultValue, field::isValidValue);
            }
        }
        return spec;
    }

    private static void applyComments(CommentedConfig file, List<ConfigField> fields) {
        for (ConfigField field : fields) {
            if (!field.comment().isBlank()) {
                file.setComment(field.path(), field.comment());
            }
        }
    }

    private void load() {
        try (Reader reader = Files.newBufferedReader(this.path)) {
            new TomlParser().parse(reader, this.file, ParsingMode.REPLACE);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read config " + this.id, exception);
        }
    }

    private void saveFile() {
        new TomlWriter().write(this.file, this.path, WritingMode.REPLACE_ATOMIC);
    }

    private static List<ConfigField> discoverFields(
            Object instance,
            Class<?> type,
            String namespace,
            String configName
    ) {
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            hierarchy.add(0, current);
        }
        List<Field> allFields = new ArrayList<>();
        boolean explicitFields = false;
        for (Class<?> current : hierarchy) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())
                        || field.isSynthetic()) {
                    continue;
                }
                allFields.add(field);
                explicitFields |= field.isAnnotationPresent(ConfigEntry.class);
            }
        }
        List<ConfigField> result = new ArrayList<>();
        for (Field field : allFields) {
            ConfigEntry entry = field.getAnnotation(ConfigEntry.class);
            if (explicitFields && entry == null) {
                continue;
            }
            result.add(ConfigField.of(
                    instance,
                    field,
                    namespace,
                    configName,
                    entry == null ? "" : entry.value(),
                    entry == null ? "" : entry.translation(),
                    entry == null ? "" : entry.comment()
            ));
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Config class has no configurable fields: " + type.getName());
        }
        return List.copyOf(result);
    }

    private static Map<String, ConfigField> indexFields(List<ConfigField> fields) {
        Map<String, ConfigField> indexed = new LinkedHashMap<>();
        for (ConfigField field : fields) {
            ConfigField previous = indexed.putIfAbsent(field.pathString(), field);
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate config path " + field.pathString());
            }
        }
        return Map.copyOf(indexed);
    }

    private static <T> T instantiate(Class<T> type) {
        try {
            var constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("Config class needs a no-argument constructor: " + type.getName(), exception);
        }
    }
}
