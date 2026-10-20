package dev.arakiel.gemini.mixin.fancy;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface ContainerScreenAccessor {
    @Accessor("leftPos")
    int gemini$getLeftPos();

    @Accessor("topPos")
    int gemini$getTopPos();

    @Accessor("imageHeight")
    int gemini$getImageHeight();
}
