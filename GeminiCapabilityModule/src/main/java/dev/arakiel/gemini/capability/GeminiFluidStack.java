package dev.arakiel.gemini.capability;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public record GeminiFluidStack(Holder<Fluid> fluid, long amount, DataComponentPatch components) {
    public GeminiFluidStack {
        Objects.requireNonNull(fluid, "fluid");
        Objects.requireNonNull(components, "components");
        if (amount < 0) {
            throw new IllegalArgumentException("Fluid amount cannot be negative");
        }
    }

    public static GeminiFluidStack empty() {
        return new GeminiFluidStack(
                net.minecraft.core.registries.BuiltInRegistries.FLUID.wrapAsHolder(
                        net.minecraft.world.level.material.Fluids.EMPTY
                ),
                0,
                DataComponentPatch.EMPTY
        );
    }

    public boolean isEmpty() {
        return this.amount == 0 || this.fluid.value() == net.minecraft.world.level.material.Fluids.EMPTY;
    }

    public GeminiFluidStack withAmount(long amount) {
        return new GeminiFluidStack(this.fluid, amount, this.components);
    }

    public GeminiFluidStack copy() {
        return new GeminiFluidStack(this.fluid, this.amount, this.components);
    }
}
