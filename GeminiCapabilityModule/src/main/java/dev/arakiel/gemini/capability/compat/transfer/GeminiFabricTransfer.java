package dev.arakiel.gemini.capability.compat.transfer;

import dev.arakiel.gemini.capability.BlockCapabilityContext;
import dev.arakiel.gemini.capability.CapabilityRegistry;
import dev.arakiel.gemini.capability.GeminiCapabilities;
import dev.arakiel.gemini.capability.ItemCapabilityContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class GeminiFabricTransfer {
    private static final Set<BlockEntityType<?>> EXPORTED_ITEM_BLOCK_ENTITIES = ConcurrentHashMap.newKeySet();
    private static final Set<BlockEntityType<?>> EXPORTED_FLUID_BLOCK_ENTITIES = ConcurrentHashMap.newKeySet();
    private static final Set<Block> EXPORTED_ITEM_BLOCKS = ConcurrentHashMap.newKeySet();
    private static final Set<Block> EXPORTED_FLUID_BLOCKS = ConcurrentHashMap.newKeySet();
    private static final Set<Item> EXPORTED_ITEM_ITEMS = ConcurrentHashMap.newKeySet();
    private static final Set<Item> EXPORTED_FLUID_ITEMS = ConcurrentHashMap.newKeySet();

    private GeminiFabricTransfer() {
    }

    public static void exportItemStorage(BlockEntityType<?> type) {
        exportItemStorage(type, GeminiCapabilities.registry());
    }

    public static void exportItemStorage(BlockEntityType<?> type, CapabilityRegistry registry) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(registry, "registry");
        if (!EXPORTED_ITEM_BLOCK_ENTITIES.add(type)) {
            return;
        }
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) ->
                        findItem(registry, context(blockEntity, direction)),
                type);
    }

    public static void exportFluidStorage(BlockEntityType<?> type) {
        exportFluidStorage(type, GeminiCapabilities.registry());
    }

    public static void exportFluidStorage(BlockEntityType<?> type, CapabilityRegistry registry) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(registry, "registry");
        if (!EXPORTED_FLUID_BLOCK_ENTITIES.add(type)) {
            return;
        }
        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) ->
                        findFluid(registry, context(blockEntity, direction)),
                type);
    }

    public static void exportItemStorage(Block block) {
        exportItemStorage(block, GeminiCapabilities.registry());
    }

    public static void exportItemStorage(Block block, CapabilityRegistry registry) {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(registry, "registry");
        if (!EXPORTED_ITEM_BLOCKS.add(block)) {
            return;
        }
        ItemStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, direction) ->
                        findItem(registry, new BlockCapabilityContext(level, pos, state, blockEntity, direction)),
                block);
    }

    public static void exportFluidStorage(Block block) {
        exportFluidStorage(block, GeminiCapabilities.registry());
    }

    public static void exportFluidStorage(Block block, CapabilityRegistry registry) {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(registry, "registry");
        if (!EXPORTED_FLUID_BLOCKS.add(block)) {
            return;
        }
        FluidStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, direction) ->
                        findFluid(registry, new BlockCapabilityContext(level, pos, state, blockEntity, direction)),
                block);
    }

    public static void exportContainedItemStorage(Item item) {
        exportContainedItemStorage(item, GeminiCapabilities.registry());
    }

    public static void exportContainedItemStorage(Item item, CapabilityRegistry registry) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(registry, "registry");
        if (!EXPORTED_ITEM_ITEMS.add(item)) {
            return;
        }
        ItemStorage.ITEM.registerForItems((stack, ignored) ->
                        findItem(registry, new ItemCapabilityContext(stack, null, null)),
                item);
    }

    public static void exportContainedFluidStorage(Item item) {
        exportContainedFluidStorage(item, GeminiCapabilities.registry());
    }

    public static void exportContainedFluidStorage(Item item, CapabilityRegistry registry) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(registry, "registry");
        if (!EXPORTED_FLUID_ITEMS.add(item)) {
            return;
        }
        FluidStorage.ITEM.registerForItems((stack, ignored) ->
                        findFluid(registry, new ItemCapabilityContext(stack, null, null)),
                item);
    }

    public static @Nullable Storage<ItemVariant> findItemStorage(
            CapabilityRegistry registry,
            Level level,
            BlockPos pos,
            @Nullable BlockState state,
            @Nullable BlockEntity blockEntity,
            @Nullable net.minecraft.core.Direction direction
    ) {
        BlockState resolvedState = state == null ? level.getBlockState(pos) : state;
        return findItem(
                registry,
                new BlockCapabilityContext(level, pos, resolvedState, blockEntity, direction)
        );
    }

    public static @Nullable Storage<ItemVariant> findItemStorage(
            CapabilityRegistry registry,
            ItemStack stack,
            @Nullable net.minecraft.world.entity.LivingEntity holder
    ) {
        return findItem(registry, new ItemCapabilityContext(stack, holder, null));
    }

    public static @Nullable Storage<FluidVariant> findFluidStorage(
            CapabilityRegistry registry,
            Level level,
            BlockPos pos,
            @Nullable BlockState state,
            @Nullable BlockEntity blockEntity,
            @Nullable net.minecraft.core.Direction direction
    ) {
        BlockState resolvedState = state == null ? level.getBlockState(pos) : state;
        return findFluid(
                registry,
                new BlockCapabilityContext(level, pos, resolvedState, blockEntity, direction)
        );
    }

    private static @Nullable Storage<ItemVariant> findItem(
            CapabilityRegistry registry,
            BlockCapabilityContext context
    ) {
        return registry.find(GeminiCapabilities.ITEM_STORAGE, context)
                .map(FabricItemStorageAdapter::new)
                .orElse(null);
    }

    private static @Nullable Storage<ItemVariant> findItem(
            CapabilityRegistry registry,
            ItemCapabilityContext context
    ) {
        return registry.find(GeminiCapabilities.ITEM_STORAGE, context)
                .map(FabricItemStorageAdapter::new)
                .orElse(null);
    }

    private static @Nullable Storage<FluidVariant> findFluid(
            CapabilityRegistry registry,
            BlockCapabilityContext context
    ) {
        return registry.find(GeminiCapabilities.FLUID_STORAGE, context)
                .map(FabricFluidStorageAdapter::new)
                .orElse(null);
    }

    private static @Nullable Storage<FluidVariant> findFluid(
            CapabilityRegistry registry,
            ItemCapabilityContext context
    ) {
        return registry.find(GeminiCapabilities.FLUID_STORAGE, context)
                .map(FabricFluidStorageAdapter::new)
                .orElse(null);
    }

    private static BlockCapabilityContext context(BlockEntity blockEntity, net.minecraft.core.Direction direction) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            throw new IllegalStateException("Block entity has no level: " + blockEntity);
        }
        BlockPos pos = blockEntity.getBlockPos();
        return new BlockCapabilityContext(level, pos, level.getBlockState(pos), blockEntity, direction);
    }
}
