package dev.arakiel.gemini.event.internal;

import net.minecraft.world.Container;

public interface GeminiGrindstoneAccess {
    Container gemini$getRepairSlots();

    int gemini$getExperience();

    void gemini$setExperience(int experience);
}
