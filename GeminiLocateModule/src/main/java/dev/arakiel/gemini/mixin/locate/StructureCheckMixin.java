package dev.arakiel.gemini.mixin.locate;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(StructureCheck.class)
public abstract class StructureCheckMixin {
    @Shadow
    @Final
    private Map<Structure, Long2BooleanMap> featureChecks;

    @Inject(method = "checkStart", at = @At("HEAD"), cancellable = true)
    private void gemini$synchronizeCheckStart(
            ChunkPos pos,
            Structure structure,
            StructurePlacement placement,
            boolean skipKnown,
            CallbackInfoReturnable<StructureCheckResult> callback
    ) {
        featureChecks.clear();
        if (!Thread.holdsLock(this)) {
            synchronized (this) {
                callback.setReturnValue(((StructureCheck) (Object) this)
                        .checkStart(pos, structure, placement, skipKnown));
            }
        }
    }

    @Inject(method = "onStructureLoad", at = @At("HEAD"), cancellable = true)
    private void gemini$synchronizeStructureLoad(
            ChunkPos pos,
            Map<Structure, StructureStart> starts,
            CallbackInfo callback
    ) {
        if (!Thread.holdsLock(this)) {
            synchronized (this) {
                ((StructureCheck) (Object) this).onStructureLoad(pos, starts);
            }
            callback.cancel();
        }
    }

    @Inject(method = "incrementReference", at = @At("HEAD"), cancellable = true)
    private void gemini$synchronizeReference(ChunkPos pos, Structure structure, CallbackInfo callback) {
        if (!Thread.holdsLock(this)) {
            synchronized (this) {
                ((StructureCheck) (Object) this).incrementReference(pos, structure);
            }
            callback.cancel();
        }
    }
}
