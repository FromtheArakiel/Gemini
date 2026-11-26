package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.spawn.RegisterSpawnPlacementsEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnPlacements.class)
public abstract class RegisterSpawnPlacementsEventMixin {
    @Unique
    private static boolean gemini$placementsPosted;

    @Inject(method = "checkSpawnRules", at = @At("HEAD"))
    private static <T extends Entity> void gemini$registerPlacements(
            EntityType<T> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (gemini$placementsPosted) {
            return;
        }
        gemini$placementsPosted = true;
        RegisterSpawnPlacementsEvent event = new RegisterSpawnPlacementsEvent();
        GeminiEvents.post(event);
        for (RegisterSpawnPlacementsEvent.Placement<?> placement : event.getPlacements()) {
            gemini$registerPlacement(placement);
        }
    }

    @Unique
    private static <T extends net.minecraft.world.entity.Mob> void gemini$registerPlacement(
            RegisterSpawnPlacementsEvent.Placement<T> placement
    ) {
        SpawnPlacementsInvoker.gemini$register(
                placement.type(),
                placement.placementType(),
                placement.heightmap(),
                placement.predicate()
        );
    }
}
