package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@Cancelable
public class ItemStackedOnOtherEvent extends GeminiEvent {
    private final ItemStack carried;
    private final ItemStack stackedOn;
    private final Slot slot;
    private final ClickAction action;
    private final AbstractContainerMenu menu;

    public ItemStackedOnOtherEvent(
            ItemStack carried,
            ItemStack stackedOn,
            Slot slot,
            ClickAction action,
            AbstractContainerMenu menu
    ) {
        this.carried = Objects.requireNonNull(carried, "carried");
        this.stackedOn = Objects.requireNonNull(stackedOn, "stackedOn");
        this.slot = Objects.requireNonNull(slot, "slot");
        this.action = Objects.requireNonNull(action, "action");
        this.menu = Objects.requireNonNull(menu, "menu");
    }

    public ItemStack getCarried() {
        return this.carried;
    }

    public ItemStack getStackedOn() {
        return this.stackedOn;
    }

    public Slot getSlot() {
        return this.slot;
    }

    public ClickAction getAction() {
        return this.action;
    }

    public AbstractContainerMenu getMenu() {
        return this.menu;
    }
}
