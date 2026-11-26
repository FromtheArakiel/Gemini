package dev.arakiel.gemini.energy;

import net.minecraft.core.Direction;

public interface EnergyConduitView {
    boolean connects(Direction incomingSide, Direction outgoingSide);

    default long throughput(Direction incomingSide, Direction outgoingSide) {
        return Long.MAX_VALUE;
    }
}
