package dev.arakiel.gemini.event.brewing;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.alchemy.PotionBrewing;

import java.util.Objects;

public class RegisterBrewingRecipesEvent extends GeminiEvent {
    private final PotionBrewing.Builder builder;
    private final RegistryAccess registryAccess;

    public RegisterBrewingRecipesEvent(PotionBrewing.Builder builder) {
        this(builder, RegistryAccess.EMPTY);
    }

    public RegisterBrewingRecipesEvent(PotionBrewing.Builder builder, RegistryAccess registryAccess) {
        this.builder = Objects.requireNonNull(builder, "builder");
        this.registryAccess = Objects.requireNonNull(registryAccess, "registryAccess");
    }

    public PotionBrewing.Builder getBuilder() {
        return this.builder;
    }

    public RegistryAccess getRegistryAccess() {
        return this.registryAccess;
    }
}
