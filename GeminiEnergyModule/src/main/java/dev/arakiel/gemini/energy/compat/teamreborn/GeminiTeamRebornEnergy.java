package dev.arakiel.gemini.energy.compat.teamreborn;

import dev.arakiel.gemini.capability.BlockCapabilityContext;
import dev.arakiel.gemini.capability.CapabilityPriority;
import dev.arakiel.gemini.capability.EnergyStorageView;
import dev.arakiel.gemini.capability.GeminiCapabilities;
import dev.arakiel.gemini.capability.ItemCapabilityContext;
import dev.arakiel.gemini.energy.SnapshotEnergyStorageView;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class GeminiTeamRebornEnergy {
    private static final Set<BlockEntityType<?>> EXPORTED_BLOCK_ENTITIES = ConcurrentHashMap.newKeySet();
    private static final Set<Block> EXPORTED_BLOCKS = ConcurrentHashMap.newKeySet();
    private static final Set<Item> EXPORTED_ITEMS = ConcurrentHashMap.newKeySet();

    private GeminiTeamRebornEnergy() {
    }

    public static void exportBlockEntity(BlockEntityType<?> type) {
        Objects.requireNonNull(type, "type");
        if (!EXPORTED_BLOCK_ENTITIES.add(type)) {
            return;
        }
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) ->
                        adapter(findBlock(blockEntity, direction).orElse(null)),
                type);
    }

    public static void exportBlock(Block block) {
        Objects.requireNonNull(block, "block");
        if (!EXPORTED_BLOCKS.add(block)) {
            return;
        }
        EnergyStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, direction) ->
                        adapter(GeminiCapabilities.registry().find(
                                GeminiCapabilities.ENERGY,
                                new BlockCapabilityContext(level, pos, state, blockEntity, direction)
                        ).orElse(null)),
                block);
    }

    public static void exportContainedItem(Item item) {
        Objects.requireNonNull(item, "item");
        if (!EXPORTED_ITEMS.add(item)) {
            return;
        }
        EnergyStorage.ITEM.registerForItems((stack, context) ->
                        adapter(GeminiCapabilities.registry().find(
                                GeminiCapabilities.ENERGY,
                                new ItemCapabilityContext(stack, null, null)
                        ).orElse(null)),
                item);
    }

    public static void importBlockEntity(BlockEntityType<?> type, CapabilityPriority priority) {
        Objects.requireNonNull(type, "type");
        GeminiCapabilities.registry().registerBlockEntity(
                GeminiCapabilities.ENERGY,
                type,
                context -> importBlock(context, priority),
                priority
        );
    }

    public static void importBlock(Block block, CapabilityPriority priority) {
        Objects.requireNonNull(block, "block");
        GeminiCapabilities.registry().registerBlock(
                GeminiCapabilities.ENERGY,
                block,
                context -> importBlock(context, priority),
                priority
        );
    }

    public static EnergyStorageView wrap(EnergyStorage storage) {
        return new TeamRebornEnergyView(storage);
    }

    public static @Nullable GeminiEnergyStorageAdapter adapter(@Nullable EnergyStorageView view) {
        return view instanceof SnapshotEnergyStorageView snapshot ? new GeminiEnergyStorageAdapter(snapshot) : null;
    }

    private static Optional<EnergyStorageView> findBlock(BlockEntity blockEntity, net.minecraft.core.Direction direction) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return Optional.empty();
        }
        return GeminiCapabilities.registry().find(
                GeminiCapabilities.ENERGY,
                new BlockCapabilityContext(
                        level,
                        blockEntity.getBlockPos(),
                        level.getBlockState(blockEntity.getBlockPos()),
                        blockEntity,
                        direction
                )
        );
    }

    private static Optional<EnergyStorageView> importBlock(
            BlockCapabilityContext context,
            CapabilityPriority ignored
    ) {
        EnergyStorage storage = EnergyStorage.SIDED.find(
                context.level(),
                context.pos(),
                context.state(),
                context.blockEntity(),
                context.direction()
        );
        return storage == null ? Optional.empty() : Optional.of(wrap(storage));
    }
}
