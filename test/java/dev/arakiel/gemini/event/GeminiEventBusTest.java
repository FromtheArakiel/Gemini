package dev.arakiel.gemini.event;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.EventPriority;
import dev.arakiel.gemini.event.GeminiEvent;
import dev.arakiel.gemini.event.GeminiEventBus;
import dev.arakiel.gemini.event.SubscribeEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiEventBusTest {
    @Test
    void listenersRunInPriorityOrder() {
        GeminiEventBus bus = new GeminiEventBus();
        List<String> order = new ArrayList<>();
        bus.listen(TestEvent.class, EventPriority.LOW, event -> order.add("low"));
        bus.listen(TestEvent.class, EventPriority.HIGHEST, event -> order.add("highest"));
        bus.listen(TestEvent.class, EventPriority.NORMAL, event -> order.add("normal"));

        bus.post(new TestEvent());

        assertEquals(List.of("highest", "normal", "low"), order);
    }

    @Test
    void canceledEventSkipsNormalListenersButReachesCanceledListeners() {
        GeminiEventBus bus = new GeminiEventBus();
        List<String> order = new ArrayList<>();
        bus.listen(CancelableEvent.class, EventPriority.HIGHEST, event -> {
            order.add("cancel");
            event.setCanceled(true);
        });
        bus.listen(CancelableEvent.class, EventPriority.NORMAL, event -> order.add("normal"));
        bus.listen(CancelableEvent.class, EventPriority.LOWEST, true, event -> order.add("canceled"));

        CancelableEvent event = bus.post(new CancelableEvent());

        assertTrue(event.isCanceled());
        assertEquals(List.of("cancel", "canceled"), order);
    }

    @Test
    void childEventsReachParentListeners() {
        GeminiEventBus bus = new GeminiEventBus();
        List<String> order = new ArrayList<>();
        bus.listen(ParentEvent.class, event -> order.add("parent"));
        bus.listen(ChildEvent.class, event -> order.add("child"));

        bus.post(new ChildEvent());

        assertEquals(List.of("child", "parent"), order);
    }

    @Test
    void annotationRegistrationSupportsStaticAndInstanceListeners() {
        GeminiEventBus bus = new GeminiEventBus();
        AnnotatedListener.calls = 0;
        AnnotatedStaticListener.calls = 0;
        bus.register(new AnnotatedListener());
        bus.register(AnnotatedStaticListener.class);

        TestEvent event = bus.post(new TestEvent());

        assertEquals(1, AnnotatedListener.calls);
        assertEquals(1, AnnotatedStaticListener.calls);
        assertEquals(2, event.value);
    }

    @Test
    void unregisterRemovesEveryListenerFromOwner() {
        GeminiEventBus bus = new GeminiEventBus();
        AnnotatedListener listener = new AnnotatedListener();
        AnnotatedListener.calls = 0;
        bus.register(listener);
        bus.post(new TestEvent());
        bus.unregister(listener);
        bus.post(new TestEvent());

        assertEquals(1, AnnotatedListener.calls);
        assertEquals(0, bus.listenerCount());
    }

    @Test
    void subscriptionCloseRemovesConsumer() {
        GeminiEventBus bus = new GeminiEventBus();
        List<String> order = new ArrayList<>();
        try (var ignored = bus.listen(TestEvent.class, event -> order.add("called"))) {
            bus.post(new TestEvent());
        }
        bus.post(new TestEvent());

        assertEquals(List.of("called"), order);
    }

    @Test
    void subscriptionsWithSameConsumerAreIndependent() {
        GeminiEventBus bus = new GeminiEventBus();
        AtomicInteger calls = new AtomicInteger();
        java.util.function.Consumer<TestEvent> consumer = event -> calls.incrementAndGet();
        var first = bus.listen(TestEvent.class, consumer);
        try (var ignored = bus.listen(TestEvent.class, consumer)) {
            first.close();
            bus.post(new TestEvent());
        }

        assertEquals(1, calls.get());
    }

    @Test
    void shutdownStopsDispatch() {
        GeminiEventBus bus = new GeminiEventBus();
        List<String> order = new ArrayList<>();
        bus.listen(TestEvent.class, event -> order.add("called"));
        bus.shutdown();

        bus.post(new TestEvent());

        assertTrue(order.isEmpty());
    }

    @Test
    void cancelableMetadataIsEnforced() {
        CancelableEvent cancelable = new CancelableEvent();
        TestEvent plain = new TestEvent();

        assertTrue(cancelable.isCancelable());
        assertFalse(plain.isCancelable());
        assertThrows(UnsupportedOperationException.class, () -> plain.setCanceled(true));
    }

    static class ParentEvent extends GeminiEvent {
    }

    static final class ChildEvent extends ParentEvent {
    }

    static class TestEvent extends GeminiEvent {
        int value;
    }

    @Cancelable
    static final class CancelableEvent extends GeminiEvent {
    }

    static final class AnnotatedListener {
        static int calls;

        @SubscribeEvent(priority = EventPriority.HIGH)
        public void onTest(TestEvent event) {
            calls++;
            event.value++;
        }
    }

    static final class AnnotatedStaticListener {
        static int calls;

        @SubscribeEvent
        public static void onTest(TestEvent event) {
            calls++;
            event.value++;
        }
    }
}
