package dev.arakiel.gemini.capability;

import dev.arakiel.gemini.capability.internal.CapabilityLookupInternals;
import dev.arakiel.gemini.capability.internal.CapabilityRegistration;
import dev.arakiel.gemini.capability.internal.CapabilityRegistryState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class CapabilityRegistry {
    private final CapabilityRegistryState state = new CapabilityRegistryState();

    public <T> CapabilityProviderHandle registerBlockEntity(
            CapabilityKey<T> key,
            BlockEntityType<?> type,
            CapabilityProvider<T, BlockCapabilityContext> provider,
            CapabilityPriority priority
    ) {
        Objects.requireNonNull(type, "type");
        return this.register(key, CapabilityRegistryState.Source.blockEntityType(type), provider, priority);
    }

    public <T> CapabilityProviderHandle registerBlockEntityClass(
            CapabilityKey<T> key,
            Class<? extends BlockEntity> type,
            CapabilityProvider<T, BlockCapabilityContext> provider,
            CapabilityPriority priority
    ) {
        Objects.requireNonNull(type, "type");
        return this.register(key, CapabilityRegistryState.Source.blockEntityClass(type), provider, priority);
    }

    public <T> CapabilityProviderHandle registerBlock(
            CapabilityKey<T> key,
            Block block,
            CapabilityProvider<T, BlockCapabilityContext> provider,
            CapabilityPriority priority
    ) {
        Objects.requireNonNull(block, "block");
        return this.register(key, CapabilityRegistryState.Source.block(block), provider, priority);
    }

    public <T> CapabilityProviderHandle registerEntity(
            CapabilityKey<T> key,
            EntityType<?> type,
            CapabilityProvider<T, EntityCapabilityContext> provider,
            CapabilityPriority priority
    ) {
        Objects.requireNonNull(type, "type");
        return this.register(key, CapabilityRegistryState.Source.entityType(type), provider, priority);
    }

    public <T> CapabilityProviderHandle registerEntityClass(
            CapabilityKey<T> key,
            Class<? extends Entity> type,
            CapabilityProvider<T, EntityCapabilityContext> provider,
            CapabilityPriority priority
    ) {
        Objects.requireNonNull(type, "type");
        return this.register(key, CapabilityRegistryState.Source.entityClass(type), provider, priority);
    }

    public <T> CapabilityProviderHandle registerItem(
            CapabilityKey<T> key,
            Item item,
            CapabilityProvider<T, ItemCapabilityContext> provider,
            CapabilityPriority priority
    ) {
        Objects.requireNonNull(item, "item");
        return this.register(key, CapabilityRegistryState.Source.item(item), provider, priority);
    }

    public <T> CapabilityProviderHandle registerItemStack(
            CapabilityKey<T> key,
            Predicate<ItemStack> predicate,
            CapabilityProvider<T, ItemCapabilityContext> provider,
            CapabilityPriority priority
    ) {
        Objects.requireNonNull(predicate, "predicate");
        return this.register(key, CapabilityRegistryState.Source.itemStack(predicate), provider, priority);
    }

    public <T> CapabilityProviderHandle registerLevel(
            CapabilityKey<T> key,
            CapabilityProvider<T, LevelCapabilityContext> provider,
            CapabilityPriority priority
    ) {
        return this.register(key, CapabilityRegistryState.Source.level(), provider, priority);
    }

    public <T> Optional<T> find(CapabilityKey<T> key, CapabilityContext context) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(context, "context");
        this.state.seal();
        for (CapabilityRegistration<?> registration : this.ordered(key, context)) {
            Optional<?> result = registration.find(context);
            if (result.isPresent()) {
                @SuppressWarnings("unchecked")
                T value = (T) result.get();
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public <T> List<T> findAll(CapabilityKey<T> key, CapabilityContext context) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(context, "context");
        this.state.seal();
        List<T> results = new ArrayList<>();
        for (CapabilityRegistration<?> registration : this.ordered(key, context)) {
            Optional<?> result = registration.find(context);
            if (result.isPresent()) {
                @SuppressWarnings("unchecked")
                T value = (T) result.get();
                results.add(value);
            }
        }
        return List.copyOf(results);
    }

    public boolean provides(CapabilityKey<?> key, CapabilityContext context) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(context, "context");
        return this.find(key, context).isPresent();
    }

    public synchronized void seal() {
        this.state.seal();
    }

    public boolean isSealed() {
        return this.state.isSealed();
    }

    public int providerCount() {
        return this.state.size();
    }

    public List<CapabilityProviderHandle> handles() {
        return List.copyOf(this.state.handles());
    }

    synchronized void invalidate(CapabilityProviderHandle handle) {
        this.state.requireHandle(handle);
        this.state.invalidateCaches();
    }

    synchronized boolean unregister(CapabilityProviderHandle handle) {
        return this.state.remove(handle);
    }

    boolean isRegistered(CapabilityProviderHandle handle) {
        return this.state.containsHandle(handle);
    }

    private synchronized <T, C extends CapabilityContext> CapabilityProviderHandle register(
            CapabilityKey<T> key,
            CapabilityRegistryState.Source source,
            CapabilityProvider<T, C> provider,
            CapabilityPriority priority
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(priority, "priority");
        CapabilityRegistration<T> registration =
                new CapabilityRegistration<>(key, source, provider, priority, this.state.nextOrder());
        CapabilityProviderHandle handle = new CapabilityProviderHandle(this, registration);
        this.state.add(handle, registration);
        return handle;
    }

    private List<CapabilityRegistration<?>> ordered(CapabilityKey<?> key, CapabilityContext context) {
        return CapabilityLookupInternals.order(this.state.registrations(key), context);
    }
}
