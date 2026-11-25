package dev.arakiel.gemini.tab;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record TabHandle(ResourceLocation id, String owner, long sequence) {
    public TabHandle {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(owner, "owner");
    }
}
