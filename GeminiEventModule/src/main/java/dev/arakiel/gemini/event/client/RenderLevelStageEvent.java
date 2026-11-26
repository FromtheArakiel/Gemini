package dev.arakiel.gemini.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;

import java.util.Objects;

public class RenderLevelStageEvent extends GeminiEvent {
    public enum Stage {
        AFTER_SKY,
        AFTER_SOLID_BLOCKS,
        AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS,
        AFTER_CUTOUT_BLOCKS,
        AFTER_ENTITIES,
        AFTER_BLOCK_ENTITIES,
        AFTER_TRANSLUCENT_BLOCKS,
        AFTER_TRIPWIRE_BLOCKS,
        AFTER_PARTICLES,
        AFTER_WEATHER,
        AFTER_LEVEL
    }

    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final Camera camera;
    private final Frustum frustum;
    private final float partialTick;
    private final Stage stage;

    public RenderLevelStageEvent(
            LevelRenderer levelRenderer,
            PoseStack poseStack,
            Camera camera,
            Frustum frustum,
            float partialTick,
            Stage stage
    ) {
        this.levelRenderer = Objects.requireNonNull(levelRenderer, "levelRenderer");
        this.poseStack = Objects.requireNonNull(poseStack, "poseStack");
        this.camera = Objects.requireNonNull(camera, "camera");
        this.frustum = Objects.requireNonNull(frustum, "frustum");
        this.partialTick = partialTick;
        this.stage = Objects.requireNonNull(stage, "stage");
    }

    public LevelRenderer getLevelRenderer() {
        return this.levelRenderer;
    }

    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public Frustum getFrustum() {
        return this.frustum;
    }

    public float getPartialTick() {
        return this.partialTick;
    }

    public Stage getStage() {
        return this.stage;
    }
}
