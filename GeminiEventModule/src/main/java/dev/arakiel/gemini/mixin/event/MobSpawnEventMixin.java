package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.spawn.MobSpawnEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobSpawnEventMixin {
    @Inject(method = "checkSpawnRules", at = @At("HEAD"), cancellable = true)
    private void gemini$spawnPositionCheck(
            LevelAccessor level,
            MobSpawnType spawnType,
            CallbackInfoReturnable<Boolean> callback
    ) {
        Mob mob = (Mob) (Object) this;
        MobSpawnEvent event = new MobSpawnEvent(
                mob,
                level,
                mob.getX(),
                mob.getY(),
                mob.getZ(),
                spawnType,
                MobSpawnEvent.Phase.POSITION_CHECK,
                null
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"), cancellable = true)
    private void gemini$finalizeSpawnEvent(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            MobSpawnType spawnType,
            SpawnGroupData spawnGroupData,
            CallbackInfoReturnable<SpawnGroupData> callback
    ) {
        Mob mob = (Mob) (Object) this;
        MobSpawnEvent event = new MobSpawnEvent(
                mob,
                level,
                mob.getX(),
                mob.getY(),
                mob.getZ(),
                spawnType,
                MobSpawnEvent.Phase.FINALIZE,
                spawnGroupData
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(null);
        }
    }
}
