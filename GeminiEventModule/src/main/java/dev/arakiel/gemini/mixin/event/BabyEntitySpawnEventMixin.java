package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.BabyEntitySpawnEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class BabyEntitySpawnEventMixin {
    @Inject(method = "finalizeSpawnChildFromBreeding", at = @At("HEAD"), cancellable = true)
    private void gemini$babySpawn(
            ServerLevel level,
            Animal partner,
            AgeableMob child,
            CallbackInfo callback
    ) {
        BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(
                (Animal) (Object) this,
                partner,
                child
        );
        GeminiEvents.post(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}
