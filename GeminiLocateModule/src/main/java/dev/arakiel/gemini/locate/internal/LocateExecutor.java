package dev.arakiel.gemini.locate.internal;

import com.mojang.datafixers.util.Pair;
import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.locate.LocateConfig;
import dev.arakiel.gemini.locate.LocateTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class LocateExecutor {
    private static final Object LOCK = new Object();
    private static final ConcurrentHashMap<LocateKey, CompletableFuture<?>> PENDING = new ConcurrentHashMap<>();
    private static final AtomicInteger THREAD_COUNT = new AtomicInteger();
    private static volatile ExecutorService executor;
    private static volatile LocateTaskLimiter limiter;
    private static volatile int maxConcurrent = 2;
    private static volatile int maxQueued = 128;
    private static volatile boolean stopped;

    private LocateExecutor() {
    }

    public static void configure(LocateConfig config) {
        maxConcurrent = config.maxConcurrent;
        maxQueued = config.maxQueued;
        LocateTaskLimiter current = limiter;
        if (current != null) {
            current.configure(maxConcurrent, maxQueued);
        }
    }

    public static void start() {
        synchronized (LOCK) {
            shutdownExecutor();
            stopped = false;
            limiter = new LocateTaskLimiter(maxConcurrent, maxQueued);
            int generation = THREAD_COUNT.incrementAndGet();
            if (GeminiLocate.config().virtualThreads) {
                executor = Executors.newThreadPerTaskExecutor(
                        Thread.ofVirtual()
                                .name("gemini-locate-", generation)
                                .uncaughtExceptionHandler((thread, throwable) ->
                                        Gemini.LOGGER.error("Uncaught locate task failure in {}", thread.getName(), throwable))
                                .factory()
                );
            } else {
                executor = Executors.newFixedThreadPool(
                        maxConcurrent,
                        Thread.ofPlatform()
                                .name("gemini-locate-platform-", generation)
                                .uncaughtExceptionHandler((thread, throwable) ->
                                        Gemini.LOGGER.error("Uncaught locate task failure in {}", thread.getName(), throwable))
                                .factory()
                );
            }
        }
    }

    public static void stop() {
        synchronized (LOCK) {
            stopped = true;
            for (CompletableFuture<?> future : PENDING.values()) {
                future.cancel(false);
            }
            PENDING.clear();
            shutdownExecutor();
        }
    }

    public static LocateTask<Pair<BlockPos, Holder<Structure>>> findStructures(
            ServerLevel level,
            HolderSet<Structure> structures,
            BlockPos origin,
            int radius,
            boolean skipKnown
    ) {
        LocateKey key = new LocateKey(level, structures, origin, radius, skipKnown);
        Supplier<LocateTask<Pair<BlockPos, Holder<Structure>>>> task = () -> submit(
                level,
                result -> {
                    Pair<BlockPos, Holder<Structure>> found = level.getChunkSource()
                            .getGenerator()
                            .findNearestMapStructure(level, structures, origin, radius, skipKnown);
                    result.complete(found);
                }
        );
        return coalesce(key, task, skipKnown);
    }

    public static LocateTask<Pair<BlockPos, Holder<Biome>>> findBiome(
            ServerLevel level,
            java.util.function.Predicate<Holder<Biome>> predicate,
            BlockPos origin,
            int radius,
            int horizontalStep,
            int verticalStep
    ) {
        return submit(level, result -> {
            Pair<BlockPos, Holder<Biome>> found;
            if (GeminiLocate.config().biomeFilter
                    && level.getChunkSource().getGenerator().getBiomeSource() instanceof MultiNoiseBiomeSource source) {
                found = BiomeFinder.findClosest(
                        origin,
                        radius,
                        horizontalStep,
                        verticalStep,
                        predicate,
                        level.getChunkSource().randomState().sampler(),
                        level,
                        source
                );
            } else {
                found = level.findClosestBiome3d(predicate, origin, radius, horizontalStep, verticalStep);
            }
            result.complete(found);
        });
    }

    private static <T> LocateTask<T> coalesce(
            LocateKey key,
            Supplier<LocateTask<T>> starter,
            boolean skipKnown
    ) {
        if (skipKnown) {
            return starter.get();
        }
        CompletableFuture<T> placeholder = new CompletableFuture<>();
        @SuppressWarnings("unchecked")
        CompletableFuture<T> shared = (CompletableFuture<T>) PENDING.putIfAbsent(key, placeholder);
        if (shared == null) {
            shared = placeholder;
            try {
                LocateTask<T> task = starter.get();
                task.future().whenComplete((value, throwable) -> {
                    if (throwable == null) {
                        placeholder.complete(value);
                    } else {
                        placeholder.completeExceptionally(throwable);
                    }
                    PENDING.remove(key, placeholder);
                });
            } catch (Throwable throwable) {
                placeholder.completeExceptionally(throwable);
                PENDING.remove(key, placeholder);
            }
        }
        CompletableFuture<T> copy = shared.copy();
        return new LocateTask<>(key.level().getServer(), copy, copy);
    }

    private static <T> LocateTask<T> submit(ServerLevel level, java.util.function.Consumer<CompletableFuture<T>> action) {
        CompletableFuture<T> result = new CompletableFuture<>();
        Future<?> task = submitTask(result, () -> {
            try {
                action.accept(result);
            } catch (Throwable throwable) {
                result.completeExceptionally(throwable);
            }
        });
        return new LocateTask<>(level.getServer(), result, task);
    }

    private static Future<?> submitTask(CompletableFuture<?> result, Runnable action) {
        synchronized (LOCK) {
            if (executor == null || executor.isShutdown()) {
                if (stopped) {
                    RejectedExecutionException exception =
                            new RejectedExecutionException("Gemini locate executor is stopped");
                    result.completeExceptionally(exception);
                    return CompletableFuture.completedFuture(null);
                }
                start();
            }
            LocateTaskLimiter currentLimiter = limiter;
            if (currentLimiter == null || !currentLimiter.tryAdmit()) {
                result.completeExceptionally(new RejectedExecutionException(
                        "Locate capacity exhausted (" + maxConcurrent + " concurrent, " + maxQueued + " queued)"
                ));
                return CompletableFuture.completedFuture(null);
            }
            java.util.concurrent.FutureTask<Void> task = currentLimiter.task(result, action);
            try {
                executor.execute(task);
                return task;
            } catch (RejectedExecutionException exception) {
                task.cancel(false);
                result.completeExceptionally(exception);
                return CompletableFuture.completedFuture(null);
            }
        }
    }

    private static void shutdownExecutor() {
        ExecutorService current = executor;
        executor = null;
        limiter = null;
        if (current != null) {
            current.shutdownNow();
        }
    }

    private record LocateKey(
            ServerLevel level,
            Object target,
            BlockPos origin,
            int radius,
            boolean skipKnown
    ) {
    }
}
