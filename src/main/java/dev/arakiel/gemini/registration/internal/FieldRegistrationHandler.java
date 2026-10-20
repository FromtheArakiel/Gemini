package dev.arakiel.gemini.registration.internal;

import dev.arakiel.gemini.registration.api.AutoRegistryContainer;
import dev.arakiel.gemini.registration.api.FieldProcessingSubject;
import dev.arakiel.gemini.registration.api.RegistrySink;
import dev.arakiel.gemini.registration.api.SimpleFieldProcessingSubject;
import dev.arakiel.gemini.registration.annotations.AssignedName;
import dev.arakiel.gemini.registration.annotations.IterationIgnored;
import dev.arakiel.gemini.registration.annotations.RegistryNamespace;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public final class FieldRegistrationHandler {
    private FieldRegistrationHandler() {
    }

    public static <T> void process(Class<? extends FieldProcessingSubject<T>> container, FieldConsumer<T> processor, boolean recurseIntoInnerClasses) {
        FieldProcessingSubject<T> handler = instantiate(container);
        scanFields(container, handler.getTargetFieldType(), (value, name, field) -> {
            if (handler.shouldProcessField(value, name, field)) {
                processor.accept(value, name, field);
            }
        });

        if (recurseIntoInnerClasses) {
            processInnerClasses(container, FieldProcessingSubject.class, subclass ->
                    process((Class<? extends FieldProcessingSubject<T>>) subclass, processor, true));
        }

        handler.afterFieldProcessing();
    }

    public static <T> void processSimple(Class<? extends SimpleFieldProcessingSubject<T>> container, boolean recurseIntoInnerClasses) {
        SimpleFieldProcessingSubject<T> handler = instantiate(container);
        scanFields(container, handler.getTargetFieldType(), (value, name, field) -> {
            if (handler.shouldProcessField(value, name, field)) {
                handler.processField(value, name, field);
            }
        });

        if (recurseIntoInnerClasses) {
            processInnerClasses(container, SimpleFieldProcessingSubject.class, subclass ->
                    processSimple((Class<? extends SimpleFieldProcessingSubject<T>>) subclass, true));
        }

        handler.afterFieldProcessing();
    }

    public static <T> void register(Class<? extends AutoRegistryContainer<T>> container, String namespace, boolean recurseIntoInnerClasses) {
        AutoRegistryContainer<T> handler = instantiate(container);
        RegistrySink<T> sink = handler.getSink();
        scanFields(container, handler.getTargetFieldType(), (value, name, field) -> {
            if (!handler.shouldProcessField(value, name, field)) {
                return;
            }

            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, name);
            sink.register(id, value);
            handler.postProcessField(namespace, value, name, field);
        });

        if (recurseIntoInnerClasses) {
            processInnerClasses(container, AutoRegistryContainer.class, subclass -> {
                String classNamespace = namespace;
                if (subclass.isAnnotationPresent(RegistryNamespace.class)) {
                    classNamespace = subclass.getAnnotation(RegistryNamespace.class).value();
                }
                register((Class<? extends AutoRegistryContainer<T>>) subclass, classNamespace, true);
            });
        }

        handler.afterFieldProcessing();
    }

    private static <T> void processInnerClasses(
            Class<? extends FieldProcessingSubject<T>> parent,
            Class<?> target,
            Consumer<Class<?>> action
    ) {
        for (Class<?> subclass : parent.getDeclaredClasses()) {
            if (target.isAssignableFrom(subclass)) {
                action.accept(subclass);
            }
        }
    }

    public static <C, T> void scanFields(Class<C> container, Class<T> targetFieldType, FieldConsumer<T> consumer) {
        for (Field field : container.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            if (!Modifier.isPublic(field.getModifiers())) {
                continue;
            }

            T value;
            try {
                field.setAccessible(true);
                value = (T) field.get(null);
            } catch (IllegalAccessException ignored) {
                continue;
            }

            if (value == null || !targetFieldType.isInstance(value) || field.isAnnotationPresent(IterationIgnored.class)) {
                continue;
            }

            consumer.accept(value, getFieldName(field), field);
        }
    }

    public static String getFieldName(Field field) {
        if (field.isAnnotationPresent(AssignedName.class)) {
            return field.getAnnotation(AssignedName.class).value();
        }
        return field.getName().toLowerCase(Locale.ROOT);
    }

    private static <T> T instantiate(Class<? extends T> container) {
        Constructor<? extends T> constructor;
        try {
            constructor = container.getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Registry container has no zero-arg constructor: " + container.getName(), exception);
        }

        try {
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Cannot create registry container: " + container.getName(), exception);
        }
    }

    @FunctionalInterface
    public interface FieldConsumer<T> {
        void accept(T value, String identifier, Field field);
    }
}
