package dev.arakiel.gemini.component.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class ComponentCallbacks {
    private ComponentCallbacks() {
    }

    @FunctionalInterface
    public interface BlockEntitySyncAround {
        Event<BlockEntitySyncAround> EVENT = EventFactory.createArrayBacked(
                BlockEntitySyncAround.class,
                blockEntity -> {
                },
                listeners -> blockEntity -> {
                    for (BlockEntitySyncAround callback : listeners) {
                        callback.onBlockEntitySync(blockEntity);
                    }
                }
        );

        void onBlockEntitySync(BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface BlockEntitySync {
        Event<BlockEntitySync> EVENT = EventFactory.createArrayBacked(
                BlockEntitySync.class,
                (player, blockEntity) -> {
                },
                listeners -> (player, blockEntity) -> {
                    for (BlockEntitySync callback : listeners) {
                        callback.onBlockEntitySync(player, blockEntity);
                    }
                }
        );

        void onBlockEntitySync(ServerPlayer player, BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface ChunkSync {
        Event<ChunkSync> EVENT = EventFactory.createArrayBacked(
                ChunkSync.class,
                (player, chunk) -> {
                },
                listeners -> (player, chunk) -> {
                    for (ChunkSync callback : listeners) {
                        callback.onChunkSync(player, chunk);
                    }
                }
        );

        void onChunkSync(ServerPlayer player, LevelChunk chunk);
    }

    @FunctionalInterface
    public interface PlayerSync {
        Event<PlayerSync> EVENT = EventFactory.createArrayBacked(
                PlayerSync.class,
                player -> {
                },
                listeners -> player -> {
                    for (PlayerSync callback : listeners) {
                        callback.onPlayerSync(player);
                    }
                }
        );

        void onPlayerSync(ServerPlayer player);
    }

    @FunctionalInterface
    public interface ScoreboardSync {
        Event<ScoreboardSync> EVENT = EventFactory.createArrayBacked(
                ScoreboardSync.class,
                (player, scoreboard) -> {
                },
                listeners -> (player, scoreboard) -> {
                    for (ScoreboardSync callback : listeners) {
                        callback.onScoreboardSync(player, scoreboard);
                    }
                }
        );

        void onScoreboardSync(ServerPlayer player, Scoreboard scoreboard);
    }

    @FunctionalInterface
    public interface TeamAdd {
        Event<TeamAdd> EVENT = EventFactory.createArrayBacked(
                TeamAdd.class,
                team -> {
                },
                listeners -> team -> {
                    for (TeamAdd callback : listeners) {
                        callback.onTeamAdded(team);
                    }
                }
        );

        void onTeamAdded(PlayerTeam team);
    }

    @FunctionalInterface
    public interface TrackingStart {
        Event<TrackingStart> EVENT = EventFactory.createArrayBacked(
                TrackingStart.class,
                (player, entity) -> {
                },
                listeners -> (player, entity) -> {
                    for (TrackingStart callback : listeners) {
                        callback.onPlayerStartTracking(player, entity);
                    }
                }
        );

        void onPlayerStartTracking(ServerPlayer player, Entity entity);
    }

    @FunctionalInterface
    public interface WorldSync {
        Event<WorldSync> EVENT = EventFactory.createArrayBacked(
                WorldSync.class,
                (player, level) -> {
                },
                listeners -> (player, level) -> {
                    for (WorldSync callback : listeners) {
                        callback.onPlayerStartTracking(player, level);
                    }
                }
        );

        void onPlayerStartTracking(ServerPlayer player, ServerLevel level);
    }
}
