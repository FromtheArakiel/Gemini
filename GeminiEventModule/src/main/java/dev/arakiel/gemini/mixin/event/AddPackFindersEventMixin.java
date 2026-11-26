package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.resource.AddPackFindersEvent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.LinkedHashSet;

@Mixin(PackRepository.class)
public abstract class AddPackFindersEventMixin {
    @Shadow
    @Mutable
    private Set<RepositorySource> sources;

    @Inject(method = "reload", at = @At("HEAD"))
    private void gemini$addPackFinders(CallbackInfo callback) {
        Set<RepositorySource> mutableSources = new LinkedHashSet<>(this.sources);
        for (PackType type : PackType.values()) {
            AddPackFindersEvent event = new AddPackFindersEvent(type);
            GeminiEvents.post(event);
            mutableSources.addAll(event.getSources());
        }
        this.sources = mutableSources;
    }
}
