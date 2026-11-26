package dev.arakiel.gemini.event.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FurnaceFuelBurnTimeEvent extends FurnaceBurnTimeEvent {
    public FurnaceFuelBurnTimeEvent(AbstractFurnaceBlockEntity furnace, ItemStack fuel, int burnTime) {
        super(furnace, fuel, burnTime);
    }
}
