package dev.arakiel.gemini.config.internal;

import dev.arakiel.gemini.config.api.ConfigEntry;
import dev.arakiel.gemini.config.api.ConfigOption;
import dev.arakiel.gemini.config.api.ConfigValueType;
import dev.arakiel.gemini.config.api.Hidden;
import dev.arakiel.gemini.config.api.Range;
import dev.arakiel.gemini.config.api.RestartRequired;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

final class ConfigField {
    private final Field field;
    private final List<String> path;
    private final String translationKey;
    private final String comment;
    private final ConfigValueType type;
    private final Object defaultValue;
    private final double min;
    private final double max;
    private final List<String> choices;
    private final boolean hidden;
    private final boolean restartRequired;

    private ConfigField(
            Field field,
            List<String> path,
            String translationKey,
            String comment,
            ConfigValueType type,
            Object defaultValue,
            double min,
            double max,
            List<String> choices,
            boolean hidden,
            boolean restartRequired
    ) {
        this.field = field;
        this.path = List.copyOf(path);
        this.translationKey = translationKey;
        this.comment = comment;
        this.type = type;
        this.defaultValue = deepCopy(defaultValue);
        this.min = min;
        this.max = max;
        this.choices = List.copyOf(choices);
        this.hidden = hidden;
        this.restartRequired = restartRequired;
    }

    static ConfigField of(
            Object instance,
            Field field,
            String namespace,
            String configName,
            String annotationPath,
            String annotationTranslation,
            String annotationComment
    ) {
        field.setAccessible(true);
        String path = annotationPath.isBlank() ? field.getName() : annotationPath;
        String translation = annotationTranslation.isBlank()
                ? namespace + ".config." + configName + "." + path
                : annotationTranslation;
        ConfigValueType type = resolveType(field);
        Range range = field.getAnnotation(Range.class);
        double min = range == null ? -Double.MAX_VALUE : range.min();
        double max = range == null ? Double.MAX_VALUE : range.max();
        List<String> choices = type == ConfigValueType.ENUM ? enumChoices(field) : List.of();
        try {
            return new ConfigField(
                    field,
                    splitPath(path),
                    translation,
                    annotationComment,
                    type,
                    normalize(field.get(instance), type, field, min, max),
                    min,
                    max,
                    choices,
                    field.isAnnotationPresent(Hidden.class),
                    field.isAnnotationPresent(RestartRequired.class)
            );
        } catch (IllegalAccessException exception) {
            throw new IllegalArgumentException("Cannot read config field " + field, exception);
        }
    }

    Field field() {
        return field;
    }

    List<String> path() {
        return path;
    }

    String pathString() {
        return String.join(".", path);
    }

    String translationKey() {
        return translationKey;
    }

    String comment() {
        return comment;
    }

    ConfigValueType type() {
        return type;
    }

    Object defaultValue() {
        return deepCopy(defaultValue);
    }

    double min() {
        return min;
    }

    double max() {
        return max;
    }

    List<String> choices() {
        return choices;
    }

    boolean hidden() {
        return hidden;
    }

    boolean restartRequired() {
        return restartRequired;
    }

    Object read(Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot read config field " + field, exception);
        }
    }

    void write(Object instance, Object value) {
        try {
            field.set(instance, normalize(value, type, field, min, max));
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot write config field " + field, exception);
        }
    }

    Object normalize(Object value) {
        return normalize(value, type, field, min, max);
    }

    Object toTomlValue(Object value) {
        Object normalized = normalize(value);
        if (normalized instanceof Enum<?> enumValue) {
            return enumValue.name();
        }
        return normalized;
    }

    boolean isValidValue(Object value) {
        try {
            normalize(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    boolean isValidElement(Object value) {
        try {
            normalize(List.of(value));
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    ConfigOption option(Object value) {
        return new ConfigOption(
                pathString(),
                translationKey,
                comment,
                type,
                value,
                defaultValue,
                min,
                max,
                choices,
                hidden,
                restartRequired
        );
    }

    boolean valuesEqual(Object left, Object right) {
        return Objects.equals(deepCopy(left), deepCopy(right));
    }

    private static ConfigValueType resolveType(Field field) {
        Class<?> raw = field.getType();
        if (raw == boolean.class || raw == Boolean.class) {
            return ConfigValueType.BOOLEAN;
        }
        if (raw == byte.class || raw == Byte.class
                || raw == short.class || raw == Short.class
                || raw == int.class || raw == Integer.class) {
            return ConfigValueType.INTEGER;
        }
        if (raw == long.class || raw == Long.class) {
            return ConfigValueType.LONG;
        }
        if (raw == float.class || raw == Float.class) {
            return ConfigValueType.FLOAT;
        }
        if (raw == double.class || raw == Double.class) {
            return ConfigValueType.DOUBLE;
        }
        if (raw == String.class) {
            return ConfigValueType.STRING;
        }
        if (raw.isEnum()) {
            return ConfigValueType.ENUM;
        }
        if (List.class.isAssignableFrom(raw)) {
            Class<?> element = listElement(field);
            if (element == boolean.class || element == Boolean.class) {
                return ConfigValueType.BOOLEAN_LIST;
            }
            if (element == byte.class || element == Byte.class
                    || element == short.class || element == Short.class
                    || element == int.class || element == Integer.class) {
                return ConfigValueType.INTEGER_LIST;
            }
            if (element == long.class || element == Long.class) {
                return ConfigValueType.LONG_LIST;
            }
            if (element == double.class || element == Double.class
                    || element == float.class || element == Float.class) {
                return ConfigValueType.DOUBLE_LIST;
            }
            if (element == String.class) {
                return ConfigValueType.STRING_LIST;
            }
        }
        throw new IllegalArgumentException("Unsupported config field type: " + field);
    }

    private static Class<?> listElement(Field field) {
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType parameterized
                && parameterized.getActualTypeArguments()[0] instanceof Class<?> element) {
            return element;
        }
        throw new IllegalArgumentException("Config list fields must declare an element type: " + field);
    }

    private static List<String> enumChoices(Field field) {
        List<String> values = new ArrayList<>();
        for (Object constant : field.getType().getEnumConstants()) {
            values.add(((Enum<?>) constant).name());
        }
        return List.copyOf(values);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object normalize(
            Object value,
            ConfigValueType type,
            Field field,
            double min,
            double max
    ) {
        if (value == null) {
            throw new IllegalArgumentException("Config value cannot be null: " + field);
        }
        if (type == ConfigValueType.BOOLEAN) {
            if (!(value instanceof Boolean)) {
                throw typeError(field, value);
            }
            return value;
        }
        if (type == ConfigValueType.STRING) {
            if (!(value instanceof String)) {
                throw typeError(field, value);
            }
            return value;
        }
        if (type == ConfigValueType.ENUM) {
            Class<? extends Enum> enumType = field.getType().asSubclass(Enum.class);
            if (value instanceof Enum<?> enumValue && enumValue.getDeclaringClass() == enumType) {
                return enumValue;
            }
            if (value instanceof String name) {
                for (Object constant : enumType.getEnumConstants()) {
                    if (((Enum<?>) constant).name().equalsIgnoreCase(name)) {
                        return constant;
                    }
                }
            }
            throw typeError(field, value);
        }
        if (type == ConfigValueType.INTEGER || type == ConfigValueType.LONG) {
            if (!(value instanceof Number number)) {
                throw typeError(field, value);
            }
            long converted = number.longValue();
            checkRange(converted, min, max, field);
            if (type == ConfigValueType.LONG) {
                return converted;
            }
            if (converted < Integer.MIN_VALUE || converted > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Integer config value out of range: " + field);
            }
            if (field.getType() == byte.class || field.getType() == Byte.class) {
                if (converted < Byte.MIN_VALUE || converted > Byte.MAX_VALUE) {
                    throw new IllegalArgumentException("Byte config value out of range: " + field);
                }
                return (byte) converted;
            }
            if (field.getType() == short.class || field.getType() == Short.class) {
                if (converted < Short.MIN_VALUE || converted > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Short config value out of range: " + field);
                }
                return (short) converted;
            }
            return (int) converted;
        }
        if (type == ConfigValueType.FLOAT || type == ConfigValueType.DOUBLE) {
            if (!(value instanceof Number number)) {
                throw typeError(field, value);
            }
            double converted = number.doubleValue();
            checkRange(converted, min, max, field);
            return type == ConfigValueType.FLOAT ? (float) converted : converted;
        }
        if (type == ConfigValueType.BOOLEAN_LIST) {
            return normalizeList(value, field, item -> item instanceof Boolean);
        }
        if (type == ConfigValueType.INTEGER_LIST) {
            List<Object> result = new ArrayList<>();
            Class<?> elementType = listElement(field);
            for (Object item : normalizeList(value, field, candidate -> candidate instanceof Number)) {
                long converted = ((Number) item).longValue();
                if (elementType == byte.class || elementType == Byte.class) {
                    if (converted < Byte.MIN_VALUE || converted > Byte.MAX_VALUE) {
                        throw new IllegalArgumentException("Byte list config value out of range: " + field);
                    }
                    result.add((byte) converted);
                    continue;
                }
                if (elementType == short.class || elementType == Short.class) {
                    if (converted < Short.MIN_VALUE || converted > Short.MAX_VALUE) {
                        throw new IllegalArgumentException("Short list config value out of range: " + field);
                    }
                    result.add((short) converted);
                    continue;
                }
                if (converted < Integer.MIN_VALUE || converted > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("Integer list config value out of range: " + field);
                }
                result.add((int) converted);
            }
            return List.copyOf(result);
        }
        if (type == ConfigValueType.LONG_LIST) {
            return normalizeList(value, field, item -> item instanceof Number).stream()
                    .map(item -> ((Number) item).longValue())
                    .toList();
        }
        if (type == ConfigValueType.DOUBLE_LIST) {
            List<Object> result = new ArrayList<>();
            boolean floatElement = listElement(field) == float.class || listElement(field) == Float.class;
            for (Object item : normalizeList(value, field, candidate -> candidate instanceof Number)) {
                double converted = ((Number) item).doubleValue();
                result.add(floatElement ? (float) converted : converted);
            }
            return List.copyOf(result);
        }
        if (type == ConfigValueType.STRING_LIST) {
            return normalizeList(value, field, item -> item instanceof String);
        }
        throw new IllegalArgumentException("Unsupported config value type: " + type);
    }

    private static List<?> normalizeList(Object value, Field field, java.util.function.Predicate<Object> predicate) {
        if (!(value instanceof Collection<?> collection)) {
            throw typeError(field, value);
        }
        List<Object> result = new ArrayList<>(collection.size());
        for (Object item : collection) {
            if (!predicate.test(item)) {
                throw typeError(field, item);
            }
            result.add(item);
        }
        return List.copyOf(result);
    }

    private static void checkRange(double value, double min, double max, Field field) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    "Config value for " + field + " must be between " + min + " and " + max
            );
        }
    }

    private static IllegalArgumentException typeError(Field field, Object value) {
        return new IllegalArgumentException(
                "Invalid config value for " + field + ": " + value.getClass().getName()
        );
    }

    private static List<String> splitPath(String path) {
        if (path.isBlank()) {
            throw new IllegalArgumentException("Config path cannot be blank");
        }
        List<String> parts = List.of(path.split("\\."));
        if (parts.stream().anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("Invalid config path: " + path);
        }
        return parts;
    }

    private static Object deepCopy(Object value) {
        if (value instanceof List<?> list) {
            return List.copyOf(new ArrayList<>(list));
        }
        return value;
    }
}
