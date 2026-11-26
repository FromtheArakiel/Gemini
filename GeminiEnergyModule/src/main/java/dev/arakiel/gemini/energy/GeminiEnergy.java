package dev.arakiel.gemini.energy;

import dev.arakiel.gemini.capability.BlockCapabilityContext;
import dev.arakiel.gemini.capability.EntityCapabilityContext;
import dev.arakiel.gemini.capability.EnergyStorageView;
import dev.arakiel.gemini.capability.GeminiCapabilities;
import dev.arakiel.gemini.capability.ItemCapabilityContext;
import dev.arakiel.gemini.capability.LevelCapabilityContext;
import dev.arakiel.gemini.energy.internal.EnergyPushEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public final class GeminiEnergy {
    public static final long TE_TO_GE = 1L;
    public static final long GE_TO_TE = 1L;

    private GeminiEnergy() {
    }

    public static Optional<EnergyStorageView> find(Level level) {
        return GeminiCapabilities.registry().find(
                GeminiCapabilities.ENERGY,
                new LevelCapabilityContext(level)
        );
    }

    public static Optional<EnergyStorageView> findBlock(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable BlockEntity blockEntity,
            @Nullable Direction direction
    ) {
        return GeminiCapabilities.registry().find(
                GeminiCapabilities.ENERGY,
                new BlockCapabilityContext(level, pos, state, blockEntity, direction)
        );
    }

    public static Optional<EnergyStorageView> findEntity(Entity entity, @Nullable Direction direction) {
        return GeminiCapabilities.registry().find(
                GeminiCapabilities.ENERGY,
                new EntityCapabilityContext(entity, direction)
        );
    }

    public static Optional<EnergyStorageView> findItem(
            ItemStack stack,
            @Nullable LivingEntity holder
    ) {
        return GeminiCapabilities.registry().find(
                GeminiCapabilities.ENERGY,
                new ItemCapabilityContext(stack, holder, null)
        );
    }

    public static EnergyPushResult push(
            EnergyStorageView source,
            Collection<? extends EnergyStorageView> endpoints,
            long maxAmount,
            boolean simulate
    ) {
        return EnergyPushEngine.push(source, endpoints, maxAmount, simulate);
    }

    public static EnergyPushResult pushFrom(
            Level level,
            BlockPos sourcePos,
            @Nullable Direction sourceDirection,
            long maxAmount,
            boolean simulate
    ) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(sourcePos, "sourcePos");
        return EnergyPushEngine.pushFrom(level, sourcePos, sourceDirection, maxAmount, simulate);
    }
}
