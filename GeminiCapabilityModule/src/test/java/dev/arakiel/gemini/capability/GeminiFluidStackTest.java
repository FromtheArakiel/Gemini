package dev.arakiel.gemini.capability;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.material.Fluids;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiFluidStackTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void emptyStackHasNoAmount() {
        assertTrue(GeminiFluidStack.empty().isEmpty());
        assertEquals(0, GeminiFluidStack.empty().amount());
    }

    @Test
    void amountCanChangeWithoutLosingComponents() {
        GeminiFluidStack original = new GeminiFluidStack(
                BuiltInRegistries.FLUID.wrapAsHolder(Fluids.WATER),
                100,
                DataComponentPatch.EMPTY
        );
        GeminiFluidStack changed = original.withAmount(250);

        assertEquals(250, changed.amount());
        assertEquals(original.components(), changed.components());
        assertEquals(original.fluid(), changed.fluid());
    }

    @Test
    void negativeAmountIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new GeminiFluidStack(
                        BuiltInRegistries.FLUID.wrapAsHolder(Fluids.WATER),
                        -1,
                        DataComponentPatch.EMPTY
                )
        );
    }
}
