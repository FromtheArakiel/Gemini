package dev.arakiel.gemini.capability;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public sealed interface CapabilityContext
        permits BlockCapabilityContext, EntityCapabilityContext, ItemCapabilityContext, LevelCapabilityContext {
    @Nullable
    Direction direction();
}
