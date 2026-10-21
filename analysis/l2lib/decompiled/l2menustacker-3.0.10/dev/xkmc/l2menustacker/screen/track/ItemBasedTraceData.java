package dev.xkmc.l2menustacker.screen.track;

import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import net.minecraft.world.item.Item;

public record ItemBasedTraceData(PlayerSlot<?> parent, Item verifier) implements TrackedEntryData<ItemBasedTraceData> {
}
