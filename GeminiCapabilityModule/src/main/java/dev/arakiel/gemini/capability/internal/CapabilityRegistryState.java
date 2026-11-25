package dev.arakiel.gemini.capability.internal;

import dev.arakiel.gemini.capability.BlockCapabilityContext;
import dev.arakiel.gemini.capability.CapabilityContext;
import dev.arakiel.gemini.capability.CapabilityKey;
import dev.arakiel.gemini.capability.CapabilityProviderHandle;
import dev.arakiel.gemini.capability.EntityCapabilityContext;
import dev.arakiel.gemini.capability.ItemCapabilityContext;
import dev.arakiel.gemini.capability.LevelCapabilityContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public final class CapabilityRegistryState {
    public enum State {
        COLLECTING,
        SEALED
    }

    private final Map<CapabilityKey<?>, List<CapabilityRegistration<?>>> registrations = new LinkedHashMap<>();
    private final Map<CapabilityProviderHandle, CapabilityRegistration<?>> handleLookup = new HashMap<>();
    private final CapabilityCache cache = new CapabilityCache();
    private State state = State.COLLECTING;
    private long nextOrder;

    public synchronized void add(CapabilityProviderHandle handle, CapabilityRegistration<?> registration) {
        if (this.state == State.SEALED) {
            throw new IllegalStateException("Capability registry is sealed");
        }
        this.registrations.computeIfAbsent(registration.key(), ignored -> new ArrayList<>()).add(registration);
        this.handleLookup.put(handle, registration);
        this.invalidateCaches();
    }

    public synchronized List<CapabilityRegistration<?>> registrations(CapabilityKey<?> key) {
        return List.copyOf(this.registrations.getOrDefault(key, List.of()));
    }

    public synchronized void seal() {
        this.state = State.SEALED;
    }

    public synchronized boolean isSealed() {
        return this.state == State.SEALED;
    }

    public synchronized int size() {
        return this.handleLookup.size();
    }

    public synchronized List<CapabilityProviderHandle> handles() {
        return List.copyOf(this.handleLookup.keySet());
    }

    public synchronized void requireHandle(CapabilityProviderHandle handle) {
        if (!this.handleLookup.containsKey(handle)) {
            throw new IllegalStateException("Capability provider is not registered");
        }
    }

    public synchronized boolean containsHandle(CapabilityProviderHandle handle) {
        return this.handleLookup.containsKey(handle);
    }

    public synchronized boolean remove(CapabilityProviderHandle handle) {
        CapabilityRegistration<?> registration = this.handleLookup.remove(handle);
        if (registration == null) {
            return false;
        }
        List<CapabilityRegistration<?>> keyRegistrations = this.registrations.get(registration.key());
        if (keyRegistrations != null) {
            keyRegistrations.remove(registration);
            if (keyRegistrations.isEmpty()) {
                this.registrations.remove(registration.key());
            }
        }
        this.invalidateCaches();
        return true;
    }

    public void invalidateCaches() {
        this.cache.invalidate();
    }

    public long cacheGeneration() {
        return this.cache.generation();
    }

    public synchronized long nextOrder() {
        return this.nextOrder++;
    }

    public abstract static class Source {
        private final Kind kind;

        private Source(Kind kind) {
            this.kind = kind;
        }

        public static Source blockEntityType(BlockEntityType<?> type) {
            return new ExactSource(Kind.BLOCK_ENTITY_TYPE, type);
        }

        public static Source blockEntityClass(Class<? extends BlockEntity> type) {
            return new ClassSource(Kind.BLOCK_ENTITY_CLASS, type);
        }

        public static Source block(Block block) {
            return new ExactSource(Kind.BLOCK, block);
        }

        public static Source entityType(EntityType<?> type) {
            return new ExactSource(Kind.ENTITY_TYPE, type);
        }

        public static Source entityClass(Class<? extends Entity> type) {
            return new ClassSource(Kind.ENTITY_CLASS, type);
        }

        public static Source item(Item item) {
            return new ExactSource(Kind.ITEM, item);
        }

        public static Source itemStack(Predicate<ItemStack> predicate) {
            return new ItemStackSource(predicate);
        }

        public static Source level() {
            return new LevelSource();
        }

        public abstract boolean matches(CapabilityContext context);

        public abstract int specificity(CapabilityContext context);

        public abstract String displayName();

        public Kind kind() {
            return this.kind;
        }

        private enum Kind {
            BLOCK_ENTITY_TYPE,
            BLOCK_ENTITY_CLASS,
            BLOCK,
            ENTITY_TYPE,
            ENTITY_CLASS,
            ITEM,
            ITEM_STACK,
            LEVEL
        }

        private static final class ExactSource extends Source {
            private final Object identity;

            private ExactSource(Kind kind, Object identity) {
                super(kind);
                this.identity = Objects.requireNonNull(identity, "identity");
            }

            @Override
            public boolean matches(CapabilityContext context) {
                return switch (this.kind()) {
                    case BLOCK_ENTITY_TYPE -> context instanceof BlockCapabilityContext blockContext
                            && blockContext.blockEntity() != null
                            && blockContext.blockEntity().getType() == this.identity;
                    case BLOCK -> context instanceof BlockCapabilityContext blockContext
                            && blockContext.state().getBlock() == this.identity;
                    case ENTITY_TYPE -> context instanceof EntityCapabilityContext entityContext
                            && entityContext.entity().getType() == this.identity;
                    case ITEM -> context instanceof ItemCapabilityContext itemContext
                            && itemContext.stack().getItem() == this.identity;
                    default -> false;
                };
            }

            @Override
            public int specificity(CapabilityContext context) {
                return switch (this.kind()) {
                    case BLOCK_ENTITY_TYPE, ENTITY_TYPE, ITEM -> 0;
                    case BLOCK -> 50;
                    default -> 100;
                };
            }

            @Override
            public String displayName() {
                return this.kind().name() + "[" + this.identity + "]";
            }
        }

        private static final class ClassSource extends Source {
            private final Class<?> type;

            private ClassSource(Kind kind, Class<?> type) {
                super(kind);
                this.type = Objects.requireNonNull(type, "type");
            }

            @Override
            public boolean matches(CapabilityContext context) {
                return switch (this.kind()) {
                    case BLOCK_ENTITY_CLASS -> context instanceof BlockCapabilityContext blockContext
                            && blockContext.blockEntity() != null
                            && this.type.isInstance(blockContext.blockEntity());
                    case ENTITY_CLASS -> context instanceof EntityCapabilityContext entityContext
                            && this.type.isInstance(entityContext.entity());
                    default -> false;
                };
            }

            @Override
            public int specificity(CapabilityContext context) {
                Class<?> actual = switch (this.kind()) {
                    case BLOCK_ENTITY_CLASS -> ((BlockCapabilityContext) context).blockEntity().getClass();
                    case ENTITY_CLASS -> ((EntityCapabilityContext) context).entity().getClass();
                    default -> this.type;
                };
                return 10 + distance(actual, this.type);
            }

            @Override
            public String displayName() {
                return this.kind().name() + "[" + this.type.getName() + "]";
            }
        }

        private static final class ItemStackSource extends Source {
            private final Predicate<ItemStack> predicate;

            private ItemStackSource(Predicate<ItemStack> predicate) {
                super(Kind.ITEM_STACK);
                this.predicate = Objects.requireNonNull(predicate, "predicate");
            }

            @Override
            public boolean matches(CapabilityContext context) {
                return context instanceof ItemCapabilityContext itemContext
                        && this.predicate.test(itemContext.stack());
            }

            @Override
            public int specificity(CapabilityContext context) {
                return 20;
            }

            @Override
            public String displayName() {
                return "ITEM_STACK";
            }
        }

        private static final class LevelSource extends Source {
            private LevelSource() {
                super(Kind.LEVEL);
            }

            @Override
            public boolean matches(CapabilityContext context) {
                return context instanceof LevelCapabilityContext;
            }

            @Override
            public int specificity(CapabilityContext context) {
                return 100;
            }

            @Override
            public String displayName() {
                return "LEVEL";
            }
        }

        private static int distance(Class<?> actual, Class<?> expected) {
            int distance = 0;
            for (Class<?> current = actual; current != null; current = current.getSuperclass()) {
                if (current == expected) {
                    return distance;
                }
                distance++;
            }
            return Integer.MAX_VALUE / 2;
        }
    }
}
