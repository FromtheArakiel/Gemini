package dev.arakiel.gemini.event;

import dev.arakiel.gemini.event.client.RenderGuiLayerEvent;
import dev.arakiel.gemini.event.client.RenderLevelStageEvent;
import dev.arakiel.gemini.event.client.RenderTooltipEvent;
import dev.arakiel.gemini.event.combat.CriticalHitEvent;
import dev.arakiel.gemini.event.combat.SweepAttackEvent;
import dev.arakiel.gemini.event.enchant.GrindstoneEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiEventSemanticsTest {
    @Test
    void combatCancellationMatchesNeoForgeContracts() {
        assertFalse(CriticalHitEvent.class.isAnnotationPresent(Cancelable.class));
        assertTrue(SweepAttackEvent.class.isAnnotationPresent(Cancelable.class));
    }

    @Test
    void renderCancellationMatchesNeoForgeContracts() {
        assertFalse(RenderLevelStageEvent.class.isAnnotationPresent(Cancelable.class));
        assertTrue(RenderGuiLayerEvent.Pre.class.isAnnotationPresent(Cancelable.class));
        assertFalse(RenderGuiLayerEvent.Post.class.isAnnotationPresent(Cancelable.class));
        assertFalse(RenderTooltipEvent.Color.class.isAnnotationPresent(Cancelable.class));
    }

    @Test
    void grindstonePlaceAndTakeEventsExposeWritableState() throws NoSuchMethodException {
        assertTrue(GrindstoneEvent.OnPlaceItem.class.isAnnotationPresent(Cancelable.class));
        assertTrue(GrindstoneEvent.OnTakeItem.class.isAnnotationPresent(Cancelable.class));
        GrindstoneEvent.OnPlaceItem.class.getMethod("setOutput", net.minecraft.world.item.ItemStack.class);
        GrindstoneEvent.OnTakeItem.class.getMethod("setNewTopItem", net.minecraft.world.item.ItemStack.class);
        GrindstoneEvent.OnTakeItem.class.getMethod("setNewBottomItem", net.minecraft.world.item.ItemStack.class);
        GrindstoneEvent.class.getMethod("setXp", int.class);
    }

    @Test
    void tooltipGatherEventExposesMutableWidth() throws NoSuchMethodException {
        assertTrue(RenderTooltipEvent.GatherComponents.class.isAnnotationPresent(Cancelable.class));
        RenderTooltipEvent.GatherComponents.class.getMethod("setMaxWidth", int.class);
        RenderTooltipEvent.Pre.class.getMethod("setX", int.class);
        RenderTooltipEvent.Pre.class.getMethod("setFont", net.minecraft.client.gui.Font.class);
    }
}
