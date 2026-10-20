package dev.arakiel.gemini.event.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class GeminiEventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger("gemini-eventbus");

    private final Object lock = new Object();
    private final Map<Class<? extends GeminiEvent>, List<Registration>> registrations = new ConcurrentHashMap<>();
    private final Map<Object, List<Registration>> owners = new IdentityHashMap<>();
    private final Map<Class<?>, Registration[]> listenerCache = new ConcurrentHashMap<>();
    private volatile boolean active = true;

    public void start() {
        active = true;
    }

    public void shutdown() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void register(Object target) {
        Objects.requireNonNull(target, "target");

        synchronized (lock) {
            if (owners.containsKey(target)) {
                return;
            }
        }

        boolean staticTarget = target instanceof Class<?>;
        Class<?> type = staticTarget ? (Class<?>) target : target.getClass();
        List<Registration> found = new ArrayList<>();

        for (Method method : type.getDeclaredMethods()) {
            SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
            if (annotation == null) {
                continue;
            }
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException("Subscribed method must accept exactly one event: " + method);
            }
            if (Modifier.isStatic(method.getModifiers()) != staticTarget) {
                throw new IllegalArgumentException("Subscribed method has the wrong static modifier: " + method);
            }

            Class<?> parameter = method.getParameterTypes()[0];
            if (!GeminiEvent.class.isAssignableFrom(parameter)) {
                throw new IllegalArgumentException("Subscribed method does not accept a GeminiEvent: " + method);
            }

            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            Class<? extends GeminiEvent> eventType = (Class<? extends GeminiEvent>) parameter;
            found.add(new Registration(
                    target,
                    eventType,
                    annotation.priority(),
                    annotation.receiveCanceled(),
                    new MethodListener(target, method)
            ));
        }

        if (found.isEmpty()) {
            throw new IllegalArgumentException("No @SubscribeEvent methods found in " + type.getName());
        }

        synchronized (lock) {
            if (owners.containsKey(target)) {
                return;
            }
            owners.put(target, List.copyOf(found));
            for (Registration registration : found) {
                registrations.computeIfAbsent(registration.eventType(), key -> new ArrayList<>()).add(registration);
            }
        }
        listenerCache.clear();
    }

    public <T extends GeminiEvent> Subscription listen(Class<T> eventType, Consumer<? super T> consumer) {
        return listen(eventType, EventPriority.NORMAL, false, consumer);
    }

    public <T extends GeminiEvent> Subscription listen(Class<T> eventType, EventPriority priority, Consumer<? super T> consumer) {
        return listen(eventType, priority, false, consumer);
    }

    public <T extends GeminiEvent> Subscription listen(
            Class<T> eventType,
            EventPriority priority,
            boolean receiveCanceled,
            Consumer<? super T> consumer
    ) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(consumer, "consumer");

        EventListener listener = event -> consumer.accept(eventType.cast(event));
        add(new Registration(consumer, eventType, priority, receiveCanceled, listener));
        return new Subscription(this, consumer);
    }

    public void unregister(Object owner) {
        List<Registration> removed;
        synchronized (lock) {
            removed = owners.remove(owner);
            if (removed == null) {
                return;
            }
            for (Registration registration : removed) {
                List<Registration> listeners = registrations.get(registration.eventType());
                if (listeners == null) {
                    continue;
                }
                listeners.remove(registration);
                if (listeners.isEmpty()) {
                    registrations.remove(registration.eventType());
                }
            }
        }
        listenerCache.clear();
    }

    public <T extends GeminiEvent> T post(T event) {
        Objects.requireNonNull(event, "event");
        if (!active) {
            return event;
        }

        Registration[] listeners = listenerCache.computeIfAbsent(event.getClass(), this::collectListeners);
        for (Registration registration : listeners) {
            if (event.isCanceled() && !registration.receiveCanceled()) {
                continue;
            }
            try {
                registration.listener().invoke(event);
            } catch (Throwable throwable) {
                LOGGER.error("Error while dispatching {} to a listener", event.getClass().getName(), throwable);
            }
        }
        return event;
    }

    public int listenerCount() {
        synchronized (lock) {
            int count = 0;
            for (List<Registration> listeners : registrations.values()) {
                count += listeners.size();
            }
            return count;
        }
    }

    private void add(Registration registration) {
        synchronized (lock) {
            owners.computeIfAbsent(registration.owner(), key -> new ArrayList<>()).add(registration);
            registrations.computeIfAbsent(registration.eventType(), key -> new ArrayList<>()).add(registration);
        }
        listenerCache.clear();
    }

    private Registration[] collectListeners(Class<?> eventClass) {
        List<Registration> collected = new ArrayList<>();
        synchronized (lock) {
            for (EventPriority priority : EventPriority.values()) {
                for (Class<?> type = eventClass;
                     type != null && GeminiEvent.class.isAssignableFrom(type);
                     type = type.getSuperclass()) {
                    List<Registration> listeners = registrations.get(type);
                    if (listeners == null) {
                        continue;
                    }
                    for (Registration listener : listeners) {
                        if (listener.priority() == priority) {
                            collected.add(listener);
                        }
                    }
                }
            }
        }
        return collected.toArray(Registration[]::new);
    }

    private record Registration(
            Object owner,
            Class<? extends GeminiEvent> eventType,
            EventPriority priority,
            boolean receiveCanceled,
            EventListener listener
    ) {
    }

    private record MethodListener(Object target, Method method) implements EventListener {
        @Override
        public void invoke(GeminiEvent event) {
            try {
                method.invoke(Modifier.isStatic(method.getModifiers()) ? null : target, event);
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Cannot access event listener: " + method, exception);
            } catch (InvocationTargetException exception) {
                Throwable cause = exception.getCause();
                if (cause instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                if (cause instanceof Error error) {
                    throw error;
                }
                throw new IllegalStateException("Event listener failed: " + method, cause);
            }
        }
    }
}
