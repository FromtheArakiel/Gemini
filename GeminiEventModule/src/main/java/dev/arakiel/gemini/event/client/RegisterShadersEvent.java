package dev.arakiel.gemini.event.client;

import dev.arakiel.gemini.event.GeminiEvent;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterShadersEvent extends GeminiEvent {
    private final List<Registration> registrations = new ArrayList<>();

    public void register(ResourceLocation id, Object loader) {
        this.registrations.add(new Registration(id, null, loader));
    }

    public void register(
            ResourceLocation id,
            VertexFormat vertexFormat,
            ShaderLoader loader
    ) {
        this.registrations.add(new Registration(id, vertexFormat, loader));
    }

    public List<Registration> getRegistrations() {
        return List.copyOf(this.registrations);
    }

    public record Registration(ResourceLocation id, VertexFormat vertexFormat, Object loader) {
        public Registration {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(loader, "loader");
        }

        public Registration(ResourceLocation id, Object loader) {
            this(id, null, loader);
        }
    }

    @FunctionalInterface
    public interface ShaderLoader {
        void load(ShaderInstance shader);
    }
}
