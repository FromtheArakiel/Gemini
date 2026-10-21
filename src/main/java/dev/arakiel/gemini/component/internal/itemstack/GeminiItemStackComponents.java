package dev.arakiel.gemini.component.internal.itemstack;

import dev.arakiel.gemini.Gemini;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class GeminiItemStackComponents {
    private static DataComponentType<ItemStackComponentData> type;

    private GeminiItemStackComponents() {
    }

    public static synchronized void init() {
        if (type == null) {
            type = Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    Gemini.id("item_components"),
                    DataComponentType.<ItemStackComponentData>builder()
                            .persistent(ItemStackComponentData.CODEC)
                            .networkSynchronized(ItemStackComponentData.STREAM_CODEC)
                            .build()
            );
        }
        StaticItemStackComponentPlugin.INSTANCE.ensureInitialized();
    }

    public static DataComponentType<ItemStackComponentData> type() {
        init();
        return type;
    }
}
