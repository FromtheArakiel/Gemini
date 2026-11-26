package dev.arakiel.gemini.energy.internal;

import dev.arakiel.gemini.capability.BlockCapabilityContext;
import dev.arakiel.gemini.capability.EnergyStorageView;
import dev.arakiel.gemini.capability.GeminiCapabilities;
import dev.arakiel.gemini.energy.EnergyConduitView;
import dev.arakiel.gemini.energy.EnergyPushResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class EnergyPushEngine {
    private static final int MAX_NETWORK_BLOCKS = 2048;
    private static final int MAX_NETWORK_DEPTH = 128;

    private EnergyPushEngine() {
    }

    public static EnergyPushResult push(
            EnergyStorageView source,
            Collection<? extends EnergyStorageView> endpoints,
            long maxAmount,
            boolean simulate
    ) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(endpoints, "endpoints");
        if (maxAmount < 0) {
            throw new IllegalArgumentException("maxAmount cannot be negative");
        }
        if (maxAmount == 0 || !source.canExtract()) {
            return new EnergyPushResult(0, 0, 0, 0, endpoints.size());
        }

        Set<EnergyStorageView> unique = new LinkedHashSet<>();
        for (EnergyStorageView endpoint : endpoints) {
            if (endpoint != null && endpoint != source && endpoint.canReceive()) {
                unique.add(endpoint);
            }
        }
        if (unique.isEmpty()) {
            return new EnergyPushResult(0, 0, 0, 0, 0);
        }

        long available = Math.min(maxAmount, source.extract(maxAmount, true));
        if (available == 0) {
            return new EnergyPushResult(0, 0, 0, 0, unique.size());
        }

        long delivered = 0;
        for (EnergyStorageView endpoint : unique) {
            long remaining = available - delivered;
            if (remaining <= 0) {
                break;
            }
            long simulated = endpoint.receive(remaining, true);
            if (simulated <= 0) {
                continue;
            }
            if (simulate) {
                delivered += simulated;
                continue;
            }

            long extracted = source.extract(simulated, false);
            if (extracted <= 0) {
                continue;
            }
            long accepted = endpoint.receive(extracted, false);
            if (accepted < extracted) {
                source.receive(extracted - accepted, false);
            }
            delivered += accepted;
        }

        long extracted = delivered;
        long rejected = Math.max(0, available - delivered);
        return new EnergyPushResult(available, extracted, delivered, rejected, unique.size());
    }

    public static EnergyPushResult pushFrom(
            Level level,
            BlockPos sourcePos,
            @Nullable Direction sourceDirection,
            long maxAmount,
            boolean simulate
    ) {
        Optional<EnergyStorageView> source = findAt(level, sourcePos, sourceDirection);
        if (source.isEmpty()) {
            return new EnergyPushResult(0, 0, 0, 0, 0);
        }

        List<EnergyStorageView> endpoints = discoverEndpoints(level, sourcePos, source.get());
        return push(source.get(), endpoints, maxAmount, simulate);
    }

    private static List<EnergyStorageView> discoverEndpoints(
            Level level,
            BlockPos sourcePos,
            EnergyStorageView source
    ) {
        List<EnergyStorageView> endpoints = new ArrayList<>();
        Set<EnergyStorageView> uniqueEndpoints = new LinkedHashSet<>();
        Set<BlockPos> visited = new LinkedHashSet<>();
        ArrayDeque<SearchNode> queue = new ArrayDeque<>();
        visited.add(sourcePos);
        queue.add(new SearchNode(sourcePos, null, null, 0));

        while (!queue.isEmpty() && visited.size() < MAX_NETWORK_BLOCKS) {
            SearchNode node = queue.removeFirst();
            if (node.depth() >= MAX_NETWORK_DEPTH) {
                continue;
            }

            for (Direction outgoingSide : Direction.values()) {
                if (node.conduit() != null && outgoingSide == node.incomingSide()) {
                    continue;
                }
                if (node.conduit() != null
                        && !node.conduit().connects(node.incomingSide(), outgoingSide)) {
                    continue;
                }

                BlockPos nextPos = node.pos().relative(outgoingSide);
                if (!level.isLoaded(nextPos) || !visited.add(nextPos)) {
                    continue;
                }

                Direction capabilitySide = outgoingSide.getOpposite();
                Optional<EnergyStorageView> found = findAt(level, nextPos, capabilitySide);
                if (found.isEmpty()) {
                    continue;
                }
                EnergyStorageView view = found.get();
                if (view == source) {
                    continue;
                }
                if (view instanceof EnergyConduitView conduit) {
                    queue.addLast(new SearchNode(
                            nextPos,
                            capabilitySide,
                            conduit,
                            node.depth() + 1
                    ));
                } else if (view.canReceive() && uniqueEndpoints.add(view)) {
                    endpoints.add(view);
                }
            }
        }

        return endpoints;
    }

    private static Optional<EnergyStorageView> findAt(
            Level level,
            BlockPos pos,
            @Nullable Direction direction
    ) {
        BlockState state = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return GeminiCapabilities.registry().find(
                GeminiCapabilities.ENERGY,
                new BlockCapabilityContext(level, pos, state, blockEntity, direction)
        );
    }

    private record SearchNode(
            BlockPos pos,
            @Nullable Direction incomingSide,
            @Nullable EnergyConduitView conduit,
            int depth
    ) {
    }
}
