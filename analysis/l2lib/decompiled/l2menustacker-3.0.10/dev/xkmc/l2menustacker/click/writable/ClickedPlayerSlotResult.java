package dev.xkmc.l2menustacker.click.writable;

import dev.xkmc.l2menustacker.screen.source.PlayerSlot;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;

public record ClickedPlayerSlotResult(ItemStack stack, @Nonnull PlayerSlot<?> slot, ContainerCallback container) {
}
