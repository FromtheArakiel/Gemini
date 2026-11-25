package dev.arakiel.gemini.mixin.locate;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class)
public abstract class AsyncStructureReferenceMixin {
    @Inject(method = "tryAddReference", at = @At("HEAD"), cancellable = true)
    private static void gemini$atomicReference(
            StructureManager manager,
            StructureStart start,
            CallbackInfoReturnable<Boolean> callback
    ) {
        synchronized (start) {
            if (!start.canBeReferenced()) {
                callback.setReturnValue(false);
                return;
            }
            manager.addReference(start);
            callback.setReturnValue(true);
        }
    }
}
