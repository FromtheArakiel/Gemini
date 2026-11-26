package dev.arakiel.gemini.mixin.client.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.client.RenderGuiLayerEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class RenderGuiLayerEventMixin {
    @Unique
    private static final ResourceLocation GEMINI$CAMERA_OVERLAYS = gemini$layer("camera_overlays");
    @Unique
    private static final ResourceLocation GEMINI$CROSSHAIR = gemini$layer("crosshair");
    @Unique
    private static final ResourceLocation GEMINI$HOTBAR = gemini$layer("hotbar");
    @Unique
    private static final ResourceLocation GEMINI$EXPERIENCE_LEVEL = gemini$layer("experience_level");
    @Unique
    private static final ResourceLocation GEMINI$EFFECTS = gemini$layer("effects");
    @Unique
    private static final ResourceLocation GEMINI$DEMO_OVERLAY = gemini$layer("demo_overlay");
    @Unique
    private static final ResourceLocation GEMINI$SCOREBOARD_SIDEBAR = gemini$layer("scoreboard_sidebar");
    @Unique
    private static final ResourceLocation GEMINI$OVERLAY_MESSAGE = gemini$layer("overlay_message");
    @Unique
    private static final ResourceLocation GEMINI$TITLE = gemini$layer("title");
    @Unique
    private static final ResourceLocation GEMINI$CHAT = gemini$layer("chat");
    @Unique
    private static final ResourceLocation GEMINI$TAB_LIST = gemini$layer("tab_list");
    @Unique
    private static final ResourceLocation GEMINI$SLEEP_OVERLAY = gemini$layer("sleep_overlay");

    @Inject(method = "renderCameraOverlays", at = @At("HEAD"), cancellable = true)
    private void gemini$cameraOverlaysPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$CAMERA_OVERLAYS, callback);
    }

    @Inject(method = "renderCameraOverlays", at = @At("RETURN"))
    private void gemini$cameraOverlaysPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$CAMERA_OVERLAYS);
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void gemini$crosshairPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$CROSSHAIR, callback);
    }

    @Inject(method = "renderCrosshair", at = @At("RETURN"))
    private void gemini$crosshairPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$CROSSHAIR);
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"), cancellable = true)
    private void gemini$hotbarPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$HOTBAR, callback);
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("RETURN"))
    private void gemini$hotbarPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$HOTBAR);
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void gemini$experienceLevelPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$EXPERIENCE_LEVEL, callback);
    }

    @Inject(method = "renderExperienceLevel", at = @At("RETURN"))
    private void gemini$experienceLevelPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$EXPERIENCE_LEVEL);
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void gemini$effectsPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$EFFECTS, callback);
    }

    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void gemini$effectsPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$EFFECTS);
    }

    @Inject(method = "renderDemoOverlay", at = @At("HEAD"), cancellable = true)
    private void gemini$demoOverlayPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$DEMO_OVERLAY, callback);
    }

    @Inject(method = "renderDemoOverlay", at = @At("RETURN"))
    private void gemini$demoOverlayPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$DEMO_OVERLAY);
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void gemini$scoreboardSidebarPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$SCOREBOARD_SIDEBAR, callback);
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("RETURN"))
    private void gemini$scoreboardSidebarPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$SCOREBOARD_SIDEBAR);
    }

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void gemini$overlayMessagePre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$OVERLAY_MESSAGE, callback);
    }

    @Inject(method = "renderOverlayMessage", at = @At("RETURN"))
    private void gemini$overlayMessagePost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$OVERLAY_MESSAGE);
    }

    @Inject(method = "renderTitle", at = @At("HEAD"), cancellable = true)
    private void gemini$titlePre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$TITLE, callback);
    }

    @Inject(method = "renderTitle", at = @At("RETURN"))
    private void gemini$titlePost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$TITLE);
    }

    @Inject(method = "renderChat", at = @At("HEAD"), cancellable = true)
    private void gemini$chatPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$CHAT, callback);
    }

    @Inject(method = "renderChat", at = @At("RETURN"))
    private void gemini$chatPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$CHAT);
    }

    @Inject(method = "renderTabList", at = @At("HEAD"), cancellable = true)
    private void gemini$tabListPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$TAB_LIST, callback);
    }

    @Inject(method = "renderTabList", at = @At("RETURN"))
    private void gemini$tabListPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$TAB_LIST);
    }

    @Inject(method = "renderSleepOverlay", at = @At("HEAD"), cancellable = true)
    private void gemini$sleepOverlayPre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$pre(graphics, deltaTracker, GEMINI$SLEEP_OVERLAY, callback);
    }

    @Inject(method = "renderSleepOverlay", at = @At("RETURN"))
    private void gemini$sleepOverlayPost(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            CallbackInfo callback
    ) {
        gemini$post(graphics, deltaTracker, GEMINI$SLEEP_OVERLAY);
    }

    @Unique
    private static void gemini$pre(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            ResourceLocation layer,
            CallbackInfo callback
    ) {
        RenderGuiLayerEvent.Pre event = new RenderGuiLayerEvent.Pre(
                graphics,
                layer,
                deltaTracker
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Unique
    private static void gemini$post(
            GuiGraphics graphics,
            DeltaTracker deltaTracker,
            ResourceLocation layer
    ) {
        GeminiEvents.post(new RenderGuiLayerEvent.Post(graphics, layer, deltaTracker));
    }

    @Unique
    private static ResourceLocation gemini$layer(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }
}
