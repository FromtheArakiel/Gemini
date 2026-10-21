package dev.arakiel.gemini.component.internal.itemstack;

import dev.arakiel.gemini.component.internal.base.ComponentRegistryImpl;
import net.minecraft.resources.ResourceLocation;

final class ComponentRegistryTestSupport {
    private ComponentRegistryTestSupport() {
    }

    static void clear(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            ComponentRegistryImpl.INSTANCE.clear(id);
        }
    }
}
