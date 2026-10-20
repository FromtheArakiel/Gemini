package dev.arakiel.gemini.api;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.registration.api.AutoRegistryContainer;
import dev.arakiel.gemini.registration.container.BlockEntityRegistryContainer;
import dev.arakiel.gemini.registration.container.BlockRegistryContainer;
import dev.arakiel.gemini.registration.container.EntityRegistryContainer;
import dev.arakiel.gemini.registration.container.ItemRegistryContainer;
import dev.arakiel.gemini.registration.container.MobEffectRegistryContainer;
import dev.arakiel.gemini.registration.container.SoundEventRegistryContainer;
import dev.arakiel.gemini.registration.internal.FieldRegistrationHandler;

public final class GeminiRegistration {
    private GeminiRegistration() {
    }

    public static void init() {
    }

    public static void init(Class<?>... containers) {
        all(containers);
    }

    public static void all(Class<?>... containers) {
        for (Class<?> container : containers) {
            if (ItemRegistryContainer.class.isAssignableFrom(container)) {
                items(container.asSubclass(ItemRegistryContainer.class));
            } else if (BlockRegistryContainer.class.isAssignableFrom(container)) {
                blocks(container.asSubclass(BlockRegistryContainer.class));
            } else if (BlockEntityRegistryContainer.class.isAssignableFrom(container)) {
                blockEntities(container.asSubclass(BlockEntityRegistryContainer.class));
            } else if (EntityRegistryContainer.class.isAssignableFrom(container)) {
                entities(container.asSubclass(EntityRegistryContainer.class));
            } else if (MobEffectRegistryContainer.class.isAssignableFrom(container)) {
                mobEffects(container.asSubclass(MobEffectRegistryContainer.class));
            } else if (SoundEventRegistryContainer.class.isAssignableFrom(container)) {
                soundEvents(container.asSubclass(SoundEventRegistryContainer.class));
            } else {
                throw new IllegalArgumentException("Unsupported registry container: " + container.getName());
            }
        }
    }

    public static void items(Class<? extends ItemRegistryContainer> container) {
        // 便捷方法默认使用 Gemini 命名空间；依赖模组应传 namespace 或标注 @RegistryNamespace。
        register(container, Gemini.MOD_ID, true);
    }

    public static void blocks(Class<? extends BlockRegistryContainer> container) {
        register(container, Gemini.MOD_ID, true);
    }

    public static void blockEntities(Class<? extends BlockEntityRegistryContainer> container) {
        register(container, Gemini.MOD_ID, true);
    }

    public static void entities(Class<? extends EntityRegistryContainer> container) {
        register(container, Gemini.MOD_ID, true);
    }

    public static void mobEffects(Class<? extends MobEffectRegistryContainer> container) {
        register(container, Gemini.MOD_ID, true);
    }

    public static void soundEvents(Class<? extends SoundEventRegistryContainer> container) {
        register(container, Gemini.MOD_ID, true);
    }

    public static void register(Class<? extends AutoRegistryContainer<?>> container) {
        register(container, Gemini.MOD_ID, true);
    }

    public static void register(Class<? extends AutoRegistryContainer<?>> container, String namespace) {
        register(container, namespace, true);
    }

    public static void register(
            Class<? extends AutoRegistryContainer<?>> container,
            String namespace,
            boolean recurseIntoInnerClasses
    ) {
        registerUnchecked(container, namespace, recurseIntoInnerClasses);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerUnchecked(
            Class<? extends AutoRegistryContainer<?>> container,
            String namespace,
            boolean recurseIntoInnerClasses
    ) {
        FieldRegistrationHandler.register((Class) container, namespace, recurseIntoInnerClasses);
    }
}
