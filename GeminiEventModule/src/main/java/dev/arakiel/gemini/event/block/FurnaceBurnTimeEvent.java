package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import java.util.Objects;

public class FurnaceBurnTimeEvent extends GeminiEvent {
    private final AbstractFurnaceBlockEntity furnace;
    private final ItemStack fuel;
    private int burnTime;

    public FurnaceBurnTimeEvent(AbstractFurnaceBlockEntity furnace, ItemStack fuel, int burnTime) {
        this.furnace = Objects.requireNonNull(furnace, "furnace");
        this.fuel = Objects.requireNonNull(fuel, "fuel");
        this.burnTime = burnTime;
    }

    public AbstractFurnaceBlockEntity getFurnace() {
        return furnace;
    }

    public ItemStack getFuel() {
        return fuel;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }
}
