package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModelEvent extends GeminiEvent {
    public enum Phase {
        REGISTER_ADDITIONAL,
        MODIFY_BAKING_RESULT
    }

    private final Phase phase;
    private final List<ResourceLocation> additionalModels;
    private final Map<ResourceLocation, BakedModel> bakingResult;

    public ModelEvent(
            Phase phase,
            List<ResourceLocation> additionalModels,
            Map<ResourceLocation, BakedModel> bakingResult
    ) {
        this.phase = Objects.requireNonNull(phase, "phase");
        this.additionalModels = Objects.requireNonNull(additionalModels, "additionalModels");
        this.bakingResult = Objects.requireNonNull(bakingResult, "bakingResult");
    }

    public Phase getPhase() {
        return this.phase;
    }

    public List<ResourceLocation> getAdditionalModels() {
        return this.additionalModels;
    }

    public Map<ResourceLocation, BakedModel> getBakingResult() {
        return this.bakingResult;
    }
}
