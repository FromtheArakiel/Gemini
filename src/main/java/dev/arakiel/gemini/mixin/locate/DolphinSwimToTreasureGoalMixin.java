package dev.arakiel.gemini.mixin.locate;

import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.locate.LocateTask;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.animal.Dolphin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CancellationException;

@Mixin(targets = "net.minecraft.world.entity.animal.Dolphin$DolphinSwimToTreasureGoal")
public abstract class DolphinSwimToTreasureGoalMixin {
    @Shadow
    @Final
    private Dolphin dolphin;

    @Shadow
    private boolean stuck;

    @Unique
    private LocateTask<BlockPos> gemini$locateTask;

    @Redirect(
            method = "start",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;findNearestMapStructure(Lnet/minecraft/tags/TagKey;Lnet/minecraft/core/BlockPos;IZ)Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos gemini$deferTreasureSearch(
            ServerLevel level,
            net.minecraft.tags.TagKey<net.minecraft.world.level.levelgen.structure.Structure> tag,
            BlockPos origin,
            int radius,
            boolean skipKnown
    ) {
        if (!GeminiLocate.config().dolphinTreasure) {
            return level.findNearestMapStructure(tag, origin, radius, skipKnown);
        }
        gemini$locateTask = GeminiLocate.findStructure(
                level,
                StructureTags.DOLPHIN_LOCATED,
                origin,
                50,
                false
        );
        gemini$locateTask.handleOnServerThread((found, throwable) -> handleResult(level, found, throwable));
        return null;
    }

    @Inject(method = "start", at = @At("RETURN"))
    private void gemini$resetStuck(CallbackInfo callback) {
        if (gemini$locateTask != null) {
            stuck = false;
        }
    }

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    private void gemini$continueWhileLocating(CallbackInfoReturnable<Boolean> callback) {
        if (gemini$locateTask != null && dolphin.getAirSupply() >= 100) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void gemini$pauseWhileLocating(CallbackInfo callback) {
        if (gemini$locateTask != null) {
            callback.cancel();
        }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void gemini$cancelLocate(CallbackInfo callback) {
        if (gemini$locateTask != null) {
            gemini$locateTask.cancel();
            gemini$locateTask = null;
        }
    }

    @Unique
    private void handleResult(ServerLevel level, BlockPos found, Throwable throwable) {
        if (throwable instanceof CancellationException) {
            return;
        }
        gemini$locateTask = null;
        if (!GeminiLocate.isLevelActive(level)
                || dolphin.isRemoved()
                || !dolphin.isAlive()
                || dolphin.level() != level) {
            return;
        }
        if (throwable != null || found == null) {
            stuck = true;
            return;
        }
        dolphin.setTreasurePos(found);
        stuck = false;
        level.broadcastEntityEvent(dolphin, (byte) 38);
    }
}
