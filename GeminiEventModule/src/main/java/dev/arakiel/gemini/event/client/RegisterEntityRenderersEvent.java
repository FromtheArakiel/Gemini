package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterEntityRenderersEvent extends GeminiEvent {
    private final EntityModelSet modelSet;
    private final List<LayerRegistration> layers = new ArrayList<>();

    public RegisterEntityRenderersEvent(EntityModelSet modelSet) {
        this.modelSet = Objects.requireNonNull(modelSet, "modelSet");
    }

    public EntityModelSet getModelSet() {
        return this.modelSet;
    }

    public void registerLayer(ModelLayerLocation location, LayerDefinition definition) {
        this.layers.add(new LayerRegistration(location, definition));
    }

    public List<LayerRegistration> getLayers() {
        return List.copyOf(this.layers);
    }

    public record LayerRegistration(ModelLayerLocation location, LayerDefinition definition) {
        public LayerRegistration {
            Objects.requireNonNull(location, "location");
            Objects.requireNonNull(definition, "definition");
        }
    }
}
