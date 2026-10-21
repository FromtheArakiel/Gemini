package dev.arakiel.gemini.component.internal.token;

import dev.arakiel.gemini.component.api.Component;
import dev.arakiel.gemini.component.api.ConditionalToken;
import dev.arakiel.gemini.component.api.TokenKey;
import dev.arakiel.gemini.component.api.RespawnableComponent;
import dev.arakiel.gemini.component.api.ComponentSync;
import dev.arakiel.gemini.component.api.ComponentLifecycle;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConditionalData implements
        Component,
        RespawnableComponent<ConditionalData>,
        ComponentSync.AutoSyncedComponent,
        ComponentLifecycle.ServerTicking {
    private static final String TOKENS = "tokens";
    private static final String ID = "id";
    private static final String DATA = "data";

    private final Player owner;
    private final Map<TokenKey<?>, ConditionalToken> tokens = new LinkedHashMap<>();

    public ConditionalData(Player owner) {
        this.owner = owner;
    }

    public <T extends ConditionalToken> T getOrCreate(TokenKey<T> key) {
        ConditionalToken existing = tokens.get(key);
        if (existing != null) {
            return key.type().cast(existing);
        }
        T created = key.create();
        tokens.put(key, created);
        return created;
    }

    @SuppressWarnings("unchecked")
    public <T extends ConditionalToken> T get(TokenKey<T> key) {
        ConditionalToken token = tokens.get(key);
        return token == null ? null : (T) token;
    }

    public ConditionalToken remove(TokenKey<?> key) {
        return tokens.remove(key);
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public void clear() {
        tokens.clear();
    }

    public List<TokenKey<?>> keys() {
        return List.copyOf(tokens.keySet());
    }

    @Override
    public void serverTick() {
        if (owner != null && owner.level().isClientSide) {
            return;
        }
        boolean changed = false;
        Iterator<Map.Entry<TokenKey<?>, ConditionalToken>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<TokenKey<?>, ConditionalToken> entry = iterator.next();
            if (entry.getValue().tick(owner)) {
                iterator.remove();
                changed = true;
            }
        }
        if (changed && owner instanceof ServerPlayer serverPlayer) {
            ConditionalTokenRegistration.key().sync(serverPlayer);
        }
    }

    @Override
    public void copyForRespawn(
            ConditionalData original,
            HolderLookup.Provider registries,
            boolean lossless,
            boolean keepInventory,
            boolean sameCharacter
    ) {
        tokens.clear();
        for (Map.Entry<TokenKey<?>, ConditionalToken> entry : original.tokens.entrySet()) {
            if (!lossless && !entry.getValue().retainOnDeath(owner)) {
                continue;
            }
            ConditionalToken token = entry.getKey().create();
            CompoundTag data = new CompoundTag();
            entry.getValue().writeToNbt(data, registries);
            token.readFromNbt(data, registries);
            tokens.put(entry.getKey(), token);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tokens.clear();
        ListTag list = tag.getList(TOKENS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(entry.getString(ID));
            if (id == null) {
                continue;
            }
            TokenKey<?> key = TokenKey.get(id);
            if (key == null) {
                continue;
            }
            ConditionalToken token = key.create();
            token.readFromNbt(entry.getCompound(DATA), registries);
            tokens.put(key, token);
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<TokenKey<?>, ConditionalToken> entry : tokens.entrySet()) {
            CompoundTag item = new CompoundTag();
            item.putString(ID, entry.getKey().id().toString());
            CompoundTag data = new CompoundTag();
            entry.getValue().writeToNbt(data, registries);
            item.put(DATA, data);
            list.add(item);
        }
        tag.put(TOKENS, list);
    }
}
