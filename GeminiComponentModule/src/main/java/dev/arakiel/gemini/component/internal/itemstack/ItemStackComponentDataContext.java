package dev.arakiel.gemini.component.internal.itemstack;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistryAccess;

final class ItemStackComponentDataContext {
    private static final ThreadLocal<Deque<Provider>> PROVIDERS =
            ThreadLocal.withInitial(ArrayDeque::new);

    private ItemStackComponentDataContext() {
    }

    static void push(Provider provider) {
        PROVIDERS.get().push(provider == null ? RegistryAccess.EMPTY : provider);
    }

    static void pop() {
        Deque<Provider> providers = PROVIDERS.get();
        if (!providers.isEmpty()) {
            providers.pop();
        }
        if (providers.isEmpty()) {
            PROVIDERS.remove();
        }
    }

    static Provider current() {
        Deque<Provider> providers = PROVIDERS.get();
        return providers.isEmpty() ? RegistryAccess.EMPTY : providers.peek();
    }
}
