package dev.arakiel.gemini.component.internal.itemstack;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.CopyableComponent;
import dev.arakiel.gemini.component.GeminiComponents;
import dev.arakiel.gemini.component.TransientComponent;
import dev.arakiel.gemini.component.internal.base.asm.ComponentBootstrap;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemStackComponentsTest {
    private static final HolderLookup.Provider PROVIDER = RegistryAccess.EMPTY;
    private static final ResourceLocation PERSISTENT_ID =
            Gemini.id("test/item_stack_persistent");
    private static final ResourceLocation TRANSIENT_ID =
            Gemini.id("test/item_stack_transient");
    private static final ResourceLocation DEPENDENT_ID =
            Gemini.id("test/item_stack_dependent");

    private static ComponentKey<PersistentValue> persistentKey;
    private static ComponentKey<TransientValue> transientKey;
    private static ComponentKey<DependentValue> dependentKey;
    private static ComponentContainer.Factory<ItemStack> factory;
    private static DataComponentType<ItemStackComponentData> componentType;

    @BeforeAll
    static void setUp() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        ComponentBootstrap.INSTANCE.addComponentIdForTesting(PERSISTENT_ID);
        ComponentBootstrap.INSTANCE.addComponentIdForTesting(TRANSIENT_ID);
        ComponentBootstrap.INSTANCE.addComponentIdForTesting(DEPENDENT_ID);
        componentType = DataComponentType.<ItemStackComponentData>builder()
                .persistent(ItemStackComponentData.CODEC)
                .networkSynchronized(ItemStackComponentData.STREAM_CODEC)
                .build();

        persistentKey = GeminiComponents.key(PERSISTENT_ID, PersistentValue.class);
        transientKey = GeminiComponents.key(TRANSIENT_ID, TransientValue.class);
        dependentKey = GeminiComponents.key(DEPENDENT_ID, DependentValue.class);

        StaticItemStackComponentPlugin plugin = new StaticItemStackComponentPlugin(
                List.of(registry -> {
                    registry.register(persistentKey, stack -> new PersistentValue());
                    registry.register(transientKey, stack -> new TransientValue());
                    registry.beginRegistration(dependentKey)
                            .after(persistentKey)
                            .end(stack -> new DependentValue());
                })
        );
        factory = plugin.factory();
    }

    @AfterAll
    static void clearTestKeys() {
        ComponentRegistryTestSupport.clear(PERSISTENT_ID, TRANSIENT_ID, DEPENDENT_ID);
    }

    @Test
    void returnsLiveRegisteredInstances() {
        ItemStack stack = stack();

        PersistentValue first = get(persistentKey, stack);
        PersistentValue second = get(persistentKey, stack);

        assertSame(first, second);
        first.value = 12;
        assertEquals(12, get(persistentKey, stack).value);
    }

    @Test
    void cachesGeneratedContainerFactory() {
        assertSame(
                StaticItemStackComponentPlugin.INSTANCE.factory(),
                StaticItemStackComponentPlugin.INSTANCE.factory()
        );
    }

    @Test
    void persistsOnlyPersistentComponentsThroughComponentNbtCodec() {
        ItemStack original = stack();
        get(persistentKey, original).value = 41;
        get(transientKey, original).value = 99;
        commit(original);

        ItemStackComponentData data = original.get(componentType);
        Tag saved = ItemStackComponentData.CODEC
                .encodeStart(NbtOps.INSTANCE, data)
                .getOrThrow();
        ItemStackComponentData decodedData = ItemStackComponentData.CODEC
                .parse(NbtOps.INSTANCE, saved)
                .getOrThrow();
        ItemStack decoded = stack();
        decoded.set(componentType, decodedData);

        assertEquals(41, get(persistentKey, decoded).value);
        assertEquals(0, get(transientKey, decoded).value);
    }

    @Test
    void roundTripsThroughComponentNetworkCodec() {
        ItemStack original = stack();
        get(persistentKey, original).value = 73;
        get(transientKey, original).value = 17;
        commit(original);

        RegistryFriendlyByteBuf buffer =
                new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        try {
            ItemStackComponentData.STREAM_CODEC.encode(
                    buffer,
                    original.get(componentType)
            );
            ItemStackComponentData decodedData =
                    ItemStackComponentData.STREAM_CODEC.decode(buffer);
            ItemStack decoded = stack();
            decoded.set(componentType, decodedData);

            assertEquals(73, get(persistentKey, decoded).value);
            assertEquals(0, get(transientKey, decoded).value);
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    void copyDeepCopiesPersistentAndTransientState() {
        ItemStack original = stack();
        get(persistentKey, original).value = 5;
        get(transientKey, original).value = 8;
        commit(original);

        ItemStack copy = original.copy();
        GeminiItemStackInternals.copyRuntime(
                original,
                copy,
                factory,
                componentType
        );

        PersistentValue copiedPersistent = get(persistentKey, copy);
        TransientValue copiedTransient = get(transientKey, copy);
        assertEquals(5, copiedPersistent.value);
        assertEquals(8, copiedTransient.value);

        copiedPersistent.value = 50;
        copiedTransient.value = 80;

        assertEquals(5, get(persistentKey, original).value);
        assertEquals(8, get(transientKey, original).value);
    }

    @Test
    void splitProducesIndependentComponentState() {
        ItemStack original = stack();
        original.setCount(3);
        get(persistentKey, original).value = 9;
        commit(original);

        ItemStack split = original.split(2);
        GeminiItemStackInternals.copyRuntime(
                original,
                split,
                factory,
                componentType
        );

        assertEquals(2, split.getCount());
        assertEquals(1, original.getCount());
        assertEquals(9, get(persistentKey, split).value);

        get(persistentKey, split).value = 10;
        assertEquals(9, get(persistentKey, original).value);
    }

    @Test
    void persistentStateControlsStackMatching() {
        ItemStack first = stack();
        ItemStack second = stack();
        get(persistentKey, first).value = 2;
        get(persistentKey, second).value = 2;
        commit(first);
        commit(second);

        assertTrue(ItemStack.matches(first, second));

        get(persistentKey, second).value = 3;
        commit(second);
        assertFalse(ItemStack.matches(first, second));
    }

    @Test
    void transientStateDoesNotAffectStackMatchingOrDataComponents() {
        ItemStack first = stack();
        ItemStack second = stack();
        get(transientKey, first).value = 1;
        get(transientKey, second).value = 2;
        commit(first);
        commit(second);

        assertTrue(ItemStack.matches(first, second));
        assertNull(first.get(componentType));
        assertNull(second.get(componentType));
    }

    @Test
    void clearingAllPersistentStateRemovesDataComponent() {
        ItemStack stack = stack();
        get(persistentKey, stack).value = 4;
        commit(stack);
        assertNotNull(stack.get(componentType));

        get(persistentKey, stack).value = 0;
        commit(stack);

        assertNull(stack.get(componentType));
        assertTrue(stack.getComponentsPatch().isEmpty());
    }

    @Test
    void emptyStackNeverReceivesComponents() {
        ItemStack empty = ItemStack.EMPTY;
        ItemStackComponentRuntime runtime =
                GeminiItemStackInternals.getOrCreateRuntime(
                        empty,
                        factory,
                        PROVIDER,
                        componentType
                );

        assertSame(ComponentContainer.EMPTY, runtime.container());
        assertTrue(runtime.container().keys().isEmpty());
        assertFalse(runtime.container().hasComponents());
        assertNull(empty.get(componentType));
    }

    @Test
    void preservesUnknownComponentsAcrossCommit() {
        ItemStack stack = stack();
        CompoundTag unknown = new CompoundTag();
        unknown.putString("other_mod:hidden", "keep-me");
        CompoundTag snapshot = new CompoundTag();
        snapshot.put("gemini_components", unknown);
        stack.set(
                componentType,
                new ItemStackComponentData(snapshot, PROVIDER)
        );

        get(persistentKey, stack).value = 27;
        commit(stack);

        CompoundTag committed = stack.get(componentType).snapshot();
        assertEquals(
                "keep-me",
                committed.getCompound("gemini_components")
                        .getString("other_mod:hidden")
        );
    }

    @Test
    void resolvesDependenciesInRegistrationOrder() {
        ItemStack stack = stack();
        List<ComponentKey<?>> keys = List.copyOf(container(stack).keys());

        assertTrue(keys.indexOf(persistentKey) < keys.indexOf(dependentKey));
    }

    @Test
    void dataComponentPatchCanBeReadAndReapplied() {
        ItemStack original = stack();
        get(persistentKey, original).value = 31;
        commit(original);

        ItemStack copy = stack();
        copy.applyComponents(original.getComponentsPatch());
        GeminiItemStackInternals.invalidate(copy);

        assertEquals(31, get(persistentKey, copy).value);
    }

    private static ItemStack stack() {
        return new ItemStack(Items.STICK);
    }

    private static ComponentContainer container(ItemStack stack) {
        return GeminiItemStackInternals
                .getOrCreateRuntime(stack, factory, PROVIDER, componentType)
                .container();
    }

    private static void commit(ItemStack stack) {
        GeminiItemStackInternals.commit(
                stack,
                PROVIDER,
                factory,
                componentType
        );
    }

    private static <C extends Component> C get(
            ComponentKey<C> key,
            ItemStack stack
    ) {
        return key.getFromContainer(container(stack));
    }

    public static final class PersistentValue implements Component {
        private int value;

        @Override
        public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
            this.value = tag.getInt("value");
        }

        @Override
        public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
            if (this.value != 0) {
                tag.putInt("value", this.value);
            }
        }
    }

    public static final class TransientValue
            implements TransientComponent, CopyableComponent<TransientValue> {
        private int value;

        @Override
        public void copyFrom(TransientValue other, HolderLookup.Provider provider) {
            this.value = other.value;
        }
    }

    public static final class DependentValue implements Component {
        @Override
        public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        }

        @Override
        public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
        }
    }
}
