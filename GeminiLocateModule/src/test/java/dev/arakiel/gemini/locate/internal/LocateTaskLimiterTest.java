package dev.arakiel.gemini.locate.internal;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocateTaskLimiterTest {
    @Test
    void admissionIncludesBothRunningAndQueuedTasks() {
        LocateTaskLimiter limiter = new LocateTaskLimiter(2, 3);

        for (int index = 0; index < 5; index++) {
            assertTrue(limiter.tryAdmit());
        }
        assertFalse(limiter.tryAdmit());
    }

    @Test
    void cancelledQueuedTaskReleasesAdmission() {
        LocateTaskLimiter limiter = new LocateTaskLimiter(1, 1);
        assertTrue(limiter.tryAdmit());
        assertTrue(limiter.tryAdmit());

        var task = limiter.task(new CompletableFuture<Void>(), () -> {
        });
        assertTrue(task.cancel(false));
        assertTrue(limiter.tryAdmit());
    }
}
