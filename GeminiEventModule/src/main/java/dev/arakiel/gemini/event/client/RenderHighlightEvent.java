package dev.arakiel.gemini.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Cancelable
public class RenderHighlightEvent extends GeminiEvent {
    public enum Phase {
        BLOCK,
        ENTITY
    }

    private final PoseStack poseStack;
    private final Camera camera;
    private final Phase phase;
    private final @Nullable BlockHitResult blockHit;
    private final @Nullable Entity entity;

    public RenderHighlightEvent(
            PoseStack poseStack,
            Camera camera,
            Phase phase,
            @Nullable BlockHitResult blockHit,
            @Nullable Entity entity
    ) {
        this.poseStack = Objects.requireNonNull(poseStack, "poseStack");
        this.camera = Objects.requireNonNull(camera, "camera");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.blockHit = blockHit;
        this.entity = entity;
    }

    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public @Nullable BlockHitResult getBlockHit() {
        return this.blockHit;
    }

    public @Nullable Entity getEntity() {
        return this.entity;
    }
}
