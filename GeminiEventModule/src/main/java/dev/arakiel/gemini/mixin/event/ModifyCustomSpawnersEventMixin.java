package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.spawn.ModifyCustomSpawnersEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ModifyCustomSpawnersEventMixin {
    @Shadow
    @Final
    @Mutable
    private List<CustomSpawner> customSpawners;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gemini$modifyCustomSpawners(CallbackInfo callback) {
        List<CustomSpawner> mutableSpawners = new ArrayList<>(this.customSpawners);
        GeminiEvents.post(new ModifyCustomSpawnersEvent(
                (ServerLevel) (Object) this,
                mutableSpawners
        ));
        this.customSpawners = mutableSpawners;
    }
}
