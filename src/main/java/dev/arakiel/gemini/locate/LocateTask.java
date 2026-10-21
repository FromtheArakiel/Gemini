package dev.arakiel.gemini.locate;

import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class LocateTask<T> {
    private final MinecraftServer server;
    private final CompletableFuture<T> result;
    private final Future<?> task;

    public LocateTask(MinecraftServer server, CompletableFuture<T> result, Future<?> task) {
        this.server = server;
        this.result = result;
        this.task = task;
    }

    public LocateTask<T> handle(BiConsumer<? super T, Throwable> handler) {
        result.whenComplete(handler);
        return this;
    }

    public LocateTask<T> handleOnServerThread(BiConsumer<? super T, Throwable> handler) {
        result.handle((value, throwable) -> {
            server.execute(() -> handler.accept(value, throwable));
            return null;
        });
        return this;
    }

    public LocateTask<T> withTimeout(long timeout, TimeUnit unit) {
        result.orTimeout(timeout, unit);
        return this;
    }

    public CompletableFuture<T> future() {
        return result;
    }

    public <R> LocateTask<R> map(Function<? super T, ? extends R> mapper) {
        return new LocateTask<>(server, result.thenApply(mapper), task);
    }

    public void cancel() {
        task.cancel(true);
        result.cancel(false);
    }

    static <T> LocateTask<T> completed(MinecraftServer server, T value) {
        CompletableFuture<T> future = CompletableFuture.completedFuture(value);
        return new LocateTask<>(server, future, future);
    }
}
