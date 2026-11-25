package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.spawn.FinalizeSpawnEvent;
import dev.arakiel.gemini.event.spawn.MobSpawnEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobSpawnMixin {
    @Inject(method = "checkSpawnRules", at = @At("HEAD"), cancellable = true)
    private void gemini$spawnPositionCheck(
            net.minecraft.world.level.LevelAccessor level,
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
    private void gemini$finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            SpawnGroupData spawnGroupData,
            CallbackInfoReturnable<SpawnGroupData> callback
    ) {
        Mob mob = (Mob) (Object) this;
        FinalizeSpawnEvent event = new FinalizeSpawnEvent(mob, level, difficulty, spawnType);
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.setReturnValue(null);
            return;
        }
        MobSpawnEvent spawnEvent = new MobSpawnEvent(
                mob,
                level,
                mob.getX(),
                mob.getY(),
                mob.getZ(),
                spawnType,
                MobSpawnEvent.Phase.FINALIZE,
                spawnGroupData
        );
        GeminiEvents.post(spawnEvent);
        if (spawnEvent.isCanceled()) {
            callback.setReturnValue(null);
        }
    }
}
