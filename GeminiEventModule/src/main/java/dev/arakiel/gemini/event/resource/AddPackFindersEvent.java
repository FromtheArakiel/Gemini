package dev.arakiel.gemini.event.resource;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddPackFindersEvent extends GeminiEvent {
    private final PackType type;
    private final List<RepositorySource> sources = new ArrayList<>();

    public AddPackFindersEvent(PackType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public PackType getType() {
        return this.type;
    }

    public void addRepositorySource(RepositorySource source) {
        this.sources.add(Objects.requireNonNull(source, "source"));
    }

    public List<RepositorySource> getSources() {
        return List.copyOf(this.sources);
    }
}
