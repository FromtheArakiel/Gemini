package dev.arakiel.gemini.component.api;

import dev.arakiel.gemini.component.internal.base.asm.CalledByAsm;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.LevelData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class ComponentFactoryRegistries {
    private ComponentFactoryRegistries() {
    }

    @FunctionalInterface
    public interface TeamFactory<C extends Component> {
        @CalledByAsm
        @Contract(pure = true)
        C createForTeam(
                net.minecraft.world.scores.PlayerTeam team,
                net.minecraft.world.scores.Scoreboard scoreboard,
                @Nullable MinecraftServer server
        );
    }

    @FunctionalInterface
    public interface ScoreboardFactory<C extends Component> {
        @CalledByAsm
        @Contract(pure = true)
        C createForScoreboard(
                net.minecraft.world.scores.Scoreboard scoreboard,
                @Nullable MinecraftServer server
        );
    }

    public interface Block {
        <C extends Component, BE extends BlockEntity> void registerFor(
                Class<BE> target,
                ComponentKey<C> key,
                ComponentFactory<BE, C> factory
        );

        <C extends Component, BE extends BlockEntity> Block.Registration<C, BE> beginRegistration(
                Class<BE> target,
                ComponentKey<C> key
        );

        interface Registration<C extends Component, BE extends BlockEntity> {
            Registration<C, BE> filter(Predicate<Class<? extends BE>> filter);

            @ApiStatus.Experimental
            Registration<C, BE> after(ComponentKey<?> dependency);

            <I extends C> Registration<I, BE> impl(Class<I> implementation);

            void end(ComponentFactory<BE, C> factory);
        }
    }

    public interface Chunk {
        <C extends Component> void register(
                ComponentKey<C> key,
                ComponentFactory<ChunkAccess, ? extends C> factory
        );

        <C extends Component> void register(
                ComponentKey<? super C> key,
                Class<C> implementation,
                ComponentFactory<ChunkAccess, ? extends C> factory
        );
    }

    public interface Entity {
        <C extends Component, E extends net.minecraft.world.entity.Entity> void registerFor(
                Class<E> target,
                ComponentKey<C> key,
                ComponentFactory<E, ? extends C> factory
        );

        <C extends Component, E extends net.minecraft.world.entity.Entity> Entity.Registration<C, E> beginRegistration(
                Class<E> target,
                ComponentKey<C> key
        );

        <C extends RespawnableComponent<? super C>> void registerForPlayers(
                ComponentKey<? super C> key,
                ComponentFactory<Player, C> factory
        );

        <C extends Component, P extends C> void registerForPlayers(
                ComponentKey<C> key,
                ComponentFactory<Player, P> factory,
                RespawnCopyStrategy<? super P> strategy
        );

        interface Registration<C extends Component, E extends net.minecraft.world.entity.Entity> {
            Registration<C, E> filter(Predicate<Class<? extends E>> filter);

            @ApiStatus.Experimental
            Registration<C, E> after(ComponentKey<?> dependency);

            <I extends C> Registration<I, E> impl(Class<I> implementation);

            Registration<C, E> respawnStrategy(RespawnCopyStrategy<? super C> strategy);

            void end(ComponentFactory<E, C> factory);
        }
    }

    public interface Level {
        <C extends Component> void register(
                ComponentKey<C> key,
                ComponentFactory<LevelData, ? extends C> factory
        );

        <C extends Component> void register(
                ComponentKey<? super C> key,
                Class<C> implementation,
                ComponentFactory<LevelData, ? extends C> factory
        );
    }

    public interface Scoreboard {
        <C extends Component> void registerTeamComponent(
                ComponentKey<C> key,
                TeamFactory<? extends C> factory
        );

        <C extends Component> void registerTeamComponent(
                ComponentKey<? super C> key,
                Class<C> implementation,
                TeamFactory<? extends C> factory
        );

        <C extends Component> void registerScoreboardComponent(
                ComponentKey<C> key,
                ScoreboardFactory<? extends C> factory
        );

        <C extends Component> void registerScoreboardComponent(
                ComponentKey<? super C> key,
                Class<C> implementation,
                ScoreboardFactory<? extends C> factory
        );
    }

    public interface World {
        <C extends Component> void register(
                ComponentKey<C> key,
                ComponentFactory<net.minecraft.world.level.Level, ? extends C> factory
        );

        <C extends Component> void register(
                ComponentKey<? super C> key,
                Class<C> implementation,
                ComponentFactory<net.minecraft.world.level.Level, ? extends C> factory
        );

        <C extends Component> void registerFor(
                ResourceKey<net.minecraft.world.level.Level> dimension,
                ComponentKey<C> key,
                ComponentFactory<net.minecraft.world.level.Level, ? extends C> factory
        );

        <C extends Component> void registerFor(
                ResourceKey<net.minecraft.world.level.Level> dimension,
                ComponentKey<? super C> key,
                Class<C> implementation,
                ComponentFactory<net.minecraft.world.level.Level, ? extends C> factory
        );
    }
}
