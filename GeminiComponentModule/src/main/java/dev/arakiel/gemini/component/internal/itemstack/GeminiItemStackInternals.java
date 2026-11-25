package dev.arakiel.gemini.component.internal.itemstack;

import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.internal.base.AbstractComponentContainer;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class GeminiItemStackInternals {
    private static final Map<ItemStack, ItemStackComponentRuntime> RUNTIMES =
            Collections.synchronizedMap(new WeakHashMap<>());

    private GeminiItemStackInternals() {
    }

    public static ComponentContainer getComponentContainer(ItemStack stack) {
        return getOrCreateRuntime(
                stack,
                StaticItemStackComponentPlugin.INSTANCE.factory(),
                RegistryAccess.EMPTY,
                GeminiItemStackComponents.type()
        ).container();
    }

    public static void commit(ItemStack stack) {
        commit(stack, providerFor(stack));
    }

    public static void commit(ItemStack stack, Provider provider) {
        ItemStackComponentRuntime runtime;
        synchronized (RUNTIMES) {
            runtime = RUNTIMES.get(stack);
        }
        if (runtime != null) {
            commit(
                    stack,
                    runtime,
                    provider,
                    GeminiItemStackComponents.type()
            );
        }
    }

    public static void prepareForComparison(ItemStack first, ItemStack second) {
        commit(first);
        commit(second);
    }

    public static void pushDecodeProvider(Provider provider) {
        ItemStackComponentDataContext.push(provider);
    }

    public static void popDecodeProvider() {
        ItemStackComponentDataContext.pop();
    }

    public static void copyRuntime(ItemStack source, ItemStack target) {
        copyRuntime(
                source,
                target,
                StaticItemStackComponentPlugin.INSTANCE.factory(),
                GeminiItemStackComponents.type()
        );
    }

    public static void invalidate(ItemStack stack) {
        RUNTIMES.remove(stack);
    }

    static ItemStackComponentRuntime getOrCreateRuntime(
            ItemStack stack,
            ComponentContainer.Factory<ItemStack> factory,
            Provider fallbackProvider,
            DataComponentType<ItemStackComponentData> componentType
    ) {
        if (stack.isEmpty()) {
            return new ItemStackComponentRuntime(
                    ComponentContainer.EMPTY,
                    new CompoundTag(),
                    fallbackProvider
            );
        }
        synchronized (RUNTIMES) {
            return RUNTIMES.computeIfAbsent(
                    stack,
                    ignored -> createRuntime(
                            stack,
                            factory,
                            fallbackProvider,
                            componentType
                    )
            );
        }
    }

    static void commit(
            ItemStack stack,
            Provider provider,
            ComponentContainer.Factory<ItemStack> factory,
            DataComponentType<ItemStackComponentData> componentType
    ) {
        ItemStackComponentRuntime runtime = getOrCreateRuntime(
                stack,
                factory,
                provider,
                componentType
        );
        commit(stack, runtime, provider, componentType);
    }

    static void copyRuntime(
            ItemStack source,
            ItemStack target,
            ComponentContainer.Factory<ItemStack> factory,
            DataComponentType<ItemStackComponentData> componentType
    ) {
        if (source.isEmpty() || target.isEmpty()) {
            return;
        }
        ItemStackComponentRuntime sourceRuntime;
        synchronized (RUNTIMES) {
            sourceRuntime = RUNTIMES.get(source);
        }
        if (sourceRuntime == null) {
            return;
        }

        Provider provider = sourceRuntime.provider();
        ItemStackComponentRuntime targetRuntime = getOrCreateRuntime(
                target,
                factory,
                provider,
                componentType
        );
        targetRuntime.container().copyFrom(sourceRuntime.container(), provider);
        targetRuntime.replaceUnknownComponents(sourceRuntime.unknownComponents());
        targetRuntime.setProvider(provider);
        commit(target, targetRuntime, provider, componentType);
    }

    private static ItemStackComponentRuntime createRuntime(
            ItemStack stack,
            ComponentContainer.Factory<ItemStack> factory,
            Provider fallbackProvider,
            DataComponentType<ItemStackComponentData> componentType
    ) {
        ComponentContainer container = factory.createContainer(stack);
        ItemStackComponentData data = stack.get(componentType);
        if (data == null) {
            return new ItemStackComponentRuntime(
                    container,
                    new CompoundTag(),
                    fallbackProvider
            );
        }

        Provider provider = data.provider();
        CompoundTag snapshot = data.snapshot();
        container.fromTag(snapshot, provider);
        CompoundTag unknown = snapshot.getCompound(AbstractComponentContainer.NBT_KEY);
        return new ItemStackComponentRuntime(container, unknown, provider);
    }

    private static void commit(
            ItemStack stack,
            ItemStackComponentRuntime runtime,
            Provider provider,
            DataComponentType<ItemStackComponentData> componentType
    ) {
        if (stack.isEmpty()) {
            return;
        }

        Provider effectiveProvider = provider == null
                ? runtime.provider()
                : provider;
        runtime.setProvider(effectiveProvider);

        CompoundTag snapshot = runtime.container().toTag(
                new CompoundTag(),
                effectiveProvider
        );
        CompoundTag unknown = runtime.unknownComponents();
        if (!unknown.isEmpty()) {
            CompoundTag components = snapshot.getCompound(
                    AbstractComponentContainer.NBT_KEY
            );
            for (String key : unknown.getAllKeys()) {
                components.put(key, unknown.get(key).copy());
            }
            snapshot.put(AbstractComponentContainer.NBT_KEY, components);
        }

        CompoundTag components = snapshot.getCompound(
                AbstractComponentContainer.NBT_KEY
        );
        if (components.isEmpty()) {
            stack.remove(componentType);
        } else {
            stack.set(
                    componentType,
                    new ItemStackComponentData(snapshot, effectiveProvider)
            );
        }
    }

    private static Provider providerFor(ItemStack stack) {
        synchronized (RUNTIMES) {
            ItemStackComponentRuntime runtime = RUNTIMES.get(stack);
            return runtime == null ? RegistryAccess.EMPTY : runtime.provider();
        }
    }
}
